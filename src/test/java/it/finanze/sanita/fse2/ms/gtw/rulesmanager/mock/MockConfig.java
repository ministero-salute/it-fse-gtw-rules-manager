/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import org.springframework.stereotype.Component;

@Component
public class MockConfig extends ChangesetCFG {
    protected MockConfig() {
        super("https://127.0.0.1/status", "https://127.0.0.1/data/", "mock");
    }
}
