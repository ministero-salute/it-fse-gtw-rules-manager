package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClientV2;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSDatabaseHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockData;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICollectionsRepo;
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
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.createChangeset;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.emptyChangeset;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EDSExecutorTest extends EDSDatabaseHandler {

    @SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClientV2 client;
    @Autowired
    private MockExecutor executor;
    @Autowired
    private ICollectionsRepo repository;

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
        assertEquals(RETURN, executor.onChangesetEmpty());
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(createChangeset(10, 0, 0));
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
        assertEquals(executor.onStaging(), OK);
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
        assertEquals(executor.onStaging(), OK);
        // There is only staging
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

    @Test
    void processing() {
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setChangeset(MockExecutor.createChangeset(10, 5, 1));
        // Call processing with verified flag
        assertEquals(executor.onProcessing(true), OK);
        // Now check size
        assertEquals(10, executor.getOperations().getInsertions());
        assertEquals(5, executor.getOperations().getModifications());
        assertEquals(1, executor.getOperations().getDeletions());
        assertEquals(16, executor.getOperations().getOperations());
        // Call processing with verified flag
        assertEquals(executor.onProcessing(false), OK);
        // Now check size
        assertEquals(0, executor.getOperations().getInsertions());
        assertEquals(0, executor.getOperations().getModifications());
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
        executor.setChangeset(MockExecutor.createChangeset(10, 5, 1));
        // Call processing with verified flag
        assertEquals(executor.onProcessing(true), OK);
        assertEquals(executor.onVerify(), OK);
        // Call processing with unverified flag
        assertEquals(executor.onProcessing(false), OK);
        assertEquals(executor.onVerify(), KO);
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
        assertEquals(executor.onSwap(
            mongo.getCollection(executor.getConfig().getStaging())
        ), OK);
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
        ChangeSetDTO<MockData> changeset = createChangeset(10, 5, 1);
        // Setup changeset
        executor.setChangeset(changeset);
        // Call it
        assertEquals(executor.onSync(), OK);
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
        ChangeSetDTO<MockData> changeset = createChangeset(0,0,0);
        // Set
        executor.setChangeset(changeset);
        // Call it
        assertEquals(executor.onChangesetAlignment(), OK);
        // Create changeset
        changeset = createChangeset(10,5,3);
        // Set
        executor.setChangeset(changeset);
        // Call it
        assertEquals(executor.onChangesetAlignment(), KO);
        // Verify production integrity
        verifyProductionIntegrity();
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
