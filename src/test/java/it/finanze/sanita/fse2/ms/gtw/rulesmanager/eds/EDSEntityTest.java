/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Schema;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO.Schematron;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO.Terminology;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.FhirStructuresQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchematronQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO.Transform;
import static org.junit.jupiter.api.Assertions.*;

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
	
	@Autowired
	public FhirStructuresQuery fhirQuery; 
	

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

		assertEquals(etyFirst, etySecond);

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

		assertNotEquals(etyFirst, etySecond);
		assertEquals(etyFirstNull, etySecondNull);
		
		etySecondNull = new TerminologyETY(); 
		etySecondNull.setCode(TEST_CODE);

		assertNotEquals(etyFirstNull, etySecondNull);
		
		etyFirstNull.setCode("testCodeSecond");

		assertNotEquals(etyFirstNull, etySecondNull);


	} 
	
	@Test
	void terminologyEtyHashCodeTest() {
		TerminologyETY ety = new TerminologyETY(); 
		ety.setSystem(TEST_SYSTEM);
		ety.setCode(TEST_CODE);
		ety.setDescription(TEST_DESCRIPTION); 
		ety.setLastUpdateDate(TEST_DATE); 
		
		assertDoesNotThrow(ety::hashCode);

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
		
		dto.setSpanID("spanID");
		dto.setTraceID("traceID"); 
		dto.setDocument(schema);
		
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

	@Test
	void getUpsertQueryFhirTest() {
		FhirStructuresDTO dto = new FhirStructuresDTO();
		Transform transform = new Transform();
		transform.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString());
		transform.setTemplateIdRoot(new ArrayList<>());
		transform.setVersion("version");
		transform.setType("type");
		transform.setContent("content");
		transform.setFilename("filename");
		transform.setLastUpdateDate(new Date());
		transform.setDeleted(false);

		dto.setSpanID("spanID");
		dto.setTraceID("traceID");
		dto.setDocument(transform);

		Document fhirDto = fhirQuery.getUpsertQuery(dto);

		assertEquals(Document.class, fhirDto.getClass());
		assertEquals(Document.class, fhirQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass());
		assertEquals(Document.class, fhirQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass());

		assertDoesNotThrow(() -> fhirQuery.getComparatorQuery(fhirDto));
	}

	@Test
	void fromMapToDoc() {
		TerminologyMapDTO map = new TerminologyMapDTO(
			"system",
			"version",
			"code",
			new Date(),
			new Date(),
			false
		);
		Document doc = DictionaryETY.fromMap(map);
		assertNotNull(doc);
	}

}
