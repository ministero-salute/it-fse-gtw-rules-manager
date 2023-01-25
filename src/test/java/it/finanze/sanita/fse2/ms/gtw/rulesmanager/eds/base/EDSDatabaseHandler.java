/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.CONFIG_MONGO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.REPOSITORY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.SCHEDULER_QUERIES;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.UTILITY;

import java.io.IOException;

@ComponentScans( value = {
	    @ComponentScan(CONFIG_MONGO),
	    @ComponentScan(REPOSITORY),
	    @ComponentScan(SCHEDULER_QUERIES),
	    @ComponentScan(UTILITY)
	})
public abstract class EDSDatabaseHandler extends EDSEntityHandler {
    @Autowired
    private MongoTemplate mongo;

    protected void setupTestRepository(String name) throws IOException {
        // Verify if previous test database exists
        if (isTestSchemaAvailable(name)) {
            // Drop it, we are going to create a new one
            mongo.dropCollection(name);
        }
        // Make test collection
        createTestSchema(name);
        // Init entities
        if(getEntities().size() == 0) initTestEntities();
        if(getTerminologies().size() == 0) initTestTerminologies();

        // Add to collection
        addTestEntityToSchema(name);
        addTestTerminologiesToSchema(name); 
    }

    protected void clearTestRepository(String name) {
        mongo.dropCollection(name);
        this.clearTestEntities();
    }

    private void createTestSchema(String name) {
        mongo.createCollection(name);
    }

    private void addTestEntityToSchema(String name) {
        mongo.insert(getEntities(), name);
    }

    private void addTestTerminologiesToSchema(String name) {
        mongo.insert(getTerminologies(), name);
    }
    
    private boolean isTestSchemaAvailable(String name) {
        return mongo.getCollectionNames().contains(name);
    }
}
