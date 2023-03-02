package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.FhirStructuresCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTransformHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TransformETY;

@Component
public class EDSTransformDB extends AbstractSchemaDB<TransformETY> {
	public EDSTransformDB(MongoTemplate mongo, EDSTransformHandler hnd, FhirStructuresCFG config) {
        super(mongo, hnd, config);
    }
}
