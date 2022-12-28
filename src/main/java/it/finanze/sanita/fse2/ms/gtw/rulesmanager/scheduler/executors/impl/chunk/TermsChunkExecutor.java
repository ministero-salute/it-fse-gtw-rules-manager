/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk;

import com.mongodb.MongoException;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.chunk.TerminologyChunkedCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.chunks.TerminologyChunkDelDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.chunks.TerminologyChunkInsDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionCallbackEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.IChunkHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.ISnapshotHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.base.EmptySetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICodeSystemVersionSRV;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Updates.set;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryOnException;
import static java.lang.String.format;


@Slf4j
@Component
@Setter
@Getter
public class TermsChunkExecutor extends ExecutorEDS<EmptySetDTO> implements ISnapshotHandlerEDS {

    @Autowired
    private TerminologyQuery query;

    @Autowired
    private ICodeSystemVersionSRV csv;

    private ChangeSetChunkDTO snapshot;

    private IActionCallbackEDS onBeforeSwap;

    private IActionCallbackEDS onSuccessSwap;
    private IActionCallbackEDS onFailedSwap;

    protected TermsChunkExecutor(TerminologyChunkedCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    protected ChunkChangesetCFG getConfigAsChunked() {
        return (ChunkChangesetCFG) getConfig();
    }

    @Override
	public ActionRes onChangeset(IActionFnEDS<Date> hnd) {
        Optional<ChangeSetChunkDTO> data;
        // Log me
        log.debug("[{}] Retrieving changeset", getConfig().getTitle());
        // Retrieve HTTP request data
        ChunkChangesetCFG chunkCfg = getConfigAsChunked(); 
        ChangesetCFG cfg = getConfig(); 
        IEDSClient client = getBridge().getClient(); 
        try {
			ChangeSetChunkDTO resp = client.getSnapshot(getConfigAsChunked(), hnd.get(), ChangeSetChunkDTO.class);
		} catch (EdsClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        data = retryOnException(() -> getBridge().getClient().getSnapshot(getConfigAsChunked(), hnd.get(), ChangeSetChunkDTO.class), getConfig(), log);
        // Set the flag
        ActionRes res = data.isPresent() ? OK : KO;
        if(data.isPresent()) {
            this.snapshot = data.get();
            log.debug(
                "[{}] Changeset retrieved with last update at: {}",
                getConfig().getTitle(),
                getConfig().getLastUpdateFormatted(snapshot.getLastUpdate())
            );
        }
        return res;
    }

    @Override
    protected ActionRes onChangesetEmpty() {
        // Working var
        ActionRes res = KO;
        // Verify emptiness
        if (this.snapshot.getTotalNumberOfElements() == 0) {
            // Log me
            log.debug("[{}] Changeset is empty", getConfig().getTitle());
            try {
                log.debug("[{}] Verifying production matches size", getConfig().getTitle());
                // Retrieve current size
                long size = getBridge().getRepository().countActiveDocuments(getConfig().getProduction());
                // Verify match
                if(snapshot.getCollectionSize() == size) {
                    log.debug("[{}] Verification success", getConfig().getTitle());
                    // Set flag
                    res = EXIT;
                }else {
                    log.warn("[{}] Verification failure", getConfig().getTitle());
                }
                log.debug("[{}] Expecting {} | Got {}", getConfig().getTitle(), snapshot.getCollectionSize(), size);
            }catch (EdsDbException ex) {
                log.error(
                    format("[%s] Unable to verify if production matches expected size", getConfig().getTitle()),
                    ex
                );
            }
        }else {
            // Changeset is not empty
            res = OK;
        }
        return res;
    }

    @Override
    protected ActionRes onProcessing() {
        ActionRes res = KO;
        log.debug("[{}] Start processing data on staging", getConfig().getTitle());
        // Gives handler their data
        try {
            setOperations(new ProcessResult(
                // Execute insertions on staging
                onInsertionChunkProcessing(getCollection(), snapshot),
                // Execute deletions on staging
                onDeletionsChunkProcessing(getCollection(), snapshot),
                // Expected insertions
                getSnapshot().getChunks().getInsertions().getChunksItems(),
                // Expected deletions
                getSnapshot().getChunks().getDeletions().getChunksItems()
            ));
            res = OK;
            log.debug("[{}] Operations have been applied on staging", getConfig().getTitle());
        }catch (Exception ex){
            log.error(
                format("[%s] Unable to process collection due to unknown error", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    @Override
    public IChunkHandlerEDS onChunkInsertion() {
        return (staging, snapshot, chunk, max) -> {
            // Log me
            log.debug("[{}][Insert] Retrieving chunk {}/{}", getConfig().getTitle(), chunk + 1, max);
            // Working var
            int process = 0;
            // Working var
            Optional<TerminologyChunkInsDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> getBridge().getClient().getChunkIns(
                getConfigAsChunked(),
                snapshot,
                chunk,
                TerminologyChunkInsDTO.class
                ), getConfig(), log
            );
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Retrieve docs
                    List<Document> docs = query.getUpsertQueries(dto.get().getDocuments());
                    // Note: We need to check docs.size() because insertMany() does not allow empty lists
                    if(!docs.isEmpty()) {
                        // Insert
                        InsertManyResult status = staging.insertMany(docs);
                        // Calculate insertions
                        process = status.getInsertedIds().size();
                    } else {
                        log.debug("[{}][Insert] Skipping empty chunk with index {}", getConfig().getTitle(), chunk);
                    }
                }catch (MongoException ex) {
                    log.error(
                        format("[EDS][%s] Unable to insert chunk documents", getConfig().getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[EDS][{}] Unable to retrieve chunk document for insertion", getConfig().getTitle());
            }
            return new AbstractMap.SimpleImmutableEntry<>(res, process);
        };
    }

    @Override
    public IChunkHandlerEDS onChunkDeletions() {
        return (staging, snapshot, chunk, max) -> {
            // Log me
            log.debug("[{}][Delete] Retrieving chunk {}/{}", getConfig().getTitle(), chunk + 1, max);
            // Working var
            int process = 0;
            // Working var
            Optional<TerminologyChunkDelDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> getBridge().getClient().getChunkDel(
                    getConfigAsChunked(),
                    snapshot,
                    chunk,
                    TerminologyChunkDelDTO.class
                ), getConfig(), log
            );
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Delete docs if request didn't fail
            if(res == OK) {
                try {
                    // Retrieve docs
                    List<String> docs = dto.get().getDocuments();
                    // Note: We check docs.size() just to be sure updateMany() is not going to be an empty stmt
                    if(!docs.isEmpty()) {
                        // Execute
                        UpdateResult result = staging.updateMany(query.getDeleteQueries(docs), set("deleted", true));
                        // Calculate deletions
                        process = (int) result.getModifiedCount();
                    } else {
                        log.debug("[{}][Delete] Skipping empty chunk with index {}", getConfig().getTitle(), chunk);
                    }
                }catch (MongoException ex) {
                    log.error(
                        format("[EDS][%s] Unable to delete chunk documents", getConfig().getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[EDS][{}] Unable to retrieve chunk document for deletion", getConfig().getTitle());
            }
            return new AbstractMap.SimpleImmutableEntry<>(res, process);
        };
    }

    @Override
    protected ActionRes onVerifySize() {
        ActionRes res = KO;
        log.debug("[{}] Verifying staging matches size", getConfig().getTitle());
        try {
            // Retrieve current size (after performing operations)
            long size = getBridge().getRepository().countActiveDocuments(getCollection());
            // Verify match
            if(snapshot.getCollectionSize() == size) {
                log.debug("[{}] Verification success", getConfig().getTitle());
                // Set flag
                res = OK;
            }else {
                log.warn("[{}] Verification failure", getConfig().getTitle());
            }
            log.debug("[{}] Expecting {} | Got {}", getConfig().getTitle(), snapshot.getCollectionSize(), size);
        }catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify collection size", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    @Override
    protected ActionRes onSync() {
        ActionRes res = KO;
        log.debug("[{}] Syncing documents at {}",
            getConfig().getTitle(),
            getConfig().getLastUpdateFormatted(getSnapshot().getTimestamp())
        );
        try {
            // Sync collection
            getBridge().getRepository().sync(getConfig().getStaging(), getSnapshot().getTimestamp());
            // Set the flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to sync collection", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    @Override
    protected ActionRes onChangesetAlignment() {
        ActionRes res = KO;
        // Verify return condition
        if (getSnapshot().getTotalNumberOfElements() == 0) {
            res = OK;
            log.debug("[{}] Changeset is empty, data is aligned", getConfig().getTitle());
        }
        return res;
    }

    @Override
    protected ActionRes onReset() {
        setSnapshot(null);
        setCollection(null);
        setOperations(null);
        return ActionRes.OK;
    }

    @Override
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
                res = super.onSwap();
            } else {
                // Log me
                log.debug("[{}] Pre-swap operation failure", getConfig().getTitle());
                // Remove staging
                onClean();
            }
        }else{
            res = super.onSwap();
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
}
