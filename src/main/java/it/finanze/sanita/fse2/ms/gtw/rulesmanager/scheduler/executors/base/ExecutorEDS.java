/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionCallbackEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.PlanEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.listeners.OnPlanListener;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.listeners.OnStepListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.AppConstants.LOG_TYPE_CONTROL;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryOnException;
import static java.lang.String.format;

@Slf4j
@Getter
@Setter
public abstract class ExecutorEDS<T> implements IDocumentHandlerEDS<T>, IExecutableEDS, IRecoverableEDS {

    private ChangesetCFG config;
    private BridgeEDS bridge;
    private ChangeSetDTO<T> changeset;
    private MongoCollection<Document> collection;
    private ProcessResult operations;
    private Map<String, IActionStepEDS> mappers;

    private IActionCallbackEDS onBeforeSwap;

    private IActionCallbackEDS onSuccessSwap;
    private IActionCallbackEDS onFailedSwap;

    protected ExecutorEDS(ChangesetCFG config, BridgeEDS bridge) {
        this.config = config;
        this.bridge = bridge;
        this.mappers = createDefaultMapper();
    }

    protected String[] getSteps() {
        return ActionEDS.defaults();
    }

    protected String[] getRecoverySteps() {
        return ActionEDS.recovery();
    }

    // TO IMPLEMENT BY THE CALLER
    protected ParameterizedTypeReference<ChangeSetDTO<T>> getType() {
        throw new UnsupportedOperationException("getType() is not implemented!");
    }

