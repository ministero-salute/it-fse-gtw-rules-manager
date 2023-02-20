/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class EDSTermsDB extends AbstractSchemaDB<TerminologyETY> {
    public EDSTermsDB(@Autowired MongoTemplate mongo, @Autowired EDSTermsHandler hnd) {
        super(mongo, hnd);
    }
}
