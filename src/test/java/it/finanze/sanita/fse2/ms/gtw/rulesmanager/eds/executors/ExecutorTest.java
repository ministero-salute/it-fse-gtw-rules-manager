
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.executors;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.SchemaSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Schema;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockSchemaExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl.ConfigSRV;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockSchemaExecutor.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryExecutorOnException;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryRecoveryOnException;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
@Slf4j
class ExecutorTest {

    @SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClient client;
    @SpyBean
    private MockSchemaExecutor executor;
    @SpyBean
    private IExecutorRepo repository;
    @Autowired
    private EDSSchemaDB db;
    @MockBean
    private ConfigSRV config;

    @BeforeAll
    public void init() { resetDB(); }

    @AfterEach
    public void reset() {
        resetDB();
        resetListeners();
    }

    @Test
    void execute() {
        // Mock an executor that simply reset
        doReturn(FAKE_STEPS).when(executor).getSteps();
        when(config.isControlLogPersistenceEnable()).thenReturn(true);
        assertEquals(OK, executor.execute());
        // Mock a runtime error then call real method after test
        doThrow(new RuntimeException("Test error")).doCallRealMethod().when(executor).startup(any(String[].class));
        assertEquals(KO, executor.execute());
        // Mock an erroneous step pattern
        doReturn(ERR_FAKE_STEPS).when(executor).getSteps();
        assertEquals(KO, executor.execute());
    }

    @Test
    void recovery() {
        // Mock an executor that simply reset
        doReturn(FAKE_STEPS).when(executor).getRecoverySteps();
        when(config.isControlLogPersistenceEnable()).thenReturn(true);
        assertEquals(OK, executor.recovery());
        // Mock a runtime error then call real method after test
        doThrow(new RuntimeException("Test error")).doCallRealMethod().when(executor).startup(any(String[].class));
        assertEquals(KO, executor.recovery());
        // Mock an erroneous step pattern
        doReturn(ERR_FAKE_STEPS).when(executor).getRecoverySteps();
        assertEquals(KO, executor.recovery());
    }

    @Test
    void clean() {
        // Setup production
        setupProduction();
        // Create collection
        mongo.createCollection(executor.getConfig().getStaging());
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        // Execute
        assertEquals(OK, executor.onClean());
        // Verify
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        // No collection exists, call executor without collection
        assertEquals(OK, executor.onClean());
        // Verify production integrity
        db.verifyIntegrityProduction();
    }


