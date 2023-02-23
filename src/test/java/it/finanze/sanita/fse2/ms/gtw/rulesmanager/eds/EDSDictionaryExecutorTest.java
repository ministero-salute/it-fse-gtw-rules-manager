package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

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

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockDictionaryExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.DerivedActionEDS;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
class EDSDictionaryExecutorTest {
	
	@SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClient client;
    @Autowired
    private MockDictionaryExecutor executor;
    @SpyBean
    private IExecutorRepo repository;
    
    @BeforeAll
    public void init() { resetDB(); }

    @AfterEach
    public void reset() { resetDB(); }
    
    @Test
    void onClean() {
    	// Call onClean
    	assertEquals(OK, executor.onClean());
    }
    
    @Test
    void cleanBackup() throws EdsDbException {
    	// Call createBackup
    	assertEquals(OK, executor.createBackup());
    	assertTrue(mongo.collectionExists(executor.getConfig().getBackup()));
    	
    	// Call onCleanBackup
    	assertEquals(OK, executor.onCleanBackup());
    	assertFalse(mongo.collectionExists(executor.getConfig().getBackup()));
    }
    
    @Test
    void processing() {
    	// Setup staging
    	assertEquals(OK, executor.createStaging());
    	assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        // Call processing
        assertEquals(OK, executor.onProcessing());
    }
    
    @Test
    void recovery() {
    	// Call createStaging
    	assertEquals(OK, executor.createStaging());
    	// Call createBackup
    	assertEquals(OK, executor.createBackup());
    	// Call onRecovery
    	assertEquals(CB_OK, executor.onRecovery());
    }
    
    @Test
    void getConfig() {
    	// Call getConfig
    	assertDoesNotThrow(() -> executor.getConfig());
    }
    
    @Test
    void getSteps() {
    	// Steps expected
    	String[] stepsExpected = DerivedActionEDS.defaults();
    	String[] stepsResult = executor.getSteps();
    	
    	assertEquals(stepsExpected.length, stepsResult.length);
    	
    	for(int i=0; i < stepsResult.length; i++) {
    		assertEquals(stepsExpected[i], stepsResult[i]);
    	}
    }
    
    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }
    
}
