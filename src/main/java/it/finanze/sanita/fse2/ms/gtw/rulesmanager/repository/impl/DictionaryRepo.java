package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IDictionaryRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class DictionaryRepo extends AbstractMongoRepo<DictionaryETY, String> implements IDictionaryRepo {

	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -5236847076186265946L;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void insertAll(final List<DictionaryETY> etys) {
		super.insertAll(etys);
	}

	@Override
	public void dropCollection() {
		try {
			mongoTemplate.dropCollection(DictionaryETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
	}
}
