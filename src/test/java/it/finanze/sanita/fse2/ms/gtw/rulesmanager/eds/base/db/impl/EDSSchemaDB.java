/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchemaCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSSchemaHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;

@Component
public class EDSSchemaDB extends AbstractSchemaDB<SchemaETY> {
    public EDSSchemaDB(MongoTemplate mongo, EDSSchemaHandler hnd, SchemaCFG config) {
        super(mongo, hnd, config);
    }
}
