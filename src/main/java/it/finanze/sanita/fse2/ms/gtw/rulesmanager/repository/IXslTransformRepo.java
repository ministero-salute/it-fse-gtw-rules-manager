package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;

/**
 *	@author vincenzoingenito
 *
 *	Xsl transform interface repository.
 */
public interface IXslTransformRepo extends Serializable {

	/**
	 * Inserts a xsl transform on database.
	 * 
	 * @param ety Xsl transform to insert.
	 * @return Xsl transform inserted.
	 */
	XslTransformETY insert(XslTransformETY ety);
	
	/**
	 * Returns a xsl transform identified by its {@code pk}.
	 * 
	 * @param pk Primary key of the xsl transform to return.
	 * @return Xsl transform identified by its {@code pk}.
	 */
	XslTransformETY findById(String pk);
	
	/**
	 * Insert all entities on database.
	 * 
	 * @param etys List of entities to insert.
	 */
	void insertAll(List<XslTransformETY> etys);

	/**
	 * Returns all xsl transforms.
	 * 
	 * @return List of all xsl transforms.
	 */
	List<XslTransformETY> findAll();
	
	
	 boolean existByVersion(String version);
	 
	 void dropCollection();
}
