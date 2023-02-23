/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;

@Component
public class EDSTermsDB extends AbstractSchemaDB<TerminologyETY> {
    public EDSTermsDB(MongoTemplate mongo, EDSTermsHandler hnd, TerminologyCFG config) {
        super(mongo, hnd, config);
    }
}
