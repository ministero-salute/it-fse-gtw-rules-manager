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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.FhirStructuresCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.FhirStructuresSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.FhirStructuresQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class FhirStructuresExecutors extends ExecutorEDS<FhirStructuresSetDTO> {

    @Autowired
    private FhirStructuresQuery query;
  

    protected FhirStructuresExecutors(FhirStructuresCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<FhirStructuresSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<FhirStructuresSetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<FhirStructuresSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, FhirStructuresDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<FhirStructuresSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }
}
