package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.VocabularyETY;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary interface repository.
 */
public interface IVocabularyRepo extends Serializable {

	/**
	 * Inserts a vocabulary on database.
	 * 
	 * @param ety Vocabulary to insert.
	 * @return Vocabulary inserted.
	 */
	VocabularyETY insert(VocabularyETY ety);
	
	/**
	 * Returns a Vcard identified by its {@code pk}.
	 * 
	 * @param pk Primary key of the Vcard to return.
	 * @return Vcard identified by its {@code pk}.
	 */
	VocabularyETY findById(String pk);

	/**
	 * Inserts all vocabularies on database.
	 * 
	 * @param etys List of vocabularies to insert.
	 */
	void insertAll(List<VocabularyETY> etys);

	/**
	 * Returns all vocabularies.
	 * 
	 * @return List of all vocabularies.
	 */
	List<VocabularyETY> findAll();
	
	Boolean existsBySystem(String system);
	
	List<VocabularyETY> findByInCodeAndSystem(List<String> codes, String system);
	
}
