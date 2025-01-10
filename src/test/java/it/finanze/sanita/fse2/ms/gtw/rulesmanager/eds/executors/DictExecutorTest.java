
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

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler.EXPECTED_DICTIONARIES;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler.EXPECTED_SYSTEMS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

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

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSTermsDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockDictionaryExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.DerivedActionEDS;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
class DictExecutorTest {
	
	@SpyBean
    private MongoTemplate mongo;
    @Autowired
    private MockDictionaryExecutor executor;
    @Autowired
    private IExecutorRepo executorRepo;
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
    void cleanBackup() throws EdsDbException {
    	// Call createBackup
    	assertEquals(OK, executor.createBackup());
//    	assertTrue(mongo.collectionExists(executor.getConfig().getBackup()));
    	assertTrue(executorRepo.exists(executor.getConfig().getBackup()));
    	
    	// Call onCleanBackup
    	assertEquals(OK, executor.onCleanBackup());
//    	assertFalse(mongo.collectionExists(executor.getConfig().getBackup()));
    	assertFalse(executorRepo.exists(executor.getConfig().getBackup()));
    }
    
    @Test
    void processing() throws Exception {
    	// Setup staging
    	assertEquals(OK, executor.createStaging());
    	db.setupStaging();
//    	assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
    	assertTrue(executorRepo.exists(executor.getConfig().getStaging()));
        // Call processing
        assertEquals(OK, executor.onProcessing());
        List<TerminologyETY> terminologies = mongo.findAll(TerminologyETY.class, executor.getConfig().getStaging());
        assertEquals(EXPECTED_DICTIONARIES, terminologies.size());
        // If this return false, at least one element has a different system from the one declared
        assertTrue(
            terminologies.stream().map(TerminologyETY::getSystem).allMatch(EXPECTED_SYSTEMS::contains)
        );
//    	assertDoesNotThrow(() -> {
//    		// Setup staging
//        	assertEquals(OK, executor.createStaging());
//        	db.setupStaging();
////        	assertTrue(mongo.collectionExists(executor.getConfig().getStaging()));
//        	assertTrue(executorRepo.exists(executor.getConfig().getStaging()));
//            // Call processing
//            assertEquals(OK, executor.onProcessing());
//            List<TerminologyETY> terminologies = mongo.findAll(TerminologyETY.class, executor.getConfig().getStaging());
//            assertEquals(EXPECTED_DICTIONARIES, terminologies.size());
//            // If this return false, at least one element has a different system from the one declared
//            assertTrue(
//                terminologies.stream().map(TerminologyETY::getSystem).allMatch(EXPECTED_SYSTEMS::contains)
//            );
//    	});
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
