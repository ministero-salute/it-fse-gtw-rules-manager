package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClientV2;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSDatabaseHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.mock.MockData;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.mock.MockExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICollectionsRepo;
import org.junit.jupiter.api.AfterEach;
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

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.mock.MockExecutor.createChangeset;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.mock.MockExecutor.emptyChangeset;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
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
        // Create collection
        mongo.createCollection(executor.getConfig().getStaging());
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        // Execute
        assertEquals(OK, executor.onClean());
        // Verify
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
    }

    @Test
    void changeset() throws EdsClientException {
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(emptyChangeset());
        // Changeset is empty, it should quit
        assertEquals(RETURN, executor.onChangeset());
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(createChangeset(10, 0, 0));
        // Changeset is not empty, it should be OK
        assertEquals(OK, executor.onChangeset());
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenThrow(
            new EdsClientException("Test error", new IOException("Test error"))
        );
        // Get status errored, it should be KO
        assertEquals(KO, executor.onChangeset());
    }

    @Test
    void staging() {
        // Case #1 - Production exists
        // Create collection if not exists
        mongo.createCollection(executor.getConfig().getSchema());
        assertTrue(mongo.collectionExists(executor.getConfig().getSchema()));
        // Execute
        assertEquals(executor.onStaging(), OK);
        // Now staging and production co-exists
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        assertTrue(mongo.collectionExists(executor.getConfig().getSchema()));
        // Emptying database
        resetDB();
        // Case #2 - Production not exists
        assertFalse(mongo.collectionExists(executor.getConfig().getSchema()));
        // Execute
        assertEquals(executor.onStaging(), OK);
        // There is only staging
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getSchema()));
    }

    @Test
    void processing() {
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
    }

    @Test
    void verify() {
        // Setup changeset
        executor.setChangeset(MockExecutor.createChangeset(10, 5, 1));
        // Call processing with verified flag
        assertEquals(executor.onProcessing(true), OK);
        assertEquals(executor.onVerify(), OK);
        // Call processing with unverified flag
        assertEquals(executor.onProcessing(false), OK);
        assertEquals(executor.onVerify(), KO);
    }

    @Test
    void swap() throws IOException, EdsDbException {
        // Setup repositories with documents
        this.setupTestRepository(executor.getConfig().getStaging());
        this.setupTestRepository(executor.getConfig().getSchema());
        // Call swap
        assertEquals(executor.onSwap(), OK);
        // Staging is removed, schema is kept
        assertTrue(repository.exists(executor.getConfig().getSchema()));
        assertFalse(repository.exists(executor.getConfig().getStaging()));
        // Drop it
        this.clearTestRepository(executor.getConfig().getSchema());
    }

    @Test
    void sync() throws IOException, EdsDbException {
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
        assertEquals(changeset.getTimestamp(), lastSync);
        // Drop it
        this.clearTestRepository(executor.getConfig().getStaging());
    }

    @AfterEach
    public void teardown() {
        resetDB();
    }

    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getSchema());
        mongo.dropCollection(executor.getConfig().getStaging());
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getSchema()));
    }

}
