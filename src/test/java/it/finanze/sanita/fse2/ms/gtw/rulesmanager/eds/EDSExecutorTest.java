/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockData;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import org.bson.Document;
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

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EDSExecutorTest {

    @SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClient client;
    @Autowired
    private MockExecutor executor;
    @SpyBean
    private IExecutorRepo repository;
    @Autowired
    private EDSSchemaDB db;

    @BeforeAll
    public void init() { resetDB(); }

    @AfterEach
    public void reset() {
        resetDB();
        resetListeners();
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
        verifyProductionIntegrity();
    }


    @Test
    void changeset() throws EdsClientException {
        // Setup production
        setupProduction();
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(emptyChangeset());
        when(client.getSnapshot(any(), any(), any())).thenReturn(emptyChangeset());
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
        verifyProductionIntegrity();
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
        verifyProductionIntegrity();
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
        verifyProductionIntegrity();
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
        verifyProductionIntegrity();
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
        verifyProductionIntegrity();
    }

    @Test
    void swap() throws Exception {
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
        // Call swap with staging instance
        assertEquals(OK, executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ));
        // Staging is removed, production is kept
        assertTrue(repository.exists(executor.getConfig().getProduction()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Verify production integrity
        verifyProductionIntegrity();
    }

    @Test
    void onBeforeSwapOK() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
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
        verifyProductionIntegrity();
    }

    @Test
    void onBeforeSwapKO() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
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
        verifyProductionIntegrity();
    }

    @Test
    void onFailedSwapOK() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
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
        verifyProductionIntegrity();
    }

    @Test
    void onFailedSwapKO() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
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
        verifyProductionIntegrity();
    }

    @Test
    void onSuccessSwapOK() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
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
        verifyProductionIntegrity();
    }

    @Test
    void onSuccessSwapKO() throws Exception {
        // Mock due to stats printing on log
        executor.setChangeset(emptyChangeset());
        // Setup production
        setupProduction();
        // Setup repositories with documents
        db.setupTestRepository(executor.getConfig().getStaging());
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
        verifyProductionIntegrity();
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
        db.setupTestRepository(executor.getConfig().getStaging());
        // Create
        ChangeSetDTO<MockData> changeset = createChangeset(10, 1, 9);
        // Setup changeset
        executor.setChangeset(changeset);
        // Call it
        assertEquals(OK, executor.onSync());
        // Retrieve
        Date lastSync = repository.getLastSync(executor.getConfig().getStaging());
        // Verify it matches
        assertEquals(lastSync, changeset.getTimestamp());
        // Verify production integrity
        verifyProductionIntegrity();
    }

    @Test
    void onChangesetAlignment() {
        // Setup production
        setupProduction();
        // Create empty changeset
        ChangeSetDTO<MockData> changeset = createChangeset(0,0, 0);
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
        verifyProductionIntegrity();
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
        assertFalse(compareDeeply(db.handler().getModifiedEntitiesAsDocuments(), getProduction()));
    }

    private void setupProduction() {
        assertDoesNotThrow(() -> db.setupTestRepository(executor.getConfig().getProduction()));
    }

    private void resetListeners() {
        executor.setOnBeforeSwap(null);
        executor.setOnFailedSwap(null);
        executor.setOnSuccessSwap(null);
    }

    private MongoCollection<Document> getProduction() {
        return mongo.getCollection(executor.getConfig().getProduction());
    }

    private void verifyProductionIntegrity() {
        assertTrue(compareDeeply(db.handler().getEntitiesAsDocuments(), getProduction()));
    }

    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

}
