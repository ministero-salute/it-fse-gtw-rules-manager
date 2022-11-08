/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryExecutorOnException;
import static java.lang.String.format;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IDictionaryRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.FhirStructuresExecutors;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchemaExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchematronExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.XslExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.TermsChunkExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICodeSystemVersionSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * Invoke EDS Client Scheduler, handles the invocations to EDS Client endpoints.
 * @author Riccardo Bonesi
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
	private XslExecutor xsl;

	@Autowired
	private TermsChunkExecutor terminology;

	@Autowired
	private FhirStructuresExecutors fhirExecutor;

	@Autowired
	private ICodeSystemVersionSRV codeSystemVersionSRV;
	
	@PostConstruct
	public void postConstruct() {
		if(!profiles.isTestProfile() && !profiles.isDevOrDockerProfile()) {
			log.debug("[EDS] Executing post construct");
			action();
		}
	}
	
	@Scheduled(cron = "${eds.scheduler.invoke}")
	@SchedulerLock(name = "invokeEDSClientScheduler")
	public void action() { 
		// Log me
		log.info("[EDS] Starting scheduled updating process");
		// Run executors
		start(schema, schematron, xsl, terminology,fhirExecutor); 
		// Log me
		log.info("[EDS] Updating process completed");
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
			}
		}
		
		codeSystemVersionSRV.syncCodeSystemVersions(); 
	}
}
