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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.base.ChunkStatsDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.base.ChunksDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.IChunkHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.TermsExecutor;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

@Component
public class MockTermsExecutor extends TermsExecutor {
	
	private boolean verify;

    protected MockTermsExecutor(TerminologyCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    public IActionFnEDS<Date> onLastUpdateProd() {
        return super.onLastUpdateProd();
    }
    
    public static ChangeSetChunkDTO createChangesetChunk(int insert, int delete, long size) {
        ChunksDTO chunks = new ChunksDTO();
        ChunkStatsDTO insertions = new ChunkStatsDTO();
        ChunkStatsDTO deletions = new ChunkStatsDTO();
        
        // 1287 13 chunk
        
        insertions.setChunksAvgSize(9);
        insertions.setChunksCount(insert);
        insertions.setChunksItems(insert);
        
        deletions.setChunksAvgSize(9);
        deletions.setChunksCount(delete);
        deletions.setChunksItems(delete);
             
        chunks.setInsertions(insertions);
        chunks.setDeletions(deletions);

        return new ChangeSetChunkDTO(
            "randomTraceId",
            "randomSpanId",
            new Date(),
            new Date(),
            chunks,
            insert + delete,
            size
        );
    }

    public static ChangeSetChunkDTO emptyChangesetChunk() {
    	ChunksDTO chunks = new ChunksDTO();
        ChunkStatsDTO insertions = new ChunkStatsDTO();
        ChunkStatsDTO deletions = new ChunkStatsDTO();
        
        insertions.setChunksAvgSize(0);
        insertions.setChunksCount(0);
        insertions.setChunksItems(0);
        
        deletions.setChunksAvgSize(0);
        deletions.setChunksCount(0);
        deletions.setChunksItems(0);
             
        chunks.setInsertions(insertions);
        chunks.setDeletions(deletions);
    	
        return new ChangeSetChunkDTO(
            "",
            "",
            new Date(),
            new Date(),
            chunks,
            0,
            0
        );
    }

    public ActionRes onProcessing(boolean verify) {
    	this.verify = verify;
    	return super.onProcessing();
    }
    
    @Override
    public IChunkHandlerEDS onChunkInsertion() {
    	return (staging, info, index, max) -> verify ? new SimpleImmutableEntry<>(OK, 1) : new SimpleImmutableEntry<>(KO, 0);
    }
    
    @Override
    public IChunkHandlerEDS onChunkDeletions() {
    	return (staging, info, index, max) -> verify ? new SimpleImmutableEntry<>(OK, 1) : new SimpleImmutableEntry<>(KO, 0);
    }
    
    @Override
    public ActionRes onChangesetEmpty() {
    	return super.onChangesetEmpty();
    }
    
    @Override
    public ActionRes onProcessing() {
    	return super.onProcessing();
    }
    
    @Override
    public ActionRes onVerify() {
    	return super.onVerify();
    }
    
    @Override
    public ActionRes onSync() {
    	return super.onSync();
    }
    
    @Override
    public ActionRes onChangesetAlignment() {
    	return super.onChangesetAlignment();
    }
    
    @Override
    public ActionRes onReset() {
    	return super.onReset();
    }
    
    @Override
    public ActionRes onVerifyProductionSize() {
    	return super.onVerifyProductionSize();
    }
}
