/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.CollectionNaming;
import lombok.Getter;

@Configuration
@Getter
public class DictionaryCFG extends ChangesetCFG {
    private final String backup;

    protected DictionaryCFG(CollectionNaming naming, TerminologyCFG parent) {
        super(null, null, naming.getDictionaryCollection(), parent);
        this.backup = naming.getDictionaryCollection() + BACKUP_QUALIFIER;
    }
}
