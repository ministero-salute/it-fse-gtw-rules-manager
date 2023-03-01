/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl.SchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockRequests.runScheduler;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles(TEST)
@AutoConfigureMockMvc
class EDSControllerTest {

	@MockBean
	public InvokeEDSClientScheduler scheduler; 
	
	@Autowired
	public SchedulerCTL controller;

	@Autowired
	private MockMvc mvc;
	
	@Test
	void runOK() throws Exception {
		// Prevent running logic
		doNothing().when(scheduler).asyncAction();
		// Test response
		mvc.perform(runScheduler()).andExpect(status().is2xxSuccessful());
	}

	@Test
	void runKO() throws Exception {
		// Create resource lock
		when(scheduler.isRunning()).thenReturn(true);
		// Test response
		mvc.perform(runScheduler()).andExpect(status().isLocked());
	}
	
}
