/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DictionaryCFG extends ChangesetCFG {
    private static final String SCHEMA = "dictionary";
    private final String backup;

    protected DictionaryCFG(TerminologyCFG parent) {
        super(null, null, SCHEMA, parent);
        this.backup = SCHEMA + BACKUP_QUALIFIER;
    }
}
