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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.DictionaryCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.EngineCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.FhirStructuresCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.DictionaryExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.EnginesExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@Profile(value = Constants.Profile.DOCKER)
@Configuration
@Slf4j
public class DockerSetup {

	// Executors
	@Autowired
	private EnginesExecutor engineExecutor;
	
	@Autowired
	private DictionaryExecutor dictionaryExecutor;

	// Data-layer
	@Autowired
	private IExecutorRepo repository;

	// Configurations
	@Autowired
	private FhirStructuresCFG transforms;
	
	@Autowired
	private EngineCFG engine;

	@Autowired
	private DictionaryCFG dictionary;
	
	@Autowired
	private TerminologyCFG terminology;

	@EventListener(value = ApplicationStartedEvent.class)
	public void init() throws EdsDbException {
		log.info("[DOCKER] Setup has started");
		setupEngine();
		setupDictionary();
		log.info("[DOCKER] Setup has finished");
	}

	private void setupEngine() throws EdsDbException {
		log.info("[DOCKER] Starting docker-routine to initialize engine");
		repository.drop(engine.getProduction());
		log.info("[DOCKER] Clean-up previous engines, if they exist");
		repository.clone(transforms.getProduction(), transforms.getStaging());
		log.info("[DOCKER] Spawning engine ...");
		engineExecutor.execute();
		log.info("[DOCKER] Engine spawned!");
		repository.drop(transforms.getStaging());
		repository.drop(engine.getBackup());
		log.info("[DOCKER] Cleaning-up");
	}

	private void setupDictionary() throws EdsDbException {
		log.info("[DOCKER] Starting docker-routine to initialize dictionary");
		repository.drop(dictionary.getProduction());
		log.info("[DOCKER] Clean-up previous dictionaries, if they exist");
		repository.clone(terminology.getProduction(), terminology.getStaging());
		log.info("[DOCKER] Creating dictionaries ...");
		dictionaryExecutor.execute();
		log.info("[DOCKER] Dictionaries created!");
		repository.drop(terminology.getStaging());
		repository.drop(dictionary.getBackup());
		log.info("[DOCKER] Cleaning-up");
	}


}
