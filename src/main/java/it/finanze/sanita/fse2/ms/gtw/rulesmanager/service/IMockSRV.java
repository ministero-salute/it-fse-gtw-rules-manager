package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;
import java.util.Map;

public interface IMockSRV extends Serializable {

	
	void dropCollections();
	
	void saveMockConfigurationItem();
	
	Map<String,Integer> countSavedElement();
}
