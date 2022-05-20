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
	public Integer upsertByVersion(final XslTransformETY ety) {
		Integer output = 0;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("version").is(ety.getVersion()).and("cda_type").is(ety.getCdaType()));
			
			Update update = new Update();
			update.set("cda_type", ety.getCdaType());
			update.set("name_xsl_transform", ety.getNameXslTransform());
			update.set("content_xsl_transform", ety.getContentXslTransform());
			update.set("version", ety.getVersion());
			UpdateResult uResult = mongoTemplate.upsert(query, update, XslTransformETY.class);
			output = (int)uResult.getModifiedCount();
		} catch(Exception ex) {
			log.error("Error upserting ety schema " , ex);
			throw new BusinessException("Error inserting ety schema ", ex);
		}
		return output;
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
}
