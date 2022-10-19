/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.chunk;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TerminologyChunkedCFG extends ChunkChangesetCFG {

    private static final String SCHEMA = "terminology";

    protected TerminologyChunkedCFG(
        @Value("${eds.changeset.terminology.chunks.status}")
        String status,
        @Value("${eds.changeset.terminology.chunks.ins}")
        String insert,
        @Value("${eds.changeset.terminology.chunks.del}")
        String delete
    ) {
        super(status, insert, delete, SCHEMA);
    }
}
