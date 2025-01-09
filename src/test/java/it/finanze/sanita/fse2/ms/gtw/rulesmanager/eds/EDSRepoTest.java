/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB.TEST_BASE_COLLECTION;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSSchemaHandler.SCHEMA_TEST_SIZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;


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
    void rename() throws EdsDbException {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        mongo.dropCollection(TEST_COLL_B);
        // Create collection
        MongoCollection<Document> tmp = mongo.createCollection(TEST_COLL_A);
        // Rename and drop obsolete target
        assertDoesNotThrow(() -> repository.rename(tmp, TEST_COLL_B));
        // Verify if new one exists
        assertTrue(repository.exists(TEST_COLL_B));
        // Verify if old one has been removed
        assertFalse(repository.exists(TEST_COLL_A));
        // Now drop
        mongo.dropCollection(TEST_COLL_B);
    }

    @Test
    void drop() throws EdsDbException {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        // Create collection
        mongo.createCollection(TEST_COLL_A);
        // Verify exists
        assertTrue(repository.exists(TEST_COLL_A));
        // Drop
        assertDoesNotThrow(() -> repository.drop(TEST_COLL_A));
        // Verify not exists
        assertFalse(repository.exists(TEST_COLL_A));
        // Drop collection that doesn't exist
        assertDoesNotThrow(() -> repository.drop(TEST_COLL_B));
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).dropCollection(anyString());
        // Drop
        assertThrows(EdsDbException.class, () -> repository.drop(TEST_COLL_A));
    }

    @Test
    void create() throws EdsDbException {
        // Drop if exists
        mongo.dropCollection(TEST_COLL_A);
        // Create collection
        assertDoesNotThrow(() -> repository.create(TEST_COLL_A));
        // Verify exists
        assertTrue(repository.exists(TEST_COLL_A));
        // Now create one with the same name
        assertThrows(EdsDbException.class, () -> repository.create(TEST_COLL_A));
        // Drop
        assertDoesNotThrow(() -> mongo.dropCollection(TEST_COLL_A));
        // Verify not exists
        assertFalse(repository.exists(TEST_COLL_A));
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
            assertTrue(repository.exists(TEST_COLL_A));
            // Verify documents
            assertEquals(copy.countDocuments(), SCHEMA_TEST_SIZE);
            // Now compare deeply
            assertTrue(compareDeeply(copy, mongo.getCollection(TEST_BASE_COLLECTION), query));
        });
        // Drop
        mongo.dropCollection(TEST_COLL_A);
        // Throw if source does not exist
        assertThrows(EdsDbException.class, () -> repository.clone(TEST_COLL_A, TEST_BASE_COLLECTION));
        // Throw if target exists
        assertThrows(EdsDbException.class, () -> repository.clone(TEST_BASE_COLLECTION, TEST_BASE_COLLECTION));
    }

    @Test
    void sync() throws EdsDbException {
        // Drop
        mongo.dropCollection(TEST_COLL_A);
        // Verify base repository is up
        assertTrue(repository.exists(TEST_BASE_COLLECTION));
        // Now set sync
        Date currentSync = new Date();
        // Set it
        assertDoesNotThrow(() -> {
            // Sync now
            repository.sync(TEST_BASE_COLLECTION, currentSync);
            // Verify sync
            assertEquals(repository.getLastSync(TEST_BASE_COLLECTION), currentSync);
        });
        // Throw if source does not exist
        assertThrows(EdsDbException.class, () -> repository.sync(TEST_COLL_A, currentSync));
    }

    @Test
    void ids() throws EdsDbException {
        // Drop
        mongo.dropCollection(TEST_COLL_A);
        // Verify base repository is up
        assertTrue(repository.exists(TEST_BASE_COLLECTION));
        // Verify does not throw
        assertDoesNotThrow(() -> {
            // Retrieve active docs
            List<ObjectId> ids = repository.getActiveDocumentsId(TEST_BASE_COLLECTION);
            // Check is not empty
            assertFalse(ids.isEmpty());
        });
        // Throw if source does not exist
        assertThrows(EdsDbException.class, () -> repository.getActiveDocumentsId(TEST_COLL_A));
    }

    @Test
    void count() throws EdsDbException {
        // Verify base repository is up
        assertTrue(repository.exists(TEST_BASE_COLLECTION));
        // Verify does not throw
        assertDoesNotThrow(() -> {
            // Retrieve active docs
            long size = repository.countActiveDocuments(mongo.getCollection(TEST_BASE_COLLECTION));
            // Check is not empty
            assertNotEquals(0, size);
            // Retrieve active docs (different signature)
            size = repository.countActiveDocuments(TEST_BASE_COLLECTION);
            // Check is not empty
            assertNotEquals(0, size);
        });
    }

    @AfterAll
    public void teardown() {
        db.clearTest();
    }
}
