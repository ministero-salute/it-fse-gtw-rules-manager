package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.executors;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTransformHandler.EXPECTED_ENGINE_FILES;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTransformHandler.EXPECTED_ENGINE_ROOTS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSTransformDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTransformHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockEnginesExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.EngineETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.DerivedActionEDS;

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
    @Autowired
    private EDSTransformDB db;
    @SpyBean
    private EDSTransformHandler handler;
    
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
        	db.setupStaging();
        	assertEquals(OK, executor.onStaging());
        	assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
        	assertEquals(OK, executor.onProcessing());
        	List<EngineETY> engines = mongo.findAll(EngineETY.class , executor.getConfig().getStaging());
        	assertEquals(1, engines.size());
        	assertEquals(EXPECTED_ENGINE_FILES, engines.get(0).getFiles().size());
        	assertEquals(EXPECTED_ENGINE_ROOTS, engines.get(0).getRoots().size());
        	// Integrity check
        	String[] id = engines.get(0).getFiles().stream().map((x) -> x.toHexString()).toArray(String[]::new);
        	String[] fixtures = handler.getEntities().stream().map((x) -> x.getId()).toArray(String[]::new);
        	assertArrayEquals(id, fixtures);
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
