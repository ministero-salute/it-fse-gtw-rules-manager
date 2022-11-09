/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSDatabaseHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;


@DataMongoTest
@ComponentScans( value = {
    @ComponentScan(CONFIG_MONGO),
    @ComponentScan(REPOSITORY),
    @ComponentScan(SCHEDULER_QUERIES),
    @ComponentScan(UTILITY)
})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EDSRepoTest extends EDSDatabaseHandler {

    @SpyBean
    private MongoTemplate mongo;
    @Autowired
    private IExecutorRepo repository;
    @Autowired
    private SchemaQuery query;

    @BeforeAll
    public void setup() throws IOException {
        this.setupTestRepository(TEST_BASE_COLLECTION);
    }

    @Test
    void rename() {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        mongo.dropCollection(TEST_COLL_B);
        // Create collection
        MongoCollection<Document> tmp = mongo.createCollection(TEST_COLL_A);
        // Rename and drop obsolete target
        assertDoesNotThrow(() -> repository.rename(tmp, TEST_COLL_B));
        // Verify if new one exists
        assertTrue(mongo.collectionExists(TEST_COLL_B));
        // Verify if old one has been removed
        assertFalse(mongo.collectionExists(TEST_COLL_A));
        // Now drop
        mongo.dropCollection(TEST_COLL_B);
    }

    @Test
    void exists() {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        // Create collection
        mongo.createCollection(TEST_COLL_A);
        // Verify exists
        assertDoesNotThrow(() -> assertTrue(repository.exists(TEST_COLL_A)));
        assertDoesNotThrow(() -> assertFalse(repository.exists(TEST_COLL_B)));
        // Now drop
        mongo.dropCollection(TEST_COLL_A);
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).collectionExists(anyString());
        // Drop
        assertThrows(EdsDbException.class, () -> repository.exists(TEST_COLL_A));
    }

    @Test
    void drop() {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        // Create collection
        mongo.createCollection(TEST_COLL_A);
        // Verify exists
        assertTrue(mongo.collectionExists(TEST_COLL_A));
        // Drop
        assertDoesNotThrow(() -> repository.drop(TEST_COLL_A));
        // Verify not exists
        assertFalse(mongo.collectionExists(TEST_COLL_A));
        // Drop collection that doesn't exist
        assertDoesNotThrow(() -> repository.drop(TEST_COLL_B));
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).dropCollection(anyString());
        // Drop
        assertThrows(EdsDbException.class, () -> repository.drop(TEST_COLL_A));
    }

    @Test
    void create() {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        // Create collection
        assertDoesNotThrow(() -> repository.create(TEST_COLL_A));
        // Verify exists
        assertTrue(mongo.collectionExists(TEST_COLL_A));
        // Now create one with the same name
        assertThrows(EdsDbException.class, () -> repository.create(TEST_COLL_A));
        // Drop
        assertDoesNotThrow(() -> mongo.dropCollection(TEST_COLL_A));
        // Verify not exists
        assertFalse(mongo.collectionExists(TEST_COLL_A));
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).createCollection(anyString());
        // Create
        assertThrows(EdsDbException.class, () -> repository.create(TEST_COLL_A));
        // Now drop
        mongo.dropCollection(TEST_COLL_A);
    }

    @Test
    void cloner() {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        // Clone base repository already filled with docs
        assertDoesNotThrow(() -> {
            // Clone base repository already filled with docs
            MongoCollection<Document> copy = repository.clone(TEST_BASE_COLLECTION, TEST_COLL_A);
            // Verify it now exists
            assertTrue(mongo.collectionExists(TEST_COLL_A));
            // Verify documents
            assertEquals(copy.countDocuments(), SCHEMA_TEST_SIZE);
            // Now compare deeply
            assertTrue(compareDeeply(copy, mongo.getCollection(TEST_BASE_COLLECTION), query));
        });
        // Drop
        mongo.dropCollection(TEST_COLL_A);
    }

    @Test
    void sync() {
        // Verify base repository is up
        assertTrue(mongo.collectionExists(TEST_BASE_COLLECTION));
        // Now set sync
        Date currentSync = new Date();
        // Set it
        assertDoesNotThrow(() -> {
            // Sync now
            repository.sync(TEST_BASE_COLLECTION, currentSync);
            // Verify sync
            assertEquals(repository.getLastSync(TEST_BASE_COLLECTION), currentSync);
        });
    }

    @AfterAll
    public void teardown() {
        this.clearTestRepository(TEST_BASE_COLLECTION);
    }
}
