package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.EMPTY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.CHANGESET_ALIGNMENT;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.CHANGESET_EMPTY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.CHANGESET_PROD;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.CHANGESET_STAGING;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.CLEAN;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.PROCESSING;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.RESET;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.STAGING;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.SWAP;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.SYNC;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.VERIFY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryOnException;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.core.ParameterizedTypeReference;

import com.mongodb.client.MongoCollection;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionBuilderEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IDocumentHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IExecutableEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public abstract class ExecutorEDS<T> implements IDocumentHandlerEDS<T>, IExecutableEDS {

    private ChangesetCFG config;
    private BridgeEDS bridge;
    private ChangeSetDTO<T> changeset;
    private MongoCollection<Document> collection;
    private ProcessResult operations;
    private Map<String, IActionStepEDS> mappers;
    private LoggerHelper loggerHelper;

    protected ExecutorEDS(ChangesetCFG config, BridgeEDS bridge, LoggerHelper logger) {
        this.config = config;
        this.bridge = bridge;
        this.mappers = createDefaultMapper();
        loggerHelper = logger;
    }

    // TO IMPLEMENT BY THE CALLER
    protected abstract ParameterizedTypeReference<ChangeSetDTO<T>> getType();
    protected String[] getSteps() {
        return ActionEDS.defaults();
    }

    // HANDLERs
    @Override
    public ActionRes execute() {

        final Date startingDate = new Date();
        log.debug("[{}] Starting updating process", config.getTitle());

        // Register additional handlers if necessary
        if(!getCustomSteps().isEmpty()) registerAdditionalHandlers();

        ActionBuilderEDS builder = ActionBuilderEDS.builder();
        String[] steps = getSteps();

        // Add to builder
        for(String step: steps) {
            // Verify handler is available
            if(!mappers.containsKey(step)) {
                throw new IllegalArgumentException("Unregistered handler: " + step);
            }
            // Add
            builder.step(step, mappers.get(step));
        }
        // Execute
        ActionRes res = builder.execute((name, status) -> {
            if (status == ActionRes.KO)  {
                loggerHelper.error("Error while updating GTW configuration items", config.getTitle() + " - " + name, ResultLogEnum.KO, startingDate);
            }
        });

        if (res == ActionRes.OK) {
            loggerHelper.info("Successfully updated configuration items", "Update" + " - " + config.getTitle(), ResultLogEnum.OK, startingDate);
        }
        
        // Log me
        log.debug("[{}] Ending updating process", config.getTitle());
        // Bye
        return res;
    }

    // === MAPPER ===
    private Map<String, IActionStepEDS> createDefaultMapper() {
        // Working var
        Map<String, IActionStepEDS> mapper = new HashMap<>();
        // Identify and handle
        mapper.put(RESET, this::onReset);
        mapper.put(CLEAN, this::onClean);
        mapper.put(CHANGESET_PROD, this::onChangesetProd);
        mapper.put(CHANGESET_EMPTY, this::onChangesetEmpty);
        mapper.put(STAGING, this::onStaging);
        mapper.put(PROCESSING, this::onProcessing);
        mapper.put(VERIFY, this::onVerify);
        mapper.put(SYNC, this::onSync);
        mapper.put(CHANGESET_STAGING, this::onChangesetStaging);
        mapper.put(CHANGESET_ALIGNMENT, this::onChangesetAlignment);
        mapper.put(SWAP, this::onSwap);

        return mapper;
    }

    protected void registerAdditionalHandlers() {
        // Log me
        log.debug("[EDS][{}] Registering additional handlers {}", config.getTitle(),
            getCustomSteps()
                .stream()
                .map(Entry::getKey)
                .collect(Collectors.toList())
        );
        // Register additional handlers
        for (Entry<String, IActionStepEDS> step : getCustomSteps()) {
            this.mappers.putIfAbsent(step.getKey(), step.getValue());
        }
    }

    protected List<Entry<String, IActionStepEDS>> getCustomSteps() {
        return new ArrayList<>();
    }
    // === STEPS ===
    protected IActionFnEDS<Date> onLastUpdateProd() {
        return () -> bridge.getRepository().getLastSync(config.getProduction());
    }
    protected IActionFnEDS<Date> onLastUpdateStaging() {
        return () -> bridge.getRepository().getLastSync(config.getStaging());
    }
    protected ActionRes onChangesetProd() {
        return onChangeset(this.onLastUpdateProd());
    }
    protected ActionRes onChangesetStaging() {
        return onChangeset(this.onLastUpdateStaging());
    }
    protected ActionRes onChangeset(IActionFnEDS<Date> hnd) {
        Optional<ChangeSetDTO<T>> data;
        // Log me
        log.debug("[{}] Retrieving changeset", config.getTitle());
        // Retrieve HTTP request data
        data = retryOnException(() -> bridge.getClient().getStatus(config, hnd.get(), getType()), config, log);
        // Set the flag
        ActionRes res = data.isPresent() ? OK : KO;
        if(data.isPresent()) {
            this.changeset = data.get();
            log.debug(
                "[{}] Changeset retrieved with last update at: {}",
                config.getTitle(),
                config.getLastUpdateFormatted(changeset.getLastUpdate())
            );
        }
        return res;
    }
    protected ActionRes onChangesetEmpty() {
        ActionRes res = OK;
        if (this.changeset.getTotalNumberOfElements() == 0) {
            // Set the flag
            res = EMPTY;
            // Log me
            log.debug("[{}] Changeset is empty, quitting ...", config.getTitle());
        }
        return res;
    }
    protected ActionRes onStaging() {
        ActionRes res = KO;
        try {
            if(bridge.getRepository().exists(config.getProduction())) {
                res = cloneStaging();
            } else {
                res = emptyStaging();
            }
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify if production branch exists", config.getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes onProcessing() {
        ActionRes res = KO;
        log.debug("[{}] Start processing data on staging", config.getTitle());
        // Gives handler their data
        try {
            operations = new ProcessResult(
                // Execute insertions on staging
                onInsertionProcessing(collection, changeset),
                // Execute deletions on staging
                onDeletionsProcessing(collection, changeset),
                // Expected insertions
                changeset.getInsertions().size(),
                // Expected deletions
                changeset.getDeletions().size()
            );
            res = OK;
            log.debug("[{}] Operations have been applied on staging", config.getTitle());
        }catch (Exception ex){
            log.error(
                format("[%s] Unable to process collection due to unknown error", config.getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes onVerify() {
        log.debug("[{}] Verifying staging matches checksum", config.getTitle());
        // Verify
        ActionRes res = operations.isValid() ? OK : KO;
        if(res == OK) {
            log.debug("[{}] Verification success", config.getTitle());
        } else {
            log.warn("[{}] Verification failure", config.getTitle());
        }
        log.debug("[{}] Expecting {}", config.getTitle(), operations.getExpectedInfo());
        log.debug("[{}] Got {}", config.getTitle(), operations.getInfo());
        return res;
    }
    protected ActionRes onSwap() {
        ActionRes res = KO;
        log.debug("[{}] Setting up the production branch", config.getTitle());
        // Rename
        try {
            // Execute
            bridge.getRepository().rename(this.collection, config.getProduction());
            // Set flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to rename collection", config.getTitle()),
                ex
            );
        }
        log.debug("[{}] Finishing production branch setup", config.getTitle());
        return res;
    }
    protected ActionRes onSync() {
        ActionRes res = KO;
        log.debug("[{}] Syncing documents at {}",
            config.getTitle(),
            config.getLastUpdateFormatted(changeset.getTimestamp())
        );
        try {
            // Sync collection
            bridge.getRepository().sync(config.getStaging(), changeset.getTimestamp());
            // Set the flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to sync collection", config.getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes onChangesetAlignment() {
        ActionRes res = KO;
        // Verify return condition
        if (this.changeset.getTotalNumberOfElements() == 0) {
            res = OK;
            log.debug("[{}] Changeset is empty, data is aligned", config.getTitle());
        }
        
        return res;
    }
    protected ActionRes onClean() {
        ActionRes res = KO;
        // Drop previous staging collection if exists
        log.debug("[{}] Dropping previous branch for staging if exists", config.getTitle());
        try {
            // Drop it
            bridge.getRepository().drop(config.getStaging());
            // Set flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to drop previous staging collection", config.getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes onReset() {
        this.changeset = null;
        this.collection = null;
        this.operations = null;
        return ActionRes.OK;
    }
    // === ACTIONS ===
    protected ActionRes emptyStaging() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Database seems empty, using new collection as staging", config.getTitle());
        try {
            // Assign the collection
            collection = bridge.getRepository().create(config.getStaging());
            // Set the flag
            res = OK;
            
            log.debug("[{}] Staging branch ready", config.getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to create collection", config.getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }
    protected ActionRes cloneStaging() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Cloning current branch for staging", config.getTitle());
        // Let's clone
        try {
            // Assign the collection
            collection = bridge.getRepository().clone(config.getProduction(), config.getStaging());
            // Set flag
            res = OK;
            log.debug("[{}] Staging branch ready", config.getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to duplicate collection", config.getTitle()),
                ex
            );
        }
        return res;
    }
}
