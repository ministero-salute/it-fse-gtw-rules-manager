package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary interface repository.
 */
public interface ITerminologyRepo extends Serializable {

	/**
	 * Inserts a vocabulary on database.
	 * 
	 * @param ety Vocabulary to insert.
	 * @return Vocabulary inserted.
	 */
	TerminologyETY insert(TerminologyETY ety);
	
	/**
	 * Returns a Vcard identified by its {@code pk}.
	 * 
	 * @param pk Primary key of the Vcard to return.
	 * @return Vcard identified by its {@code pk}.
	 */
	TerminologyETY findById(String pk);

	/**
	 * Inserts all vocabularies on database.
	 * 
	 * @param etys List of vocabularies to insert.
	 */
	void insertAll(List<TerminologyETY> etys);

	/**
	 * Returns all vocabularies.
	 * 
	 * @return List of all vocabularies.
	 */
	List<TerminologyETY> findAll();
	
	Boolean existsBySystem(String system);
	
	List<TerminologyETY> findByInCodeAndSystem(List<String> codes, String system);
	
	void dropCollection();
	
}
