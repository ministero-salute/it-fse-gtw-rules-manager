package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config;

/**
 * 
 * @author vincenzoingenito
 *
 * Constants application.
 */
public final class Constants {

	/**
	 *	Path scan.
	 */
	public static final class ComponentScan {

		/**
		 * Base path.
		 */
		public static final String BASE = "it.sanita.rulesmanager";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.sanita.rulesmanager.controller";

		/**
		 * Service path.
		 */
		public static final String SERVICE = "it.sanita.rulesmanager.service";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.sanita.rulesmanager.config";
		
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


		public static final class Collections {

			public static final String SCHEMA = "schema";

			public static final String SCHEMATRON = "schematron";

			public static final String TERMINOLOGY = "terminology";

			public static final String XSL_TRANSFORM = "xsl_transform";
			
			public static final String STRUCTURE_DEFINITION = "structure_definition";
			
			public static final String STRUCTURE_MAP = "structure_map";
			
			public static final String STRUCTURE_VALUESET = "structure_valueset";

			public static final String STRUCTURES = "structures";

			private Collections() {

			}
		}
		
		private ComponentScan() {
			//This method is intentionally left blank.
		}

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
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
		}

	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
