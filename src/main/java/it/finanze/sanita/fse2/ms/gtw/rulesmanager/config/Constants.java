
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

import org.springframework.beans.factory.annotation.Value;

/**
 * 
 *
 *         Constants application.
 */
public final class Constants {

	
	@Value("${gtw.statusmanager.test.statusUrl}")
	public static String STATUS_URL; 
	
	@Value("${gtw.statusmanager.test.dataUrl}")
	public static String DATA_URL; 
	
	@Value("${gtw.statusmanager.test.mock}")
	public static String MOCK; 
	
	/**
	 * Path scan.
	 */
	public static final class ComponentScan {

		/**
		 * Base path.
		 */
		public static final String BASE = "it.finanze.sanita.fse2.ms.gtw.rulesmanager";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller";

		/**
		 * Service path.
		 */
		public static final String SERVICE = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.service";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.config";

		/**
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.config";

		/**
		 * Configuration mongo repository path.
		 */
		public static final String REPOSITORY = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository";

		public static final String UTILITY = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility";
		
		public static final String SCHEDULER_QUERIES = "it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity";


		private ComponentScan() {
			// This method is intentionally left blank.
		}

	}
	
	public static final class Collections {

		public static final String SCHEMA = "schema";

		public static final String SCHEMATRON = "schematron";

		public static final String TERMINOLOGY = "terminology";

		public static final String FHIR_TRANSFORM = "transform";
		
		public static final String DICTIONARY = "dictionary";
		public static final String ENGINES = "engines";

		private Collections() {

		}
	}

	public static final class AppConstants {
		
		private AppConstants() {}
		
		public static final String MOCKED_GATEWAY_NAME = "mocked-gateway";
		
		public static final String LOG_TYPE_KPI = "kpi-structured-log";
		public static final String LOG_TYPE_CONTROL = "control-structured-log";
		
	}

	public static final class Profile {

		/**
		 * Test profile.
		 */
		public static final String TEST = "test";

		public static final String NOT_TEST = "!" + TEST;

		public static final String TEST_PREFIX = "test_";

		/**
		 * Dev profile.
		 */
		public static final String DEV = "dev";

		/**
		 * Dev profile.
		 */
		public static final String DOCKER = "docker";

		/**
		 * Constructor.
		 */
		private Profile() {
			// This method is intentionally left blank.
		}

	}

	/**
	 * Constants.
	 */
	private Constants() {

	}

	public static final class Logs {
		public static final String ERR_SCH_RUNNING = "Il processo di aggiornamento risulta già avviato";
		public static final String DTO_RUN_TASK_QUEUED = "Processo avviato, verifica i logs";
	}

}
