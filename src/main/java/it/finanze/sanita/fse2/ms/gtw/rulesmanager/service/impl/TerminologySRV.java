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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityDTO.Resources;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityResultDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ITerminologySRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

@Service
public class TerminologySRV implements ITerminologySRV {

    @Autowired
    private ITerminologyRepo repository;

    @Override
    public void applyIndexes(String collection) throws EdsDbException {
        repository.applyIndexes(collection);
    }

    @Override
    public IntegrityResultDTO matches(IntegrityDTO integrity, String collection) throws EdsDbException {
        IntegrityResultDTO res = new IntegrityResultDTO();
        if(integrity.isEmpty()) {
            verifyResourcesCount(res, collection);
        } else {
            verifyResources(integrity, collection, res);
        }
        return res;
    }

    @Override
    public long countActiveResources(String collection) throws EdsDbException {
        return repository.countActiveResources(collection);
    }

    private void verifyResourcesCount(IntegrityResultDTO res, String collection) throws EdsDbException {
        if(countActiveResources(collection) == 0) {
            res.setSynced(OK);
        }
    }

    private void verifyResources(IntegrityDTO integrity, String collection, IntegrityResultDTO res) throws EdsDbException {
        for (Resources resource : integrity.getResources()) {
            boolean exists = repository.exists(resource.getId(), resource.getVersion(), collection);
            if(!exists) {
                res.getMissing().add(resource);
            } else {
                verifyResourceSizeIfProvided(collection, res, resource);
            }
        }
        if(res.noMissingResources() && res.noSizeMismatchResources()) res.setSynced(OK);
    }

    private void verifyResourceSizeIfProvided(String collection, IntegrityResultDTO res, Resources resource) throws EdsDbException {
        if(resource.getSize().isPresent()) {
            long current = repository.countActiveResources(
                resource.getId(),
                resource.getVersion(),
                collection
            );
            long expected = resource.getSize().get();
            // Adjust for whitelisted items
            if(expected == 0) expected = 1;
            // Now check
            if(current != expected) {
                res.getMismatch().add(resource);
            }
        }
    }
}
