package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.client.result.DeleteResult;
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
	public void addSchemaAndSchematronVersion(HttpServletRequest request) {
		String templateIdRootSchematron = "2.16.840.1.113883.2.9.10.1.1";
		String templateIdExtensionSchematron = "1.4";
		String schemaVersion = "1.4";
		addSchematronVersion(templateIdRootSchematron, templateIdExtensionSchematron);
		addSchemaVersion(schemaVersion);
	}
	
	private void addSchemaVersion(final String version) {
		deleteMockSchemaInsertedVersion(version);
		
		List<SchemaETY> schemas = mongoTemplate.findAll(SchemaETY.class);
		for(SchemaETY schema : schemas) {
			schema.setId(null);
			schema.setVersion(version);
		}
		mongoTemplate.insertAll(schemas);
	}
	
	private Integer deleteMockSchemaInsertedVersion(String version) {
		Integer outut = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("version").is(version));
		DeleteResult deleteResult = mongoTemplate.remove(query,SchematronETY.class);
		log.info("Deleted record schema : " + deleteResult.getDeletedCount());
		return outut;
	}
	
	private Integer deleteMockSchematronInsertedVersion(String templateIdRoot, String templateIdExtension) {
		Integer outut = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("template_id_root").is(templateIdRoot).
				and("template_id_extension").is(templateIdExtension));
		DeleteResult deleteResult = mongoTemplate.remove(query,SchematronETY.class);
		log.info("Deleted record schematron : " + deleteResult.getDeletedCount());
		return outut;
	}
	
	private void addSchematronVersion(final String templateIdRoot, final String templateIdExtension) {
		deleteMockSchematronInsertedVersion(templateIdRoot, templateIdExtension);
		
		List<SchematronETY> schematrons = mongoTemplate.findAll(SchematronETY.class);
		for(SchematronETY schematron : schematrons) {
			schematron.setId(null);
			schematron.setTemplateIdExtension(templateIdExtension);
		}
		mongoTemplate.insertAll(schematrons);
	}

	@Override
	public void updateDataUltimoAggiornamento(HttpServletRequest request) {
		Query query = new Query();
		Update update = new Update();
		update.set("data_ultimo_aggiornamento", new Date());
		UpdateResult uResult = mongoTemplate.updateMulti(query, update, SchematronETY.class);
		log.info("Record schematron aggironati : " + uResult.getModifiedCount());
		
		uResult = mongoTemplate.updateMulti(query, update, SchemaETY.class);
		log.info("Record schema aggiornati : " + uResult.getModifiedCount());
	}

	
}
