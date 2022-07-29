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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary repository.
 */
@Slf4j
@Repository
public class TerminologyRepo extends AbstractMongoRepo<TerminologyETY, String> implements ITerminologyRepo {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 5409183302250695125L;
	
	private static final String SYSTEM_STRING = "system";

	@Autowired
	private transient MongoTemplate mongoTemplate;
	
	@Override
	public TerminologyETY insert(final TerminologyETY ety) {
		return super.insert(ety); 
	}

	@Override
	public TerminologyETY findById(String pk) {
		return super.findByID(pk);
	}

	@Override
	public void insertAll(List<TerminologyETY> etys) {
		super.insertAll(etys);
	}

	@Override
	public List<TerminologyETY> findAll() {
		return super.findAll();
	}

	public Integer upsertByCode(final TerminologyETY ety) {
		Integer output = 0;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("code").is(ety.getCode()).and(SYSTEM_STRING).is(ety.getSystem()));
			
			Update update = new Update();
			update.set(SYSTEM_STRING, ety.getSystem());
			update.set("description", ety.getDescription());
			update.set("code", ety.getCode());
			UpdateResult uResult = mongoTemplate.upsert(query, update, TerminologyETY.class);
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
			query.addCriteria(Criteria.where(SYSTEM_STRING).is(system));
			output = mongoTemplate.exists(query, TerminologyETY.class);
 		} catch(Exception ex) {
			log.error("Error while execute exists by system :" , ex);
			throw new BusinessException("Error while execute exists by system :" , ex);
		}
		return output;
	}

	@Override
	public List<TerminologyETY> findByInCodeAndSystem(final List<String> codes, final String system) {
		List<TerminologyETY> output = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("code").in(codes).and(SYSTEM_STRING).is(system));
			output = mongoTemplate.find(query, TerminologyETY.class);
		} catch(Exception ex) {
			log.error("Error while execute find by in code and system :" , ex);
			throw new BusinessException("Error while execute find by in code and system :" , ex);
		}
		return output;
	}
 
	@Override
	public void dropCollection() {
		try {
			mongoTemplate.dropCollection(TerminologyETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
	}
	
}
