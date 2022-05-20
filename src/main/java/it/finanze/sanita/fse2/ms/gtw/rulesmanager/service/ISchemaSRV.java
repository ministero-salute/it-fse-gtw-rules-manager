package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;

/**
 *	@author vincenzoingenito
 *
 *	Schema interface service.
 */
public interface ISchemaSRV extends Serializable {

	SchemaETY insert(SchemaETY ety);
	
	void insertAll(List<SchemaETY> etys);
	
	Integer saveNewVersionSchema(SchemaDTO schemaDTO);
}
