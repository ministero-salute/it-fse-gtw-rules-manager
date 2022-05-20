package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

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
	public SchemaETY findById(String pk) {
		return super.findByID(pk);
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
	public Integer upsertByVersion(final SchemaETY ety, final Boolean rootSchema) {
		Integer output = 0;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("version").is(ety.getVersion()).and("cda_type").is(ety.getCdaType()).and("name_schema").is(ety.getNameSchema()));
			
			Update update = new Update();
			update.set("cda_type", ety.getCdaType());
			update.set("name_schema", ety.getNameSchema());
			update.set("content_schema", ety.getContentSchema());
			update.set("version", ety.getVersion());
			
			if(Boolean.TRUE.equals(rootSchema)) {
				update.set("root_schema", true);
			}
			
			UpdateResult uResult = mongoTemplate.upsert(query, update, SchemaETY.class);
			output = (int)uResult.getModifiedCount();
		} catch(Exception ex) {
			log.error("Error upserting ety schema " , ex);
			throw new BusinessException("Error inserting ety schema ", ex);
		}
		return output;
	
	}
	
	@Override
	public List<SchemaETY> findAllChildrenSchemaByVersion(final String version) {
		List<SchemaETY> out = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("version").is(version).and("root_schema").ne(true));
			out = mongoTemplate.find(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while find all children schema by version " , ex);
			throw new BusinessException("Error while find all children schema by version ", ex);
		}
		return out;
	}

	@Override
	public boolean existByVersion(final String version) {
		boolean output = false;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("version").is(version));
			output = mongoTemplate.exists(query, SchemaETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
		return output;
	}

}
 		
