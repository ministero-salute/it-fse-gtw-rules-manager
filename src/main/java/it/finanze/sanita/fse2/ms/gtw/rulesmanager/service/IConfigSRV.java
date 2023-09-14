package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

public interface IConfigSRV {

	String getEdsStrategy();

	boolean isNoEds();
	
	boolean isNoFhirEds();

	boolean isNoEdsWithLogs();

	boolean areLogsEnabled();
}
