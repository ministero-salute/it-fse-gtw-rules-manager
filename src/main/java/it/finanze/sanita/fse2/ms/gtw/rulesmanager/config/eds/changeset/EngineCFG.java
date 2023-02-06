/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.FhirStructuresCFG;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class EngineCFG extends ChangesetCFG {
    private static final String SCHEMA = "engines";
    private final String backup;

    protected EngineCFG(FhirStructuresCFG parent) {
        super(null, null, SCHEMA, parent);
        this.backup = SCHEMA + BACKUP_QUALIFIER;
    }
}
