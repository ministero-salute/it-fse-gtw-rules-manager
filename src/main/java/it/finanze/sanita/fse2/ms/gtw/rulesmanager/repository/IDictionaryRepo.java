package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;

public interface IDictionaryRepo extends Serializable {

	/**
	 * Insert a dictionary files on database.
	 * 
	 * @param ety Schema to insert.
	 * @return Schema inserted.
	 */
	void insertAll(List<DictionaryETY> etys);
	
	void dropCollection();
}
