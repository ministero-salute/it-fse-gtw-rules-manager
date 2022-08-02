package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClientV2;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangeSetSpecCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICollectionsRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionBuilderEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IDocumentHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Date;
import java.util.Optional;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static java.lang.String.format;

@Slf4j
@Getter
@Setter
public abstract class ExecutorEDS<T> implements IDocumentHandlerEDS<T>, IActionRetryEDS {

    private ICollectionsRepo repository;
    protected IEDSClientV2 client;
    protected ChangeSetSpecCFG config;
    private ChangeSetDTO<T> changeset;
    private MongoCollection<Document> collection;
    private ProcessResult operations;

    protected ExecutorEDS(ChangeSetSpecCFG config, ExecutorBridgeEDS bridge) {
        this.config = config;
        this.repository = bridge.getRepository();
        this.client = bridge.getClient();
    }

    // TO IMPLEMENT BY THE CALLER
    protected abstract ParameterizedTypeReference<ChangeSetDTO<T>> getType();

    // HANDLER
    public ActionRes execute() {
        // Log me
        log.info("[EDS][{}] Starting updating process", config.getTitle());
        // Executor process
        ActionRes res = ActionBuilderEDS
            // Create builder
            .builder()
            // Reset references
            .step(this::onReset)
            // Clean previous branch
            .step(this::onClean)
            // Acquire changeset
            .step(() -> onChangeset(this.onLastUpdateProd()))
            // Verify if empty to early quit
            .step(this::onChangesetEmpty)
            // Create staging collection
            .step(this::onStaging)
            // Start processing documents inside staging
            .step(this::onProcessing)
            // Verify we are good with data
            .step(this::onVerify)
            // Sync
            .step(this::onSync)
            // Acquire changeset (again)
            .step(() -> onChangeset(this.onLastUpdateStaging()))
            // Verify we are aligned with EDS now
            .step(this::onChangesetAlignment)
            // Swap the database instance
            .step(this::onSwap)
            // Start
            .execute();
        // Log me
        log.info("[EDS][{}] Ending updating process", config.getTitle());
        // Bye
        return res;
    }
    // === STEPS ===
    protected IActionFnEDS<Date> onLastUpdateProd() {
        return () -> repository.getLastSync(config.getProduction());
    }
    protected IActionFnEDS<Date> onLastUpdateStaging() {
        return () -> repository.getLastSync(config.getStaging());
    }
    protected ActionRes onChangeset(IActionFnEDS<Date> hnd) {
        // Working var
        Optional<ChangeSetDTO<T>> data;
        // Log me
        log.info("[EDS][{}] Retrieving changeset", config.getTitle());
        // Retrieve HTTP request data
        data = retryOnException(() -> client.getStatus(config, hnd.get(), getType()), config, log);
        // Set the flag
        ActionRes res = data.isPresent() ? OK : KO;
        // Let's go
        if(data.isPresent()) {
            // Assign
            this.changeset = data.get();
            // Log me
            log.info(
                "[EDS][{}] Changeset retrieved with last update at: {}",
                config.getTitle(),
                config.getLastUpdateFormatted(changeset.getLastUpdate())
            );
            // Log me
            log.info(
                "[EDS][{}] Changeset retrieved with timestamp at: {}",
                config.getTitle(),
                config.getLastUpdateFormatted(changeset.getTimestamp())
            );
        }
        return res;
    }
    protected ActionRes onChangesetEmpty() {
        // Working var
        ActionRes res = OK;
        // Verify return condition
        if (this.changeset.getTotalNumberOfElements() == 0) {
            // Set the flag
            res = RETURN;
            // Log me
            log.info("[EDS][{}] Changeset is empty, quitting ...", config.getTitle());
        }
        return res;
    }
    protected ActionRes onStaging() {
        // Working var
        ActionRes res = KO;
        // Verify if database is not empty
        try {
            if(repository.exists(config.getProduction())) {
                res = cloneStaging();
            } else {
                res = emptyStaging();
            }
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to verify if production branch exists", config.getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes onProcessing() {
        // Working var
        ActionRes res = KO;
        // Log me
        log.info("[EDS][{}] Start processing data on staging", config.getTitle());
        // Gives handler their data
        try {
            operations = new ProcessResult(
                // Execute insertions on staging
                onInsertionProcessing(collection, changeset),
                // Execute modifications on staging
                onModificationProcessing(collection, changeset),
                // Execute deletions on staging
                onDeletionsProcessing(collection, changeset),
                // Expected insertions
                changeset.getInsertions().size(),
                // Expected modifications
                changeset.getModifications().size(),
                // Expected deletions
                changeset.getDeletions().size()
            );
            // Set the flag
            res = OK;
            // Log me
            log.info("[EDS][{}] Operations have been applied on staging", config.getTitle());
        }catch (Exception ex){
            log.error(
                format("[EDS][%s] Unable to process collection due to unknown error", config.getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }
    protected ActionRes onVerify() {
        // Log me
        log.info("[EDS][{}] Verifying staging matches checksum", config.getTitle());
        // Verify
        ActionRes res = operations.isValid() ? OK : KO;
        // Logging
        if(res == OK) {
            // Log me
            log.info("[EDS][{}] Verification success", config.getTitle());
        }else {
            // Log me
            log.info("[EDS][{}] Verification failure", config.getTitle());
        }
        // Log me
        log.info("[EDS][{}] Expecting {}", config.getTitle(), operations.getExpectedInfo());
        log.info("[EDS][{}] Got {}", config.getTitle(), operations.getInfo());
        // Bye
        return res;
    }
    protected ActionRes onSwap() {
        // Working var
        ActionRes res = KO;
        // Log me
        log.info("[EDS][{}] Setting up the production branch", config.getTitle());
        // Rename
        try {
            // Execute
            repository.rename(this.collection, config.getProduction());
            // Set flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to rename collection", config.getTitle()),
                ex
            );
        }
        // Log me
        log.info("[EDS][{}] Finishing production branch setup", config.getTitle());
        // Bye
        return res;
    }
    protected ActionRes onSync() {
        // Working var
        ActionRes res = KO;
        // Log me
        log.info("[EDS][{}] Syncing documents at {}",
            config.getTitle(),
            config.getLastUpdateFormatted(changeset.getTimestamp())
        );
        try {
            // Sync collection
            repository.sync(config.getStaging(), changeset.getTimestamp());
            // Set the flag
            res = OK;
            // Log me
            log.info("[EDS][{}] Documents synchronised", config.getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to sync collection", config.getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }
    protected ActionRes onChangesetAlignment() {
        // Working var
        ActionRes res = KO;
        // Verify return condition
        if (this.changeset.getTotalNumberOfElements() == 0) {
            // Set the flag
            res = OK;
            // Log me
            log.info("[EDS][{}] Changeset is empty, data is aligned", config.getTitle());
        }else {
            // Log me
            log.info("[EDS][{}] Changeset is not empty, data is not aligned", config.getTitle());
        }
        return res;
    }
    protected ActionRes onClean() {
        // Working var
        ActionRes res = KO;
        // Drop previous staging collection if exists
        log.info("[EDS][{}] Dropping previous branch for staging if exists", config.getTitle());
        try {
            // Drop it
            repository.drop(config.getStaging());
            // Set flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to drop previous staging collection", config.getTitle()),
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
    private ActionRes emptyStaging() {
        // Working var
        ActionRes res = KO;
        // Log me
        log.info("[EDS][{}] Database seems empty, using new collection as staging", config.getTitle());
        try {
            // Assign the collection
            collection = repository.create(config.getStaging());
            // Set the flag
            res = OK;
            // Log me
            log.info("[EDS][{}] Staging branch ready", config.getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to create collection", config.getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }
    private ActionRes cloneStaging() {
        // Working var
        ActionRes res = KO;
        // Log me
        log.info("[EDS][{}] Cloning current branch for staging", config.getTitle());
        // Let's clone
        try {
            // Assign the collection
            collection = repository.clone(config.getProduction(), config.getStaging());
            // Set flag
            res = OK;
            // Log me
            log.info("[EDS][{}] Staging branch ready", config.getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to duplicate collection", config.getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }
}
