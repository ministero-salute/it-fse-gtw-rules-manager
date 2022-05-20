package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;

/**
 *	@author vincenzoingenito
 *
 *	Schematron interface service.
 */
public interface ISchematronSRV extends Serializable {

	SchematronETY insert(SchematronETY ety);
	
	void insertAll(List<SchematronETY> etys);
	
	SchematronETY findById(String id);
	
	Integer saveNewVersionSchematron(List<SchematronDTO> schematronDTO);

}
