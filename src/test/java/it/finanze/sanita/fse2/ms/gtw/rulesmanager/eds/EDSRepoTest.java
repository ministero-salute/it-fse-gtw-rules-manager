/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB.TEST_BASE_COLLECTION;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSSchemaHandler.SCHEMA_TEST_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.util.Date;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;


@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EDSRepoTest {
	
	public static String TEST_COLL_A = "eds-test-0";
    public static String TEST_COLL_B = "eds-test-1";

    @SpyBean
    private MongoTemplate mongo;
    @Autowired
    private IExecutorRepo repository;
    @Autowired
    private SchemaQuery query;
    @Autowired
    private EDSSchemaDB db;

    @BeforeAll
    public void setup() throws Exception {
        db.setupTest();
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
        db.clearTest();
    }
}
