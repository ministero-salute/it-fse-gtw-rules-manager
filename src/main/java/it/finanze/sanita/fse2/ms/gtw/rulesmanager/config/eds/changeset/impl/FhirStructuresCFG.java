/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.CollectionNaming;
import lombok.Getter;


@Configuration
@Getter
public class FhirStructuresCFG extends ChangesetCFG {
    
    protected FhirStructuresCFG (
    	CollectionNaming naming,
        @Value("${eds.changeset.fhir.status}")
        String status,
        @Value("${eds.changeset.fhir.data}")
        String data
    ) {
        super(status, data, naming.getStructuresCollection());
    }
}
