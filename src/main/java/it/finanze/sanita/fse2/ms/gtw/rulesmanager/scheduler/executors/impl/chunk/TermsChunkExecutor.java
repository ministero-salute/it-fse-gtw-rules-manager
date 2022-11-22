/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk;

import com.mongodb.MongoException;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.chunk.TerminologyChunkedCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.chunks.TerminologyChunkDelDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.chunks.TerminologyChunkInsDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.IChunkHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.ISnapshotHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.TermActionEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.base.EmptySetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICodeSystemVersionSRV;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import static com.mongodb.client.model.Updates.set;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
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
    private ICodeSystemVersionSRV codeSystemVersionSRV;

    private ChangeSetChunkDTO snapshot;

    protected TermsChunkExecutor(TerminologyChunkedCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    protected ChunkChangesetCFG getConfigAsChunked() {
        return (ChunkChangesetCFG) getConfig();
    }

    @Override
    protected ActionRes onChangeset(IActionFnEDS<Date> hnd) {
        Optional<ChangeSetChunkDTO> data;
        // Log me
        log.debug("[{}] Retrieving changeset", getConfig().getTitle());
        // Retrieve HTTP request data
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
        ActionRes res = OK;
        if (this.snapshot.getTotalNumberOfElements() == 0) {
            // Set the flag
            res = EMPTY;
            // Log me
            log.debug("[{}] Changeset is empty, quitting ...", getConfig().getTitle());
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
                    // Insert
                    InsertManyResult status = staging.insertMany(query.getUpsertQueries(dto.get().getDocuments()));
                    // Calculate insertions
                    process = status.getInsertedIds().size();
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
                    UpdateResult result = staging.updateMany(query.getDeleteQueries(dto.get().getDocuments()), set("deleted", true)); 
                    // Calculate deletions
                    process = (int) result.getModifiedCount(); 
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

    protected ActionRes onCodeSystemSync() {
        ActionRes res = KO;
        try {
            // Execute syncing
            codeSystemVersionSRV.syncCodeSystemVersions();
            // Set flag
            res = OK;
        }catch (Exception ex) {
            log.error(
                format("[%s] Unable to sync code-system collection", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    @Override
    protected String[] getSteps() {
        return TermActionEDS.defaults();
    }

    @Override
    protected List<Entry<String, IActionStepEDS>> getCustomSteps() {
        // Create list
        List<Entry<String, IActionStepEDS>> hnd = new ArrayList<>();
        hnd.add(new SimpleImmutableEntry<>(
            TermActionEDS.ON_CS_SYNC, this::onCodeSystemSync
        ));
        // Add additional handlers
        return hnd;
    }
}
