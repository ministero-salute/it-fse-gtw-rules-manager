/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.cfg;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import org.springframework.stereotype.Component;

@Component
public class MockChunksConfig extends ChunkChangesetCFG {
    protected MockChunksConfig() {
        super(
            "https://127.0.0.1/status",
            "https://127.0.0.1/data/insert",
            "https://127.0.0.1/data/delete",
            "mock-chunks"
        );
    }
}
