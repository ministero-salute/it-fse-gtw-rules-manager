package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary interface service.
 */
public interface ITerminologySRV extends Serializable {

	TerminologyETY insert(TerminologyETY ety);
	
	void insertAll(List<TerminologyETY> etys);
	
	Integer saveNewVocabularySystems(List<VocabularyDTO> vocabulariesDTO);
	

}
