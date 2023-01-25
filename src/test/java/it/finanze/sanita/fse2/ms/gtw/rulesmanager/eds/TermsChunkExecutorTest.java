package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.CONFIG_MONGO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.REPOSITORY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.SCHEDULER_QUERIES;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.UTILITY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.createChangeset;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.emptyChangesetChunk;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.emptyChangeset;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor.createChangesetChunk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

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

import com.mongodb.client.MongoCollection;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSDatabaseHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.TermsChunkExecutor;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@ComponentScans( value = {
	    @ComponentScan(CONFIG_MONGO),
	    @ComponentScan(REPOSITORY),
	    @ComponentScan(SCHEDULER_QUERIES),
	    @ComponentScan(UTILITY)
	})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TermsChunkExecutorTest extends EDSDatabaseHandler {

	@Autowired
	private TermsChunkExecutor executor; 
	
	@Autowired
	private MockExecutor mockExecutor; 
	
    @MockBean
    private IEDSClient client; 
	
    @SpyBean
    private MongoTemplate mongo; 
    
	
	void clean() {
        // Setup production
        setupProduction();
        // Create collection
        mongo.createCollection(mockExecutor.getConfig().getStaging());
        assertTrue(mongo.collectionExists(mockExecutor.getConfig().getStaging()));
        // Execute
        assertEquals(OK, mockExecutor.onClean());
        // Verify
        assertFalse(mongo.collectionExists(mockExecutor.getConfig().getStaging()));
        // No collection exists, call executor without collection
        assertEquals(OK, mockExecutor.onClean());
        // Verify production integrity
        verifyProductionIntegrity();
	} 

	
   /*  @Test
    void changesetCopied() throws EdsClientException {
        // Setup production
        setupProduction();
        executor.setSnapshot(new ChangeSetChunkDTO());
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(emptyChangeset());
        when(client.getChunkIns(any(), any(), anyInt(), any())).thenReturn(emptyChangesetChunk());
        // Changeset should be retrieved correctly
        assertEquals(KO, executor.onChangeset(executor.onLastUpdateProd()));
        assertEquals(OK, mockExecutor.onChangeset(mockExecutor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenReturn(createChangeset(10, 0, 10));
        when(client.getChunkIns(any(), any(), anyInt(), any())).thenReturn(createChangesetChunk());
        // Changeset is not empty, it should be OK
        assertEquals(OK, mockExecutor.onChangeset(mockExecutor.onLastUpdateProd()));
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getStatus(any(), any(), any())).thenThrow(
            new EdsClientException("Test error", new IOException("Test error"))
        );
        // Get status errored, it should be KO
        assertEquals(KO, mockExecutor.onChangeset(mockExecutor.onLastUpdateProd()));
        // Verify production integrity
        verifyProductionIntegrity();
        
    } */ 
    
   /*  @Test
    void changeset() throws EdsClientException {
        // Setup production
        setupProduction();
        // Provide knowledge
        when(client.getChunkIns(any(), any(), anyInt(), any())).thenReturn(emptyChangesetChunk());
        // Changeset should be retrieved correctly
        executor.onChangeset(executor.onLastUpdateProd()); 
        assertEquals(KO, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getChunkIns(any(), any(), anyInt(), any())).thenReturn(createChangesetChunk());
        // Changeset is not empty, it should be OK
        executor.onChangeset(executor.onLastUpdateProd()); 
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getChunkIns(any(), any(), anyInt(), any())).thenThrow(
            new EdsClientException("Test error", new IOException("Test error"))
        );
        // Get status errored, it should be KO
        assertEquals(KO, executor.onChangeset(executor.onLastUpdateProd()));
        // Verify production integrity
        verifyProductionIntegrity();  
        
    } */ 
	

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
