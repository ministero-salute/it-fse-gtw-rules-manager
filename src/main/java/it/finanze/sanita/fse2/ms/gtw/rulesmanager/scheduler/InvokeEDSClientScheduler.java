package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static java.util.Arrays.stream;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchemaExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IMockSRV;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * Invoke EDS Client Scheduler, handles the invocations to EDS Client endpoints.
 * @author G. Baittiner
 */
@Slf4j
@Component
public class InvokeEDSClientScheduler implements IActionRetryEDS {

	@Autowired
	private SchemaExecutor schema;

	@Autowired
	private IMockSRV mockSRV;

	@Autowired
	private Environment environment;

	@PostConstruct
	public void postConstruct() {
		if(!isDevProfile()) {
			mockSRV.dropCollections();
			mockSRV.saveMockConfigurationItem();
			if(!isTestProfile()) {
				log.info("Executing post construct");
				action();
			}
		}
	}
	
	@Scheduled(cron = "${eds.scheduler.invoke}")
	@SchedulerLock(name = "invokeEDSClientScheduler")
	public void action() {
		log.info("Starting scheduled updating process");
		// Verify execution result even after retries
		ActionRes schemaExec = retryExecutorOnException(schema, log);
		// Log if went wrong
		if(schemaExec == KO) {
			log.error("Unable to update the schema collection");
		}

//		// Verify execution result even after retries
//		ActionRes schematronExec = retryExecutorOnException(schematron, log);
//		// Log if went wrong
//		if(schematronExec == KO) {
//			log.error("Unable to update the schematron collection");
//		}

		log.info("Updating process completed");
	}

	private boolean isTestProfile() {
		// Get profiles
		String[] profiles = environment.getActiveProfiles();
		// Verify if exists the test profile
		Optional<String> exists = stream(profiles).filter(i -> i.equals(Constants.Profile.TEST)).findFirst();
		// Return
		return exists.isPresent();
	}
	
	private boolean isDevProfile() {
		// Get profiles
		String[] profiles = environment.getActiveProfiles();
		// Verify if exists the test profile
		Optional<String> exists = stream(profiles).filter(i -> i.equals(Constants.Profile.DEV)).findFirst();
		// Return
		return exists.isPresent();
	}
}
