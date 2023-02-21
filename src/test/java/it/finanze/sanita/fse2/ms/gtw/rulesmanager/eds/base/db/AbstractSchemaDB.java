package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractSchemaDB<T> {

    private final MongoTemplate mongo;
    private final AbstractEntityHandler<T> hnd;

    public AbstractSchemaDB(MongoTemplate mongo, AbstractEntityHandler<T> hnd) {
        this.mongo = mongo;
        this.hnd = hnd;
    }

    public void setupTestRepository(String name) throws Exception {
        // Verify if previous test database exists
        if (isTestSchemaAvailable(name)) {
            // Drop it, we are going to create a new one
            mongo.dropCollection(name);
        }
        // Make test collection
        createTestSchema(name);
        // Init entities
        hnd.initTestEntities();
        // Add to collection
        addTestEntityToSchema(name);
    }

    public void clearTestRepository(String name) {
        mongo.dropCollection(name);
        hnd.clearTestEntities();
    }

    private void createTestSchema(String name) {
        mongo.createCollection(name);
    }

    private void addTestEntityToSchema(String name) {
        mongo.insert(hnd.getEntities(), name);
    }

    private boolean isTestSchemaAvailable(String name) {
        return mongo.getCollectionNames().contains(name);
    }

    public AbstractEntityHandler<T> handler() {
        return hnd;
    }

    public void verifyIntegrity(MongoCollection<Document> target) {
        assertTrue(compareDeeply(hnd.getEntitiesAsDocuments(), target));
    }
}
