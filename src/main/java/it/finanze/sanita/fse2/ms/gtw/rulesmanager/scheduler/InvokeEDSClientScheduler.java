/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.DictionaryExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.FhirStructuresExecutors;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchemaExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchematronExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.TermsChunkExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryExecutorOnException;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryRecoveryOnException;
import static java.lang.String.format;

/**
 * Invoke EDS Client Scheduler, handles the invocations to EDS Client endpoints.
 */
@Slf4j
@Component
public class InvokeEDSClientScheduler {

	@Autowired
	private ProfileUtility profiles;

	@Autowired
	private SchemaExecutor schema;

	@Autowired
	private SchematronExecutor schematron;

	@Autowired
	private TermsChunkExecutor terminology;

	@Autowired
	private FhirStructuresExecutors fhir;

	@Autowired
	private DictionaryExecutor dictionary;

	private boolean running;


	@Async
	@EventListener(ApplicationStartedEvent.class)
	void initialize() {
		if(!profiles.isTestProfile() && !profiles.isDevOrDockerProfile()) {
			log.debug("[EDS] Executing post construct");
			action();
		}
	}

	@Async("single-thread-exec")
	public void asyncAction() {
		log.info("[EDS] Running on {}", Thread.currentThread().getName());
		action();
	}

	@Scheduled(cron = "${eds.scheduler.invoke}")
	@SchedulerLock(name = "invokeEDSClientScheduler")
	public void action() {
		// Set run flag
		running = true;
		// Log me
		log.info("[EDS] Starting scheduled updating process");
		// Setup executors
		setup();
		// Run executors
		start(schema, schematron, terminology, fhir);
		// Log me
		log.info("[EDS] Updating process completed");
		// Reset run flag
		running = false;
	}

	private void setup() {
		terminology.setOnBeforeSwap(
			() -> dictionary.execute().toCallback()
		);
		terminology.setOnFailedSwap(
			() -> dictionary.onRecovery()
		);
		terminology.setOnSuccessSwap(
			() -> dictionary.onCleanBackup().toCallback()
		);
	}
 
	private void start(ExecutorEDS<?> ...executor) {
		for (ExecutorEDS<?> executorEDS : executor) {
			// Configuration
			ChangesetCFG config = executorEDS.getConfig();
			// Verify execution result even after retries
			ActionRes exec = retryExecutorOnException(executorEDS, log);
			// Log if went wrong
			if (exec == KO) {
				log.error(format("[EDS] Unable to update the %s collection", config.getTitle()));
				// Init recovery sequence
				ActionRes recovery = retryRecoveryOnException(executorEDS, log);
				if(recovery == KO) {
					log.error(format("[EDS] Unable to recover the %s collection", config.getTitle()));
				}
			}
		}
	}

	public boolean isRunning() {
		return running;
	}
}
