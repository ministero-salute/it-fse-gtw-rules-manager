/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryDeleteDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryInsertDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityResultDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.TerminologyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.IChunkHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.ISnapshotHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.utils.EmptySetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IDictionarySRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ITerminologySRV;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Updates.set;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryOnException;
import static java.lang.String.format;


@Slf4j
@Component
@Primary
@Setter
@Getter
public class TermsExecutor extends ExecutorEDS<EmptySetDTO> implements ISnapshotHandlerEDS {


    @Autowired
    private TerminologyQuery query;

    @Autowired
    private IDictionarySRV csv;
    
    @Autowired
    private ITerminologySRV service;

    private ChangeSetChunkDTO snapshot;

    protected TermsExecutor(TerminologyCFG config, BridgeEDS bridge) {
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
        data = retryOnException(() -> getBridge().getClient().getStatusByClass(getConfigAsChunked(), hnd.get(), ChangeSetChunkDTO.class), getConfig(), log);        // Set the flag
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
        ActionRes res;
        // Print stats (collection size)
        statsSize(false);
        // Verify emptiness
        if (this.snapshot.isEmpty()) {
            // Log me
            log.debug("[{}] Changeset is empty", getConfig().getTitle());
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
    @Override
    protected ActionRes onProcessing() {
        ActionRes res = KO;
        log.debug("[{}] Start processing data on staging", getConfig().getTitle());
        // Gives handler their data
        try {
            setOperations(new ProcessResult(
                // Execute insertions on staging
                onResourceInsert(getCollection(), snapshot),
                // Execute deletions on staging
                onResourceDelete(getCollection(), snapshot),
                // Expected insertions
                getSnapshot().getInsertions().size(),
                // Expected deletions
                getSnapshot().getDeletions().size()
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
    protected ActionRes onIndexing() {
        ActionRes res = KO;
        log.debug("[{}] Applying indexes on staging", getConfig().getTitle());
        try {
            // Execute
            service.applyIndexes(getConfig().getStaging());
            // Set flag
            res = OK;
        } catch (EdsDbException e) {
            log.error(
                format("[%s] Unable to apply indexes on staging", getConfig().getTitle()),
                e
            );
        }
        return res;
    }
    @Override
    public IChunkHandlerEDS<HistoryInsertDTO> onChunkInsertion() {
        return (staging, data) -> {
            // Log me
            log.debug("[{}][Insert] Retrieving resource {}", getConfig().getTitle(), data);
            // Working var
            int process = 0;
            // Working var
            Optional<TerminologyDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> getBridge().getClient().getResource(
                getConfigAsChunked(),
                data.getId(),
                data.getVersion(),
                TerminologyDTO.class
                ), getConfig(), log
            );
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Insert & Consume
                    process = consume(staging, dto.get());
                }catch (MongoException | EdsClientException ex) {
                    log.error(
                        format("[EDS][%s] Unable to insert chunked resource", getConfig().getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            } else {
                log.error("[EDS][{}] Unable to retrieve chunked resource for insertion", getConfig().getTitle());
            }
            return new SimpleImmutableEntry<>(res, process);
        };
    }
    @Override
    public IChunkHandlerEDS<HistoryDeleteDTO> onChunkDeletions() {
        return (staging, data) -> {
            ActionRes res = OK;
            int process = 0;
            log.debug("[{}][Delete] Deleting resource {}", getConfig().getTitle(), data);
            try {
                // Execute
                staging.updateMany(query.getDeleteQuery(data), set("deleted", true));
                // Update
                process = 1;
            }catch (MongoException ex) {
                log.error(
                    format("[EDS][%s] Unable to delete chunk documents", getConfig().getTitle()),
                    ex
                );
                // Set flag
                res = KO;
            }
            return new SimpleImmutableEntry<>(res, process);
        };
    }

    @Override
    protected ActionRes onVerify() {
        log.debug("[{}] Verifying staging matches checksum", getConfig().getTitle());
        // Verify
        ActionRes res = getOperations().isValid() ? OK : KO;
        log.debug("[{}] Expecting {} | Got {}", getConfig().getTitle(), getOperations().getExpectedInfo(), getOperations().getInfo());
        if(res == OK) {
            log.debug("[{}] Verification success", getConfig().getTitle());
            // Now check size
            res = onVerifyStagingSize();
        } else {
            log.warn("[{}] Verification failure", getConfig().getTitle());
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
        if (getSnapshot().isEmpty()) {
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
    protected ActionRes onVerifyStagingSize() {
        ActionRes res = KO;
        log.debug("[{}] Verifying staging matches size", getConfig().getTitle());
        try {
            Optional<IntegrityDTO> integrity = retryOnException(
                () -> getBridge().getClient().getIntegrity(getConfigAsChunked(), IntegrityDTO.class),
                getConfig(),
                log
            );
            if(integrity.isPresent()) {
                // Check if synced
                IntegrityResultDTO matches = service.matches(integrity.get(), getConfig().getStaging());
                // Verify match
                if (matches.isSynced()) {
                    log.debug("[{}] Verification success", getConfig().getTitle());
                    // Set flag
                    res = OK;
                } else {
                    log.warn("[{}] Verification failure", getConfig().getTitle());
                }
                matches.getMissing().forEach(
                    raw -> log.debug("[{}] Missing resource from system: {}", getConfig().getTitle(), raw.info())
                );
            } else {
                log.error("[{}] Unable to retrieve checksum from EDS", getConfig().getTitle());
            }
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify if staging collection matches checksum", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    @Override
    protected ActionRes onVerifyProductionSize() {
        ActionRes res = KO;
        log.debug("[{}] Verifying production matches size", getConfig().getTitle());
        try {
            Optional<IntegrityDTO> integrity = retryOnException(
                () -> getBridge().getClient().getIntegrity(getConfigAsChunked(), IntegrityDTO.class),
                getConfig(),
                log
            );
            if(integrity.isPresent()) {
                // Check if synced
                IntegrityResultDTO matches = service.matches(integrity.get(), getConfig().getProduction());
                // Verify match
                if (matches.isSynced()) {
                    log.debug("[{}] Verification success", getConfig().getTitle());
                    // Set flag
                    res = EXIT;
                } else {
                    log.warn("[{}] Verification failure", getConfig().getTitle());
                }
                matches.getMissing().forEach(
                    raw -> log.debug("[{}] Missing resource from system: {}", getConfig().getTitle(), raw.info())
                );
            } else {
                log.error("[{}] Unable to retrieve checksum from EDS", getConfig().getTitle());
            }
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify if production collection matches checksum", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    // === STATS ===
    @Override
    protected void statsSize(boolean synchronised) {
        try {
            // Retrieve current production collection size
            long size = service.countActiveResources(getConfig().getProduction());
            // Display current and remote collection size
            log.info("[{}][Stats] Displaying sizes {} elaboration",
                getConfig().getTitle(),
                synchronised ? "after": "before"
            );
            log.info("[{}][Stats][Size] Resources: {}", getConfig().getTitle(), size);
        } catch (EdsDbException e) {
            log.warn(
                format("[%s] Unable to retrieve production size for inspection", getConfig().getTitle()),
                e
            );
        }
    }

    @Override
    protected void statsOps(boolean toApply) {
        if(toApply) {
            log.info("[{}][Stats][Ops] {}", getConfig().getTitle(), ProcessResult.info(snapshot));
        } else {
            log.info("[{}][Stats][Ops] No operations to apply", getConfig().getTitle());
        }
    }

    private int consume(MongoCollection<Document> staging, TerminologyDTO term) throws EdsClientException {
        while(term != null) {
            // Retrieve docs
            List<Document> docs = query.getUpsertQuery(term);
            // Note: We need to check docs.size() because insertMany() does not allow empty lists
            if (!docs.isEmpty()) {
                staging.insertMany(docs);
            } else {
                log.debug("[{}][Insert] Skipping empty chunk for {}", getConfig().getTitle(), term.info());
            }
            // Retrieve next url
            String next = term.getLinks().getNext();
            // Check if we need to keep going
            if(next != null) {
                Optional<TerminologyDTO> nextTerm = retryOnException(
                    () -> getBridge().getClient().getNextResource(next, TerminologyDTO.class),
                    getConfig(), log
                );
                if(!nextTerm.isPresent()) {
                    throw new EdsClientException("Unable to retrieve next chunk for " + term.info(), null);
                } else {
                    term = nextTerm.get();
                }
            } else {
                term = null;
            }
        }
        return 1;
    }
}
