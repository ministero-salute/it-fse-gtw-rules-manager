package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IXslTransformRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Xsl Transform repository.
 */
@Slf4j
@Repository
public class XslTransformRepo extends AbstractMongoRepo<XslTransformETY, String> implements IXslTransformRepo {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 5918243970308341366L;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public XslTransformETY insert(final XslTransformETY ety) {
		return super.insert(ety);
	}

	@Override
	public List<XslTransformETY> findAll() {
		return super.findAll();
	}

	@Override
	public XslTransformETY findById(String pk) {
		return super.findByID(pk);
	}

	@Override
	public void insertAll(List<XslTransformETY> etys) {
		super.insertAll(etys);
	}

	 
	
	@Override
	public boolean existByVersion(final String version) {
		boolean output = false;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("version").is(version));
			output = mongoTemplate.exists(query, XslTransformETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
		return output;
	}
	
	@Override
	public void dropCollection() {
		try {
			mongoTemplate.dropCollection(XslTransformETY.class);
		} catch(Exception ex) {
			log.error("Error while execute exists by version query " + getClass() , ex);
			throw new BusinessException("Error while execute exists by version query " + getClass(), ex);
		}
	}
}