    @Test
    void changeset() throws EdsClientException {
        // Setup production
        setupProduction();
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(emptyChangeset());
        // Changeset should be retrieved correctly
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(createChangeset(10, 1, 10));
        // Changeset is not empty, it should be OK
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        when(client.getStatus(any(), any(), any())).thenReturn(createChangeset(0,0,0));
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenThrow(
            new EdsClientException("Test error", new IOException("Test error"))
        );
        // Get status errored, it should be KO
        assertEquals(KO, executor.onChangeset(executor.onLastUpdateProd()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void changesetEmpty() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock to trigger empty changeset check
        executor.setChangeset(createChangeset(0, 0, size));
        // Setup production
        setupProduction();
        // Verify size
        assertEquals(EXIT, executor.onChangesetEmpty());
    }

    @Test
    void changesetNotEmpty() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock to trigger empty changeset check
        executor.setChangeset(createChangeset(10, 0, size));
        // Verify size
        assertEquals(OK, executor.onChangesetEmpty());
    }

    
    @Test
    void staging() {
        // Case #1 - Production exists
        // Create collection
        setupProduction();
        // Verify exists
        assertTrue(mongo.collectionExists(executor.getConfig().getProduction()));
        // Execute
        assertEquals(OK, executor.onStaging());
        // Now staging and production co-exists
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        assertTrue(mongo.collectionExists(executor.getConfig().getProduction()));
        // Verify production integrity
        db.verifyIntegrityProduction();
        // Emptying database
        resetDB();
        // Case #2 - Production not exists
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
        // Execute
        assertEquals(OK, executor.onStaging());
        // There is only staging
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

    @Test
    void processing() {
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setChangeset(createChangeset(10, 1, 9));
        // Call processing with verified flag
        assertEquals(OK, executor.onProcessing(true));
        // Now check size
        assertEquals(10, executor.getOperations().getInsertions());
        assertEquals(1, executor.getOperations().getDeletions());
        assertEquals(11, executor.getOperations().getOperations());
        // Call processing with verified flag
        assertEquals(executor.onProcessing(false), OK);
        // Now check size
        assertEquals(0, executor.getOperations().getInsertions());
        assertEquals(0, executor.getOperations().getDeletions());
        assertEquals(0, executor.getOperations().getOperations());
        // Verify production integrity
        db.verifyIntegrityProduction();
    } 

    @Test
    void processingWithDb() throws EdsClientException {
        // Setup production
        setupProduction();
        // It emulates an empty collection
        mongo.dropCollection(executor.getConfig().getStaging());
        executor.setCollection(mongo.getCollection(executor.getConfig().getStaging()));
        // Set changeset for one insertion and one delete
        executor.setChangeset(createChangeset(1, 1, 1));
        // Prepare one mock document for insertion
        SchemaDTO in = new SchemaDTO();
        Schema schema = new Schema();
        schema.setId(new ObjectId().toHexString());
        schema.setContentSchema("dGVzdA==");
        in.setDocument(schema);
        // Call processing with super flag
        when(client.getDocument(any(), anyString(), any())).thenReturn(in);
        assertEquals(OK, executor.onProcessing(null));
        // Now check size
        assertEquals(1, executor.getOperations().getInsertions());
        assertEquals(1, executor.getOperations().getDeletions());
        assertEquals(2, executor.getOperations().getOperations());
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void processingEmptyChangeset() {
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setChangeset(emptyChangeset());
        // Call processing with verified flag
        assertEquals(OK, executor.onProcessing(true));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }


    @Test
    @SuppressWarnings("unchecked")
    void verify() {
        // Define expected size
        final long size = 9;
        // Provide mock-knowledge (for staging that doesn't exist)
        assertDoesNotThrow(() -> {
            doReturn(size).when(repository).countActiveDocuments(nullable(MongoCollection.class));
        });
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setChangeset(createChangeset(10,1, size));
        // Call processing with verified flag
        assertEquals(OK, executor.onProcessing(true));
        assertEquals(OK, executor.onVerify());
        // Call processing with unverified flag
        assertEquals(OK, executor.onProcessing(false));
        assertEquals(KO, executor.onVerify());
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void swap() throws Exception {
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Call swap with staging instance
        assertEquals(OK, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onBeforeSwapOK() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Setup listener
        executor.setOnBeforeSwap(() -> CB_OK);
        // Call swap with staging instance
        assertEquals(OK, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onBeforeSwapKO() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Setup listener
        executor.setOnBeforeSwap(() -> CB_KO);
        // Call swap with staging instance
        assertEquals(KO, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onFailedSwapOK() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Setup listener
        executor.setOnBeforeSwap(() -> CB_KO);
        executor.setOnFailedSwap(() -> CB_OK);
        // Call swap with staging instance
        assertEquals(KO, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onFailedSwapKO() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Setup listener
        executor.setOnBeforeSwap(() -> CB_KO);
        executor.setOnFailedSwap(() -> CB_KO);
        // Call swap with staging instance
        assertEquals(KO, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onSuccessSwapOK() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Setup listener
        executor.setOnBeforeSwap(() -> CB_OK);
        executor.setOnSuccessSwap(() -> CB_OK);
        // Call swap with staging instance
        assertEquals(OK, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onSuccessSwapKO() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupStaging();
        // Setup listener
        executor.setOnBeforeSwap(() -> CB_OK);
        executor.setOnSuccessSwap(() -> CB_KO);
        // Call swap with staging instance
        assertEquals(OK, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void size() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock due to stats printing on log
        executor.setChangeset(createChangeset(size, 0, size));
        // Setup production
        setupProduction();
        // Verify size
        assertEquals(EXIT, executor.onVerifyProductionSize());
    }

    @Test
    void sizeMismatch() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock due to stats printing on log
        executor.setChangeset(createChangeset(size, 0, size + 2));
        // Setup production
        setupProduction();
        // Verify size
        assertEquals(KO, executor.onVerifyProductionSize());
    }

    @Test
    void sync() throws Exception {
        // Setup production
        setupProduction();
        // Setup repository with document
        db.setupStaging();
        // Create
        ChangeSetDTO<SchemaSetDTO> changeset = createChangeset(10, 1, 9);
        // Setup changeset
        executor.setChangeset(changeset);
        // Call it
        assertEquals(OK, executor.onSync());
        // Retrieve
        Date lastSync = repository.getLastSync(executor.getConfig().getStaging());
        // Verify it matches
        assertEquals(lastSync, changeset.getTimestamp());
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void onChangesetAlignment() {
        // Setup production
        setupProduction();
        // Create empty changeset
        ChangeSetDTO<SchemaSetDTO> changeset = createChangeset(0,0, 0);
        // Set
        executor.setChangeset(changeset);
        // Call it
        assertEquals(OK, executor.onChangesetAlignment());
        // Create changeset
        changeset = createChangeset(10,3, 7);
        // Set
        executor.setChangeset(changeset);
        // Call it
        assertEquals(KO, executor.onChangesetAlignment());
        // Verify production integrity
        db.verifyIntegrityProduction();
    }

    @Test
    void registerAdditionalHandlers() {
        // Executor is already setup for one additional handler
        executor.registerAdditionalHandlers();
        // Check if mapping key is available
        assertTrue(
            executor.getMappers().containsKey(EMPTY_STEP)
        );
    }

    @Test
    void deepCompare() {
        // Setup production
        setupProduction();
        // Use modified entities list to create a mismatch
        assertFalse(compareDeeply(db.handler().getModifiedEntitiesAsDocuments(), mongo.getCollection(executor.getConfig().getProduction())));
    }

    @Test
    void retries() {
        // Executor mode
        assertEquals(OK, retryExecutorOnException(() -> OK, log));
        assertEquals(OK, retryExecutorOnException(() -> OK, log, 5));
        assertEquals(KO, retryExecutorOnException(() -> KO, log, 5));
        // Recovery mode
        assertEquals(OK, retryRecoveryOnException(() -> OK, log));
        assertEquals(OK, retryRecoveryOnException(() -> OK, log, 5));
        assertEquals(KO, retryRecoveryOnException(() -> KO, log, 5));
    }

    private void setupProduction() {
        assertDoesNotThrow(() -> db.setupProduction());
    }

    private void resetListeners() {
        executor.setOnBeforeSwap(null);
        executor.setOnFailedSwap(null);
        executor.setOnSuccessSwap(null);
    }

    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

}
