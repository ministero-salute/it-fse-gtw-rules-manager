/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.executors;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSTermsDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockDictionaryExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.DerivedActionEDS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler.EXPECTED_DICTIONARIES;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler.EXPECTED_SYSTEMS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
class DictExecutorTest {
	
	@SpyBean
    private MongoTemplate mongo;
    @Autowired
    private MockDictionaryExecutor executor;
    @Autowired
    private EDSTermsDB db;
    
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
    void processing() {
    	assertDoesNotThrow(() -> {
    		// Setup staging
        	assertEquals(OK, executor.createStaging());
        	db.setupStaging();
        	assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
            // Call processing
            assertEquals(OK, executor.onProcessing());
            List<TerminologyETY> terminologies = mongo.findAll(TerminologyETY.class, executor.getConfig().getStaging());
            assertEquals(EXPECTED_DICTIONARIES, terminologies.size());
            // If this return false, at least one element has a different system from the one declared
            assertTrue(
                terminologies.stream().map(TerminologyETY::getSystem).allMatch(EXPECTED_SYSTEMS::contains)
            );
    	});
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
        assertArrayEquals(DerivedActionEDS.defaults(), executor.getSteps());
    }
    
    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        mongo.dropCollection(executor.getConfig().getBackup());
    }
    
}
