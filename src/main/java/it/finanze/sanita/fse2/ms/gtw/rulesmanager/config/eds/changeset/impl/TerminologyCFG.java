/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.CollectionNaming;
import lombok.Getter;

@Configuration
@Getter
public class TerminologyCFG extends ChunkChangesetCFG {

    protected TerminologyCFG(
    	CollectionNaming naming,
        @Value("${eds.changeset.terminology.chunks.status}")
        String status,
        @Value("${eds.changeset.terminology.chunks.ins}")
        String insert,
        @Value("${eds.changeset.terminology.chunks.del}")
        String delete
    ) {
        super(status, insert, delete, naming.getTerminologyCollection());
    }
}