    // HANDLERs
    @Override
    public ActionRes execute() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Starting updating process", config.getTitle());
        try {
            res = startup(getSteps());
        }catch (Exception ex) {
            log.error(
                format("[%s] Unable to properly quit execute() at runtime", config.getTitle()),
                ex
            );
        }
        log.debug("[{}] Ending updating process", config.getTitle());
        return res;
    }
    @Override
    public ActionRes recovery() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Starting recovery process", config.getTitle());
        try {
            res = startup(getRecoverySteps());
        }catch (Exception ex) {
            log.error(
                format("[%s] Unable to properly quit recovery() at runtime", config.getTitle()),
                ex
            );
        }
        log.debug("[{}] Ending recovery process", config.getTitle());
        return res;
    }

    protected ActionRes startup(String[] steps) {
        Date timestamp = new Date();
        // Register additional handlers if necessary
        if(!getCustomSteps().isEmpty()) registerAdditionalHandlers();
        // Add to builder
        PlanEDS plan = new PlanEDS();
        for(String step: steps) {
            // Verify handler is available
            if(!mappers.containsKey(step)) {
                throw new IllegalArgumentException("Unregistered handler: " + step);
            }
            // Add
            plan.addStep(step, mappers.get(step));
        }
        // Add listeners
        plan.addOnStepListener(this.onStepFailure(timestamp));
        plan.addOnPlanListener(this.onPlanSuccess(timestamp));
        // Execute
        return plan.execute();
    }

    // === LISTENERS ===
    protected OnStepListener onStepFailure(Date timestamp) {
        return (name, status) -> {
            if (status == ActionRes.KO)  {
                bridge.getLogger().error(LOG_TYPE_CONTROL, "Error while updating GTW configuration items", config.getTitle() + " - " + name, ResultLogEnum.KO, timestamp);
            }
        };
    }
    protected OnPlanListener onPlanSuccess(Date timestamp) {
        return (status) -> {
            if (status == ActionRes.OK)  {
                bridge.getLogger().info(LOG_TYPE_CONTROL, "Successfully updated configuration items", "Update" + " - " + config.getTitle(), ResultLogEnum.OK, timestamp);
            }
        };
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
        mapper.put(CHANGESET_RECOVERY, this::onChangesetRecovery);
        mapper.put(STAGING, this::onStaging);
        mapper.put(STAGING_RECOVERY, this::onStagingRecovery);
        mapper.put(PROCESSING, this::onProcessing);
        mapper.put(INDEXING, this::onIndexing);
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
        // Working var
        ActionRes res;
        // Print stats (collection size)
        statsSize(false);
        // Verify emptiness
        if (this.changeset.getTotalNumberOfElements() == 0) {
            // Log me
            log.debug("[{}] Changeset is empty", config.getTitle());
            // Now check size
            res = onVerifyProductionSize();
            // Print stats (operation to apply)
            if(res == EXIT) statsOps(false);
        }else {
            // Changeset is not empty
            res = OK;
            // Print stats (operation to apply)
            statsOps(true);
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
    protected ActionRes onIndexing() {
        log.debug("[{}] No indexes to apply on staging", config.getTitle());
        return OK;
    }
    protected ActionRes onVerify() {
        log.debug("[{}] Verifying staging matches checksum", config.getTitle());
        // Verify
        ActionRes res = operations.isValid() ? OK : KO;
        log.debug("[{}] Expecting {} | Got {}", config.getTitle(), operations.getExpectedInfo(), operations.getInfo());
        if(res == OK) {
            log.debug("[{}] Verification success", config.getTitle());
            // Now check size
            res = onVerifyStagingSize();
        } else {
            log.warn("[{}] Verification failure", config.getTitle());
        }
        return res;
    }
    protected ActionRes onSwap() {
        // Working var
        ActionRes res = KO;
        // Verify pre-callback has been provided
        if(onBeforeSwap != null) {
            // Log me
            log.debug("[{}] Executing pre-swap operation", getConfig().getTitle());
            // Execute callback
            CallbackRes cb = onBeforeSwap.execute();
            // Check if we can keep going
            if(cb == CB_OK) {
                // Log me
                log.debug("[{}] Pre-swap operation success", getConfig().getTitle());
                // Execute swapping
                res = this.swapStaging();
            } else {
                // Log me
                log.debug("[{}] Pre-swap operation failure", getConfig().getTitle());
                // Remove staging
                onClean();
            }
        }else{
            res = this.swapStaging();
        }
        // Verify post-callback has been provided
        if(onFailedSwap != null && res == KO) {
            // Log me
            log.debug("[{}] Executing on-fail-swap to recover data", getConfig().getTitle());
            // Execute recovery procedure
            CallbackRes cb = onFailedSwap.execute();
            // Check
            if(cb == CB_OK) {
                log.debug("[{}] Data successfully reverted", getConfig().getTitle());
            } else {
                log.debug("[{}] Unable to recover data", getConfig().getTitle());
                log.error("[{}][FATAL] Unable to recover data, integrity is compromised!", getConfig().getTitle());
            }
        }
        // Verify success callback has been provided
        // This listener is supposed to be side effect free and idempotent
        // If it fails but onBeforeSwap() or swap() returned OK we are not
        // modifying the operation result
        if(onSuccessSwap != null && res == OK) {
            // Log me
            log.debug("[{}] Executing on-success-swap operation", getConfig().getTitle());
            // Execute recovery procedure
            CallbackRes cb = onSuccessSwap.execute();
            // Check
            if(cb == CB_OK) {
                log.debug("[{}] Post-swap operation success", getConfig().getTitle());
            } else {
                log.debug("[{}] Post-swap operation failure", getConfig().getTitle());
            }
        }
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

    // === RECOVERY ===
    protected ActionRes onChangesetRecovery() {
        return onChangeset(() -> null);
    }
    protected ActionRes onStagingRecovery() {
        return emptyStaging();
    }

    // === ACTIONS ===
    private ActionRes swapStaging() {
        ActionRes res = KO;
        log.debug("[{}] Setting up the production branch", config.getTitle());
        // Rename
        try {
            // Execute
            bridge.getRepository().rename(this.collection, config.getProduction());
            // Set flag
            res = OK;
            // Display sizes after swapping
            statsSize(true);
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to rename collection", config.getTitle()),
                ex
            );
        }
        log.debug("[{}] Finishing production branch setup", config.getTitle());
        return res;
    }
    private ActionRes emptyStaging() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Creating new empty collection as staging", config.getTitle());
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
    private ActionRes cloneStaging() {
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

    protected ActionRes onVerifyProductionSize() {
        // Working var
        ActionRes res = KO;
        try {
            log.debug("[{}] Verifying production matches size", config.getTitle());
            // Retrieve current size
            long size = bridge.getRepository().countActiveDocuments(config.getProduction());
            // Verify match
            if(changeset.getCollectionSize() == size) {
                log.debug("[{}] Verification success", config.getTitle());
                // Set flag
                res = EXIT;
            }else {
                log.warn("[{}] Verification failure", config.getTitle());
            }
            log.debug("[{}] Expecting {} | Got {}", config.getTitle(), changeset.getCollectionSize(), size);
        }catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify if production matches expected size", config.getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes onVerifyStagingSize() {
        ActionRes res = KO;
        log.debug("[{}] Verifying staging matches size", config.getTitle());
        try {
            // Retrieve current size (after performing operations)
            long size = bridge.getRepository().countActiveDocuments(collection);
            // Verify match
            if(changeset.getCollectionSize() == size) {
                log.debug("[{}] Verification success", config.getTitle());
                // Set flag
                res = OK;
            }else {
                log.warn("[{}] Verification failure", config.getTitle());
            }
            log.debug("[{}] Expecting {} | Got {}", config.getTitle(), changeset.getCollectionSize(), size);
        }catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify collection size", config.getTitle()),
                ex
            );
        }
        return res;
    }

    // === STATS ===
    protected void statsSize(boolean synchronised) {
        try {
            // Retrieve current production collection size
            long size = bridge.getRepository().countActiveDocuments(config.getProduction());
            // Display current and remote collection size
            log.info("[{}][Stats] Displaying sizes {} elaboration",
                config.getTitle(),
                synchronised ? "after": "before"
            );
            log.info("[{}][Stats][Size] Local: {} | Remote: {}",
                config.getTitle(), size,
                changeset.getCollectionSize()
            );
        } catch (EdsDbException e) {
            log.warn(
                format("[%s] Unable to retrieve production size for inspection", config.getTitle()),
                e
            );
        }
    }
    protected void statsOps(boolean toApply) {
        if(toApply) {
            log.info("[{}][Stats][Ops] {}", config.getTitle(), ProcessResult.info(changeset));
        } else {
            log.info("[{}][Stats][Ops] No operations to apply", config.getTitle());
        }
    }

}
