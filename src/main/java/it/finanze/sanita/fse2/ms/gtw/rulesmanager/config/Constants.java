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
		public static final String CONFIG_MONGO = "it.sanita.rulesmanager.mongo";
		
		/**
		 * Configuration mongo repository path.
		 */
		public static final String REPOSITORY_MONGO = "it.sanita.rulesmanager.repository";
		 
		
		private ComponentScan() {
			//This method is intentionally left blank.
		}

	}
 
	public static final class Profile {

		/**
		 * Test profile.
		 */
		public static final String TEST = "test";

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
