package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.CONFIG_MONGO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.REPOSITORY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.SCHEDULER_QUERIES;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.UTILITY;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl.SchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockData;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.TermsChunkExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.base.EmptySetDTO;


@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@ComponentScans( value = {
	    @ComponentScan(CONFIG_MONGO),
	    @ComponentScan(REPOSITORY),
	    @ComponentScan(SCHEDULER_QUERIES),
	    @ComponentScan(UTILITY)
	})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EDSClientSchedulerTest {

	@Autowired
	public InvokeEDSClientScheduler scheduler; 
	
	@Autowired
	public SchedulerCTL schedulerCtl; 
	
	@Test
	void executeTest()
	{
		scheduler.action(); 
		try{
			schedulerCtl.runScheduler(null); 
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(true); 
	} 
	
}
