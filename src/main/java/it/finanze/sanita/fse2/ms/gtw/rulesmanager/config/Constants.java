package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config;

/**
 * 
 * @author vincenzoingenito
 *
 *         Constants application.
 */
public final class Constants {

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

		public static final String XSL_TRANSFORM = "xsl_transform";

		public static final String FHIR_TRANSFORM = "transform";

		private Collections() {

		}
	}

	public static final class AppConstants {
		
		public static final String MOCKED_GATEWAY_NAME = "mocked-gateway";
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

}
