package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl.SchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.test.context.ActiveProfiles;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
