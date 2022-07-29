package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;

public interface IMockSRV extends Serializable {

	
	void dropCollections();
	
	void saveMockConfigurationItem();
}
