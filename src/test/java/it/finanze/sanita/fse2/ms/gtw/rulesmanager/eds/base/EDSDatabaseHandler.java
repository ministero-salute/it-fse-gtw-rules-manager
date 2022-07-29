package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

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
        initTestEntities();
        // Add to collection
        addTestEntityToSchema(name);
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

    private boolean isTestSchemaAvailable(String name) {
        return mongo.getCollectionNames().contains(name);
    }
}
