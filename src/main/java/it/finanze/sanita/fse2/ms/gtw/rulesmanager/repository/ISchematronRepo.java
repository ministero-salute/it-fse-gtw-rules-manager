package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;

/**
 *	@author vincenzoingenito
 *
 *	Schemamatron interface repository.
 */
public interface ISchematronRepo extends Serializable {

	/**
	 * Insert a schematron on database.
	 * 
	 * @param ety Schematron to insert.
	 * @return Schematron inserted.
	 */
	SchematronETY insert(SchematronETY ety);
	
	/**
	 * Returns a Schematron identified by its {@code pk}.
	 * 
	 * @param pk Primary key of the Schematron to return.
	 * @return Schematron identified by its {@code pk}.
	 */
	SchematronETY findById(String pk);
	
	/**
	 * Insert all schematrons on database.
	 * 
	 * @param etys List of schematrons to insert.
	 */
	void insertAll(List<SchematronETY> etys);

	/**
	 * Returns all schematrons.
	 * 
	 * @return List of all schematrons.
	 */
	List<SchematronETY> findAll();

	
	boolean existByTemplateIdRoot(String templateIdRoot);
	
	void dropCollection();
}
