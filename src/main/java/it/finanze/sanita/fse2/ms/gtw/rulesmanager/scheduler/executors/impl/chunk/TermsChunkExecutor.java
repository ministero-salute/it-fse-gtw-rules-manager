package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.chunk.TerminologyChunkedCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.ISnapshotHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.base.EmptySetDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

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
}
