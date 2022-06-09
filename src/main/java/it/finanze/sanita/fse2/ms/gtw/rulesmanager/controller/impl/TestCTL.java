package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.ITestCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestCTL implements ITestCTL {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 3260806709541019186L;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	    
	@Override
	public void updateDataUltimoAggiornamento(HttpServletRequest request) {
		Query query = new Query();
		Update update = new Update();
		update.set("last_update_date", new Date());
		UpdateResult uResult = mongoTemplate.updateMulti(query, update, SchematronETY.class);
		log.info("Record schematron aggironati : " + uResult.getModifiedCount());
		
		uResult = mongoTemplate.updateMulti(query, update, SchemaETY.class);
		log.info("Record schema aggiornati : " + uResult.getModifiedCount());
	}

	
}
