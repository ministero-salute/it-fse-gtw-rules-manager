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
	 * Return a Schema identified by its {@code pk}.
	 * 
	 * @param pk Primary key of the Schema to return.
	 * @return Schema identified by its {@code pk}.
	 */
	SchemaETY findById(String pk);

	/**
	 * Inserts all schemas.
	 * 
	 * @param etys List of schemas to insert.
	 */
	void insertAll(List<SchemaETY> etys);

	/**
	 * Execute upsert on schema.
	 * 
	 * @param ety 	  Schema to upsert.
	 * @param boolean Root schema.
	 * @return Number of schemas upserted.
	 */
	Integer upsertByVersion(SchemaETY ety, Boolean rootSchema);

	/**
	 * Return a list of all Schema.
	 * 
	 * @return List of all Schema.
	 */
	List<SchemaETY> findAll();
	
	List<SchemaETY> findAllChildrenSchemaByVersion(String version);
	
	boolean existByVersion(String version);
}
