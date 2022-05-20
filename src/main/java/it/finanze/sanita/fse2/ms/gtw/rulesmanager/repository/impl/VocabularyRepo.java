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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IVocabularyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.VocabularyETY;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary repository.
 */
@Slf4j
@Repository
public class VocabularyRepo extends AbstractMongoRepo<VocabularyETY, String> implements IVocabularyRepo {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 5409183302250695125L;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public VocabularyETY insert(final VocabularyETY ety) {
		return super.insert(ety); 
	}

	@Override
	public VocabularyETY findById(String pk) {
		return super.findByID(pk);
	}

	@Override
	public void insertAll(List<VocabularyETY> etys) {
		super.insertAll(etys);
	}

	@Override
	public List<VocabularyETY> findAll() {
		return super.findAll();
	}

	public Integer upsertByCode(final VocabularyETY ety) {
		Integer output = 0;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("code").is(ety.getCode()).and("system").is(ety.getSystem()));
			
			Update update = new Update();
			update.set("system", ety.getSystem());
			update.set("description", ety.getDescription());
			update.set("code", ety.getCode());
			UpdateResult uResult = mongoTemplate.upsert(query, update, VocabularyETY.class);
			output = (int)uResult.getModifiedCount();
		} catch(Exception ex) {
			log.error("Error upserting ety schema " , ex);
			throw new BusinessException("Error inserting ety schema ", ex);
		}
		return output;
	}

	@Override
	public Boolean existsBySystem(final String system) {
		boolean output = false;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("system").is(system));
			output = mongoTemplate.exists(query, VocabularyETY.class);
 		} catch(Exception ex) {
			log.error("Error while execute exists by system :" , ex);
			throw new BusinessException("Error while execute exists by system :" , ex);
		}
		return output;
	}

	@Override
	public List<VocabularyETY> findByInCodeAndSystem(final List<String> codes, final String system) {
		List<VocabularyETY> output = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("code").in(codes).and("system").is(system));
			output = mongoTemplate.find(query, VocabularyETY.class);
		} catch(Exception ex) {
			log.error("Error while execute find by in code and system :" , ex);
			throw new BusinessException("Error while execute find by in code and system :" , ex);
		}
		return output;
	}
 
}
