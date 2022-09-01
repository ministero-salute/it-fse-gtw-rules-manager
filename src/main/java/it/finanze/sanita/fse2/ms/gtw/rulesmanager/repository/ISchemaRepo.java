package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;

/**
 * @author vincenzoingenito
 *
 * Schema interface repository.
 */
public interface ISchemaRepo extends Serializable {

	/**
	 * Insert a Schema on database.
	 * 
	 * @param ety Schema to insert.
	 * @return Schema inserted.
	 */
	SchemaETY insert(SchemaETY ety);
 

	/**
	 * Inserts all schemas.
	 * 
	 * @param etys List of schemas to insert.
	 */
	void insertAll(List<SchemaETY> etys);
 

	/**
	 * Return a list of all Schema.
	 * 
	 * @return List of all Schema.
	 */
	List<SchemaETY> findAll();
	 
	
	boolean existByTypeIdExtension(String typeIdExtension);
	
	void dropCollection();
	 
}
