package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import java.util.Date;
import java.util.Map;

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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IMockSRV;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestCTL implements ITestCTL {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 3260806709541019186L;

	@Autowired
	private transient MongoTemplate mongoTemplate;
	
	@Autowired
	private InvokeEDSClientScheduler edsClientScheduler;
	    
	@Autowired
	private IMockSRV mockSRV;
	
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


	@Override
	public Map<String,Integer> runScheduler(HttpServletRequest request) {
		mockSRV.dropCollections();
		mockSRV.saveMockConfigurationItem();
		edsClientScheduler.action();
		return mockSRV.countSavedElement();
	}

	
}
