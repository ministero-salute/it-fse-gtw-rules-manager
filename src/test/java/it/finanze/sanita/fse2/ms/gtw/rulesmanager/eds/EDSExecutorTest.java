/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSDatabaseHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.createChangeset;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.emptyChangeset;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@ComponentScans( value = {
	    @ComponentScan(CONFIG_MONGO),
	    @ComponentScan(REPOSITORY),
	    @ComponentScan(SCHEDULER_QUERIES),
	    @ComponentScan(UTILITY)
	})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EDSExecutorTest extends EDSDatabaseHandler {

    @SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClient client;
    @Autowired
    private MockExecutor executor;
    @Autowired
    private IExecutorRepo repository;

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
        // Changeset should be retrieved correctly
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Changeset is empty, it should quit
        assertEquals(EXIT, executor.onChangesetEmpty());
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(createChangeset(10, 0, 10));
        // Changeset is not empty, it should be OK
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
        executor.setChangeset(MockExecutor.createChangeset(10, 1, 9));
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
    void verify() {
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setChangeset(MockExecutor.createChangeset(10,1, 9));
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
    void swap() throws IOException, EdsDbException {
        // Setup production
        setupProduction();
        // Setup repositories with documents
        this.setupTestRepository(executor.getConfig().getStaging());
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
    void sync() throws IOException, EdsDbException {
        // Setup production
        setupProduction();
        // Setup repository with document
        this.setupTestRepository(executor.getConfig().getStaging());
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
            executor.getMappers().containsKey(MockExecutor.EMPTY_STEP)
        );
    }

    @Test
    void deepCompare() {
        // Setup production
        setupProduction();
        // Use modified entities list to create a mismatch
        assertFalse(compareDeeply(getModifiedEntitiesAsDocuments(), getProduction()));
    }

    private void setupProduction() {
        assertDoesNotThrow(() -> this.setupTestRepository(executor.getConfig().getProduction()));
    }

    private MongoCollection<Document> getProduction() {
        return mongo.getCollection(executor.getConfig().getProduction());
    }

    private void verifyProductionIntegrity() {
        assertTrue(compareDeeply(this.getEntitiesAsDocuments(), getProduction()));
    }

    @BeforeAll
    public void init() { resetDB(); }

    @AfterEach
    public void teardown() {
        resetDB();
    }

    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

}
