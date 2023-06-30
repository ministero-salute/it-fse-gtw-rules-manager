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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryInsertDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.IChunkHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.TermsExecutor;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryDeleteDTO;
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
    
    public static ChangeSetChunkDTO createChangesetChunk(int insert, int delete) {
        List<HistoryInsertDTO> insertions = new ArrayList<>();
        for(int i = 0; i < insert; ++i) {
            insertions.add(new HistoryInsertDTO(String.valueOf(i), String.valueOf(i + 1), "codesystem"));
        }
        List<HistoryDeleteDTO> deletions = new ArrayList<>();
        for(int i = 0; i < delete; ++i) {
            deletions.add(new HistoryDeleteDTO(String.valueOf(i + insert), "codesystem", null));
        }

        return new ChangeSetChunkDTO(
            "randomTraceId",
            "randomSpanId",
            new Date(),
            new Date(),
            insertions,
            deletions
        );
    }

    public static ChangeSetChunkDTO emptyChangesetChunk() {
        return new ChangeSetChunkDTO(
            "",
            "",
            new Date(),
            new Date(),
            new ArrayList<>(),
            new ArrayList<>()
        );
    }

    public ActionRes onProcessing(boolean verify) {
    	this.verify = verify;
    	return super.onProcessing();
    }
    
    @Override
    public IChunkHandlerEDS<HistoryInsertDTO> onChunkInsertion() {
    	return (staging, info) -> verify ? new SimpleImmutableEntry<>(OK, 1) : new SimpleImmutableEntry<>(KO, 0);
    }
    
    @Override
    public IChunkHandlerEDS<HistoryDeleteDTO> onChunkDeletions() {
    	return (staging, info) -> verify ? new SimpleImmutableEntry<>(OK, 1) : new SimpleImmutableEntry<>(KO, 0);
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
