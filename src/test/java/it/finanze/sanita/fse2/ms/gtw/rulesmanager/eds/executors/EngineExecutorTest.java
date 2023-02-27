package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.executors;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockEnginesExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.DerivedActionEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.EngineSRV;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
class EngineExecutorTest {
	
	@SpyBean
    private MongoTemplate mongo;
    @SpyBean
    private MockEnginesExecutor executor;
    @SpyBean
    private IExecutorRepo repository;
    @MockBean
    private EngineSRV engines;
    
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
    void cleanBackup() {
    	// Call createBackup
    	assertEquals(OK, executor.createBackup());
    	assertTrue(mongo.collectionExists(executor.getConfig().getBackup()));
    	// Call onCleanBackup
    	assertEquals(OK, executor.onCleanBackup());
    	assertFalse(mongo.collectionExists(executor.getConfig().getBackup()));
    }
    
    @Test
    void getConfig() {
    	// Call getConfig
    	assertDoesNotThrow(() -> executor.getConfig());
    }

    @Test
    void processing() {
        assertDoesNotThrow(() -> {
            when(engines.synthesize(anyString(), any())).thenReturn(true);
            assertEquals(OK, executor.onProcessing());
            when(engines.synthesize(anyString(), any())).thenReturn(false);
            assertEquals(OK, executor.onProcessing());
            when(engines.synthesize(anyString(), any())).thenThrow(new EdsDbException("Test error"));
            assertEquals(KO, executor.onProcessing());
        });
    }

    @Test
    void recovery() throws EdsDbException {
        // Process staging and create backup
        assertEquals(OK, executor.onStaging());
        assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        assertTrue(mongo.collectionExists(executor.getConfig().getBackup()));
        // Attempt to restore backup as production
        assertEquals(CB_OK, executor.onRecovery());
        assertTrue(mongo.collectionExists(executor.getConfig().getProduction()));
        assertFalse(mongo.collectionExists(executor.getConfig().getBackup()));
        // Emulate runtime exception
        doThrow(new EdsDbException("Test error")).when(repository).rename(anyString(), anyString());
        assertEquals(CB_KO, executor.onRecovery());
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
        mongo.dropCollection(executor.getConfig().getBackup());
    }
    
}
