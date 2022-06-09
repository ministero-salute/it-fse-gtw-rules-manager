package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ISchemaRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Schema repository.
 */
@Slf4j
@Repository
public class SchemaRepo extends AbstractMongoRepo<SchemaETY, String> implements ISchemaRepo {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -4017623557412046071L;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public SchemaETY insert(final SchemaETY ety) {
		return super.insert(ety);
	}

	@Override
	public void insertAll(List<SchemaETY> etys) {
		super.insertAll(etys);
	}

	@Override
	public List<SchemaETY> findAll() {
		return super.findAll();
	}
	 
	@Override
	public boolean existByTypeIdExtension(final String typeIdExtension) {
		boolean output = false;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("type_id_extension").is(typeIdExtension));
			output = mongoTemplate.exists(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
		return output;
	}

	@Override
	public void dropCollection() {
		try {
			mongoTemplate.dropCollection(SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
	}
}
 		
