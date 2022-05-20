package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.VocabularyETY;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary interface service.
 */
public interface IVocabularySRV extends Serializable {

	VocabularyETY insert(VocabularyETY ety);
	
	void insertAll(List<VocabularyETY> etys);
	
	Integer saveNewVocabularySystems(List<VocabularyDTO> vocabulariesDTO); 

}
