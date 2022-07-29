package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ISchematronRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Schema repository.
 */
@Slf4j
@Repository
public class SchematronRepo extends AbstractMongoRepo<SchematronETY, String> implements ISchematronRepo {
	
	/**
	 * Serial version uid. 
	 */
	private static final long serialVersionUID = 8948529146857638945L;
	
	private static final String EXECUTION_ERROR = "Error while execute exists by version query ";

	@Autowired
	private transient MongoTemplate mongoTemplate;
	
	@Override
	public SchematronETY insert(SchematronETY ety) {
		return super.insert(ety);
	}

	@Override
	public SchematronETY findById(String pk) {
		return super.findByID(pk);
	}

	@Override
	public List<SchematronETY> findAll() {
		return super.findAll();
	}

	@Override
	public void insertAll(List<SchematronETY> etys) {
		super.insertAll(etys);
	}


	@Override
	public boolean existByTemplateIdRoot(final String templateIdRoot) {
		boolean output = false;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("template_id_root").is(templateIdRoot));
			output = mongoTemplate.exists(query, SchematronETY.class);
		} catch(Exception ex) {
			log.error(EXECUTION_ERROR + getClass() , ex);
			throw new BusinessException(EXECUTION_ERROR + getClass(), ex);
		}
		return output;
	}
	
	@Override
	public void dropCollection() {
		try {
			mongoTemplate.dropCollection(SchematronETY.class);
		} catch(Exception ex) {
			log.error(EXECUTION_ERROR + getClass() , ex);
			throw new BusinessException(EXECUTION_ERROR + getClass(), ex);
		}
	}
	
}
