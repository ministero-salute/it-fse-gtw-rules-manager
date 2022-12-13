/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Payload;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Schema;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO.Schematron;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO.Terminology;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchematronQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EDSEntityTest {
	
	private final String TEST_SYSTEM = "testSystem"; 
	private final String TEST_CODE = "Code"; 
	private final String TEST_DESCRIPTION = "Description"; 
	private final Date TEST_DATE = new Date(); 
	
	@Autowired
	public SchematronQuery schematronQuery; 

	@Autowired
	public TerminologyQuery terminologyQuery; 
	
	@Autowired
	public SchemaQuery schemaQuery; 
	

	@Test
	void terminologyEtyEqualsTrueTest() {
		TerminologyETY etyFirst = new TerminologyETY(); 
		TerminologyETY etySecond = new TerminologyETY(); 
		
		etyFirst.setSystem(TEST_SYSTEM);
		etyFirst.setCode(TEST_CODE);
		etyFirst.setDescription(TEST_DESCRIPTION); 
		etyFirst.setLastUpdateDate(TEST_DATE); 
		
		etySecond.setSystem(TEST_SYSTEM);
		etySecond.setCode(TEST_CODE);
		etySecond.setDescription(TEST_DESCRIPTION); 
		etySecond.setLastUpdateDate(TEST_DATE); 
		
		assertTrue(etyFirst.equals(etySecond)); 

	} 
	
	@Test
	void terminologyEtyEqualsFalseTest() {
		TerminologyETY etyFirst = new TerminologyETY(); 
		TerminologyETY etySecond = new TerminologyETY(); 
		TerminologyETY etyFirstNull = new TerminologyETY(); 
		TerminologyETY etySecondNull = new TerminologyETY(); 
		
		etyFirst.setSystem(TEST_SYSTEM);
		etyFirst.setCode(TEST_CODE);
		etyFirst.setDescription(TEST_DESCRIPTION); 
		etyFirst.setLastUpdateDate(TEST_DATE); 
		
		etySecond.setSystem("SystemNotEqual");
		etySecond.setCode(TEST_CODE);
		etySecond.setDescription(TEST_DESCRIPTION); 
		etySecond.setLastUpdateDate(TEST_DATE); 
		
		assertFalse(etyFirst.equals(etySecond)); 
		assertTrue(etyFirstNull.equals(etySecondNull)); 
		
		etySecondNull = new TerminologyETY(); 
		etySecondNull.setCode(TEST_CODE); 
		
		assertFalse(etyFirstNull.equals(etySecondNull)); 
		
		etyFirstNull.setCode("testCodeSecond"); 
		
		assertFalse(etyFirstNull.equals(etySecondNull)); 


	} 
	
	@Test
	void terminologyEtyHashCodeTest() {
		TerminologyETY ety = new TerminologyETY(); 
		ety.setSystem(TEST_SYSTEM);
		ety.setCode(TEST_CODE);
		ety.setDescription(TEST_DESCRIPTION); 
		ety.setLastUpdateDate(TEST_DATE); 
		
		assertDoesNotThrow(() -> ety.hashCode()); 

	} 
	
	@Test
	void getUpsertQuerySchematronTest() {
		SchematronDTO dto = new SchematronDTO(); 
		Schematron schematron = new Schematron(); 
		schematron.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
		schematron.setName("testFilenameDefinition");
		schematron.setTemplateIdRoot("testidRoot"); 
		schematron.setVersion("1.0"); 
		schematron.setContent("SGVsbG8gV29ybGQ="); 
		schematron.setLastUpdateDate(new Date()); 
		
		dto.setSpanID("spanID");
		dto.setTraceID("traceID"); 
		dto.setDocument(schematron); 
		
		Document documentDto = schematronQuery.getUpsertQuery(dto); 
		
		assertEquals(Document.class, documentDto.getClass()); 
		assertEquals(Document.class, schematronQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
		assertEquals(Document.class, schematronQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
		
		assertDoesNotThrow(() -> schematronQuery.getComparatorQuery(documentDto)); 
	
	} 
	
	@Test
	void getUpsertQuerySchemaTest() {
		SchemaDTO dto = new SchemaDTO(); 
		Schema schema = new Schema(); 
		schema.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
		schema.setNameSchema("code"); 
		schema.setRootSchema(true); 
		schema.setTypeIdExtension("extension");  
		schema.setContentSchema("SGVsbG8gV29ybGQ=");
		schema.setLastUpdateDate(new Date()); 
		
		Payload payload = new Payload(); 
		payload.setDocument(schema); 
		
		dto.setSpanID("spanID");
		dto.setTraceID("traceID"); 
		dto.setData(payload); 
		
		Document documentDto = schemaQuery.getUpsertQuery(dto); 
		
		assertEquals(Document.class, documentDto.getClass()); 
		assertEquals(Document.class, schemaQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
		assertEquals(Document.class, schemaQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
		
		assertDoesNotThrow(() -> schemaQuery.getComparatorQuery(documentDto)); 
	
	}
	
	@Test
	void getUpsertQueryTerminologyTest() {
		TerminologyDTO dto = new TerminologyDTO(); 
		Terminology terminology = new Terminology(); 
		terminology.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
		terminology.setCode("code"); 
		terminology.setSystem("system"); 
		terminology.setDescription("description");  
		terminology.setLastUpdateDate(new Date()); 
		
		dto.setSpanID("spanID");
		dto.setTraceID("traceID"); 
		dto.setDocument(terminology); 
		
		Document documentDto = terminologyQuery.getUpsertQuery(dto); 
		
		assertEquals(Document.class, documentDto.getClass()); 
		assertEquals(Document.class, terminologyQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
		assertEquals(Document.class, terminologyQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
		
		assertDoesNotThrow(() -> terminologyQuery.getComparatorQuery(documentDto)); 
	
	} 
}
