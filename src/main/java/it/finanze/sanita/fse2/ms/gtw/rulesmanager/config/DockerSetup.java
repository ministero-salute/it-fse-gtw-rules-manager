package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.EngineCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.FhirStructuresCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.EnginesExecutor;
import lombok.extern.slf4j.Slf4j;

@Profile(value = Constants.Profile.DOCKER)
@Configuration
@Slf4j
public class DockerSetup {

	@Autowired
	private EnginesExecutor engineExecutors;
	
	@Autowired
	private IExecutorRepo executorRepo;
	
	@Autowired
	private FhirStructuresCFG fhirStructuresCFG;
	
	@Autowired
	private EngineCFG engineCFG;
	
	@EventListener(value = ApplicationStartedEvent.class)
	public void dockerInit() throws EdsDbException {
		log.info("Docker init engines start");
		executorRepo.drop(engineCFG.getProduction());
		log.info("Delete collection engine production");
 		executorRepo.clone(fhirStructuresCFG.getProduction(), fhirStructuresCFG.getStaging());
		engineExecutors.execute();
		executorRepo.drop(fhirStructuresCFG.getStaging());
		log.info("Delete collection transform-staging");
	}
	
}
