package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl.SchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;


@SpringBootTest
@ActiveProfiles(TEST)
public class EDSClientSchedulerTest {

	@Autowired
	public InvokeEDSClientScheduler scheduler; 
	
	@Autowired
	public SchedulerCTL controller;
	
	@Test
	void runKO() {}
	
}
