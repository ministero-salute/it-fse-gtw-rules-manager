/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IEngineRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.EngineETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.sub.EngineMap;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EngineSRV implements IEngineSRV {

    @Autowired
    private IExecutorRepo repository;

    @Autowired
    private IEngineRepo engine;

    @Override
    public boolean synthesize(String fhir, MongoCollection<Document> engines) throws EdsDbException {

        boolean spawned = false;
        // Get active object id(s)
        List<ObjectId> ids = repository.getActiveDocumentsId(fhir);
        // Keep processing if collection is not empty
        if(!ids.isEmpty()) {
            // Retrieve last_sync
            Date sync = repository.getLastSync(fhir);
            // Retrieves map
            List<EngineMap> maps = engine.getActiveMaps(fhir);
            // Insert
            engine.insert(engines, EngineETY.from(ids, maps, sync));
            // Mark as spawned
            spawned = true;
        }

        return spawned;
    }
}
