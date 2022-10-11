//package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.Date;
//
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.DefinitionDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.DefinitionDTO.Definition;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.MapDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.MapDTO.Map;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Payload;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Schema;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO.Schematron;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO.Terminology;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.ValuesetDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.ValuesetDTO.Valueset;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.XslDTO;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.XslDTO.Xsl;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.DefinitionQuery;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.MapQuery;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchematronQuery;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.ValuesetQuery;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.XslQuery;
//
//@SpringBootTest
//@ActiveProfiles(Constants.Profile.TEST)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class EDSEntityTest {
//	
//	private final String TEST_SYSTEM = "testSystem"; 
//	private final String TEST_CODE = "Code"; 
//	private final String TEST_DESCRIPTION = "Description"; 
//	private final Date TEST_DATE = new Date(); 
//	
//	
//	@Autowired
//	public MapQuery mapQuery; 
//	
//	@Autowired
//	public DefinitionQuery definitionQuery; 
//	
//	@Autowired
//	public SchematronQuery schematronQuery; 
//	
//	@Autowired
//	public XslQuery xslQuery; 
//	
//	@Autowired
//	public ValuesetQuery valuesetQuery; 
//
//	@Autowired
//	public TerminologyQuery terminologyQuery; 
//	
//	@Autowired
//	public SchemaQuery schemaQuery; 
//	
//
//	@Test
//	void terminologyEtyEqualsTrueTest() {
//		TerminologyETY etyFirst = new TerminologyETY(); 
//		TerminologyETY etySecond = new TerminologyETY(); 
//		
//		etyFirst.setSystem(TEST_SYSTEM);
//		etyFirst.setCode(TEST_CODE);
//		etyFirst.setDescription(TEST_DESCRIPTION); 
//		etyFirst.setLastUpdateDate(TEST_DATE); 
//		
//		etySecond.setSystem(TEST_SYSTEM);
//		etySecond.setCode(TEST_CODE);
//		etySecond.setDescription(TEST_DESCRIPTION); 
//		etySecond.setLastUpdateDate(TEST_DATE); 
//		
//		assertTrue(etyFirst.equals(etySecond)); 
//
//	} 
//	
//	@Test
//	void terminologyEtyEqualsFalseTest() {
//		TerminologyETY etyFirst = new TerminologyETY(); 
//		TerminologyETY etySecond = new TerminologyETY(); 
//		TerminologyETY etyFirstNull = new TerminologyETY(); 
//		TerminologyETY etySecondNull = new TerminologyETY(); 
//		
//		etyFirst.setSystem(TEST_SYSTEM);
//		etyFirst.setCode(TEST_CODE);
//		etyFirst.setDescription(TEST_DESCRIPTION); 
//		etyFirst.setLastUpdateDate(TEST_DATE); 
//		
//		etySecond.setSystem("SystemNotEqual");
//		etySecond.setCode(TEST_CODE);
//		etySecond.setDescription(TEST_DESCRIPTION); 
//		etySecond.setLastUpdateDate(TEST_DATE); 
//		
//		assertFalse(etyFirst.equals(etySecond)); 
//		assertTrue(etyFirstNull.equals(etySecondNull)); 
//		
//		etySecondNull = new TerminologyETY(); 
//		etySecondNull.setCode(TEST_CODE); 
//		
//		assertFalse(etyFirstNull.equals(etySecondNull)); 
//		
//		etyFirstNull.setCode("testCodeSecond"); 
//		
//		assertFalse(etyFirstNull.equals(etySecondNull)); 
//
//
//	} 
//	
//	@Test
//	void terminologyEtyHashCodeTest() {
//		TerminologyETY ety = new TerminologyETY(); 
//		ety.setSystem(TEST_SYSTEM);
//		ety.setCode(TEST_CODE);
//		ety.setDescription(TEST_DESCRIPTION); 
//		ety.setLastUpdateDate(TEST_DATE); 
//		
//		assertDoesNotThrow(() -> ety.hashCode()); 
//
//	} 
//	
//	@Test
//	void getUpsertQueryMapTest() {
//		MapDTO dto = new MapDTO(); 
//		Map map = new Map(); 
//		map.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		map.setFilenameMap("testFilenameMap");
//		map.setNameMap("testNameMap"); 
//		map.setRootMap("testRootMap");
//		map.setExtensionMap("testExtensionMap");
//		map.setContentMap("SGVsbG8gV29ybGQ="); 
//		map.setLastUpdateDate(new Date()); 
//
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setDocument(map); 
//		
//		Document documentDto = mapQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, mapQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, mapQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> mapQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//	
//	@Test
//	void getUpsertQueryDefinitionTest() {
//		DefinitionDTO dto = new DefinitionDTO(); 
//		Definition definition = new Definition(); 
//		definition.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		definition.setFilenameDefinition("testFilenameDefinition");
//		definition.setNameDefinition("testNameDefinition"); 
//		definition.setVersionDefinition("1.0"); 
//		definition.setContentDefinition("SGVsbG8gV29ybGQ="); 
//		definition.setLastUpdateDate(new Date()); 
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setDocument(definition); 
//		
//		Document documentDto = definitionQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, definitionQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, definitionQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> definitionQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//	
//	@Test
//	void getUpsertQuerySchematronTest() {
//		SchematronDTO dto = new SchematronDTO(); 
//		Schematron schematron = new Schematron(); 
//		schematron.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		schematron.setNameSchematron("testFilenameDefinition");
//		schematron.setTemplateIdRoot("testidRoot"); 
//		schematron.setVersion("1.0"); 
//		schematron.setContentSchematron("SGVsbG8gV29ybGQ="); 
//		schematron.setLastUpdateDate(new Date()); 
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setDocument(schematron); 
//		
//		Document documentDto = schematronQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, schematronQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, schematronQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> schematronQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//	
//	@Test
//	void getUpsertQueryXslTest() {
//		XslDTO dto = new XslDTO(); 
//		Xsl xsl = new Xsl(); 
//		xsl.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		xsl.setNameXslTransform("testFilenameDefinition");
//		xsl.setTemplateIdRoot("testidRoot"); 
//		xsl.setVersion("1.0"); 
//		xsl.setContentXslTransform("SGVsbG8gV29ybGQ="); 
//		xsl.setLastUpdateDate(new Date()); 
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setDocument(xsl); 
//		
//		Document documentDto = xslQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, xslQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, xslQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> xslQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//	
//	@Test
//	void getUpsertQueryValuesetTest() {
//		ValuesetDTO dto = new ValuesetDTO(); 
//		Valueset valueset = new Valueset(); 
//		valueset.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		valueset.setNameValueset("testNameDefinition"); 
//		valueset.setFilenameValueset("testFilenameDefinition"); 
//		valueset.setContentValueset("SGVsbG8gV29ybGQ="); 
//		valueset.setLastUpdateDate(new Date()); 
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setDocument(valueset); 
//		
//		Document documentDto = valuesetQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, valuesetQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, valuesetQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> valuesetQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//	
//	@Test
//	void getUpsertQuerySchemaTest() {
//		SchemaDTO dto = new SchemaDTO(); 
//		Schema schema = new Schema(); 
//		schema.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		schema.setNameSchema("code"); 
//		schema.setRootSchema(true); 
//		schema.setTypeIdExtension("extension");  
//		schema.setContentSchema("SGVsbG8gV29ybGQ=");
//		schema.setLastUpdateDate(new Date()); 
//		
//		Payload payload = new Payload(); 
//		payload.setDocument(schema); 
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setData(payload); 
//		
//		Document documentDto = schemaQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, schemaQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, schemaQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> schemaQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//	
//	@Test
//	void getUpsertQueryTerminologyTest() {
//		TerminologyDTO dto = new TerminologyDTO(); 
//		Terminology terminology = new Terminology(); 
//		terminology.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
//		terminology.setCode("code"); 
//		terminology.setSystem("system"); 
//		terminology.setDescription("description");  
//		terminology.setLastUpdateDate(new Date()); 
//		
//		dto.setSpanID("spanID");
//		dto.setTraceID("traceID"); 
//		dto.setDocument(terminology); 
//		
//		Document documentDto = terminologyQuery.getUpsertQuery(dto); 
//		
//		assertEquals(Document.class, documentDto.getClass()); 
//		assertEquals(Document.class, terminologyQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		assertEquals(Document.class, terminologyQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
//		
//		assertDoesNotThrow(() -> terminologyQuery.getComparatorQuery(documentDto)); 
//	
//	} 
//}
////package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;
////
////import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
////import static org.junit.jupiter.api.Assertions.assertEquals;
////import static org.junit.jupiter.api.Assertions.assertFalse;
////import static org.junit.jupiter.api.Assertions.assertTrue;
////
////import java.util.Date;
////
////import org.bson.Document;
////import org.bson.types.ObjectId;
////import org.junit.jupiter.api.Test;
////import org.junit.jupiter.api.TestInstance;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.context.SpringBootTest;
////import org.springframework.test.context.ActiveProfiles;
////
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.DefinitionDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.DefinitionDTO.Definition;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.MapDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.MapDTO.Map;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Payload;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO.Schema;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO.Schematron;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO.Terminology;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.ValuesetDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.ValuesetDTO.Valueset;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.XslDTO;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.XslDTO.Xsl;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.DefinitionQuery;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.MapQuery;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchematronQuery;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.ValuesetQuery;
////import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.XslQuery;
////
////@SpringBootTest
////@ActiveProfiles(Constants.Profile.TEST)
////@TestInstance(TestInstance.Lifecycle.PER_CLASS)
////public class EDSEntityTest {
////	
////	private final String TEST_SYSTEM = "testSystem"; 
////	private final String TEST_CODE = "Code"; 
////	private final String TEST_DESCRIPTION = "Description"; 
////	private final Date TEST_DATE = new Date(); 
////	
////	
////	@Autowired
////	public MapQuery mapQuery; 
////	
////	@Autowired
////	public DefinitionQuery definitionQuery; 
////	
////	@Autowired
////	public SchematronQuery schematronQuery; 
////	
////	@Autowired
////	public XslQuery xslQuery; 
////	
////	@Autowired
////	public ValuesetQuery valuesetQuery; 
////
////	@Autowired
////	public TerminologyQuery terminologyQuery; 
////	
////	@Autowired
////	public SchemaQuery schemaQuery; 
////	
////
////	@Test
////	void terminologyEtyEqualsTrueTest() {
////		TerminologyETY etyFirst = new TerminologyETY(); 
////		TerminologyETY etySecond = new TerminologyETY(); 
////		
////		etyFirst.setSystem(TEST_SYSTEM);
////		etyFirst.setCode(TEST_CODE);
////		etyFirst.setDescription(TEST_DESCRIPTION); 
////		etyFirst.setLastUpdateDate(TEST_DATE); 
////		
////		etySecond.setSystem(TEST_SYSTEM);
////		etySecond.setCode(TEST_CODE);
////		etySecond.setDescription(TEST_DESCRIPTION); 
////		etySecond.setLastUpdateDate(TEST_DATE); 
////		
////		assertTrue(etyFirst.equals(etySecond)); 
////
////	} 
////	
////	@Test
////	void terminologyEtyEqualsFalseTest() {
////		TerminologyETY etyFirst = new TerminologyETY(); 
////		TerminologyETY etySecond = new TerminologyETY(); 
////		TerminologyETY etyFirstNull = new TerminologyETY(); 
////		TerminologyETY etySecondNull = new TerminologyETY(); 
////		
////		etyFirst.setSystem(TEST_SYSTEM);
////		etyFirst.setCode(TEST_CODE);
////		etyFirst.setDescription(TEST_DESCRIPTION); 
////		etyFirst.setLastUpdateDate(TEST_DATE); 
////		
////		etySecond.setSystem("SystemNotEqual");
////		etySecond.setCode(TEST_CODE);
////		etySecond.setDescription(TEST_DESCRIPTION); 
////		etySecond.setLastUpdateDate(TEST_DATE); 
////		
////		assertFalse(etyFirst.equals(etySecond)); 
////		assertTrue(etyFirstNull.equals(etySecondNull)); 
////		
////		etySecondNull = new TerminologyETY(); 
////		etySecondNull.setCode(TEST_CODE); 
////		
////		assertFalse(etyFirstNull.equals(etySecondNull)); 
////		
////		etyFirstNull.setCode("testCodeSecond"); 
////		
////		assertFalse(etyFirstNull.equals(etySecondNull)); 
////
////
////	} 
////	
////	@Test
////	void terminologyEtyHashCodeTest() {
////		TerminologyETY ety = new TerminologyETY(); 
////		ety.setSystem(TEST_SYSTEM);
////		ety.setCode(TEST_CODE);
////		ety.setDescription(TEST_DESCRIPTION); 
////		ety.setLastUpdateDate(TEST_DATE); 
////		
////		assertDoesNotThrow(() -> ety.hashCode()); 
////
////	} 
////	
////	@Test
////	void getUpsertQueryMapTest() {
////		MapDTO dto = new MapDTO(); 
////		Map map = new Map(); 
////		map.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		map.setFilenameMap("testFilenameMap");
////		map.setNameMap("testNameMap"); 
////		map.setRootMap("testRootMap");
////		map.setExtensionMap("testExtensionMap");
////		map.setContentMap("SGVsbG8gV29ybGQ="); 
////		map.setLastUpdateDate(new Date()); 
////
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setDocument(map); 
////		
////		Document documentDto = mapQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, mapQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, mapQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> mapQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////	
////	@Test
////	void getUpsertQueryDefinitionTest() {
////		DefinitionDTO dto = new DefinitionDTO(); 
////		Definition definition = new Definition(); 
////		definition.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		definition.setFilenameDefinition("testFilenameDefinition");
////		definition.setNameDefinition("testNameDefinition"); 
////		definition.setVersionDefinition("1.0"); 
////		definition.setContentDefinition("SGVsbG8gV29ybGQ="); 
////		definition.setLastUpdateDate(new Date()); 
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setDocument(definition); 
////		
////		Document documentDto = definitionQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, definitionQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, definitionQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> definitionQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////	
////	@Test
////	void getUpsertQuerySchematronTest() {
////		SchematronDTO dto = new SchematronDTO(); 
////		Schematron schematron = new Schematron(); 
////		schematron.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		schematron.setNameSchematron("testFilenameDefinition");
////		schematron.setTemplateIdRoot("testidRoot"); 
////		schematron.setTemplateIdExtension("1.0"); 
////		schematron.setContentSchematron("SGVsbG8gV29ybGQ="); 
////		schematron.setLastUpdateDate(new Date()); 
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setDocument(schematron); 
////		
////		Document documentDto = schematronQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, schematronQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, schematronQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> schematronQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////	
////	@Test
////	void getUpsertQueryXslTest() {
////		XslDTO dto = new XslDTO(); 
////		Xsl xsl = new Xsl(); 
////		xsl.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		xsl.setNameXslTransform("testFilenameDefinition");
////		xsl.setTemplateIdRoot("testidRoot"); 
////		xsl.setVersion("1.0"); 
////		xsl.setContentXslTransform("SGVsbG8gV29ybGQ="); 
////		xsl.setLastUpdateDate(new Date()); 
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setDocument(xsl); 
////		
////		Document documentDto = xslQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, xslQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, xslQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> xslQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////	
////	@Test
////	void getUpsertQueryValuesetTest() {
////		ValuesetDTO dto = new ValuesetDTO(); 
////		Valueset valueset = new Valueset(); 
////		valueset.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		valueset.setNameValueset("testNameDefinition"); 
////		valueset.setFilenameValueset("testFilenameDefinition"); 
////		valueset.setContentValueset("SGVsbG8gV29ybGQ="); 
////		valueset.setLastUpdateDate(new Date()); 
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setDocument(valueset); 
////		
////		Document documentDto = valuesetQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, valuesetQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, valuesetQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> valuesetQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////	
////	@Test
////	void getUpsertQuerySchemaTest() {
////		SchemaDTO dto = new SchemaDTO(); 
////		Schema schema = new Schema(); 
////		schema.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		schema.setNameSchema("code"); 
////		schema.setRootSchema(true); 
////		schema.setTypeIdExtension("extension");  
////		schema.setContentSchema("SGVsbG8gV29ybGQ=");
////		schema.setLastUpdateDate(new Date()); 
////		
////		Payload payload = new Payload(); 
////		payload.setDocument(schema); 
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setData(payload); 
////		
////		Document documentDto = schemaQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, schemaQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, schemaQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> schemaQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////	
////	@Test
////	void getUpsertQueryTerminologyTest() {
////		TerminologyDTO dto = new TerminologyDTO(); 
////		Terminology terminology = new Terminology(); 
////		terminology.setId(new ObjectId("6332f5bbacf1522dbb24883f").toString()); 
////		terminology.setCode("code"); 
////		terminology.setSystem("system"); 
////		terminology.setDescription("description");  
////		terminology.setLastUpdateDate(new Date()); 
////		
////		dto.setSpanID("spanID");
////		dto.setTraceID("traceID"); 
////		dto.setDocument(terminology); 
////		
////		Document documentDto = terminologyQuery.getUpsertQuery(dto); 
////		
////		assertEquals(Document.class, documentDto.getClass()); 
////		assertEquals(Document.class, terminologyQuery.getFilterQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		assertEquals(Document.class, terminologyQuery.getDeleteQuery("6332f5bbacf1522dbb24883f").getClass()); 
////		
////		assertDoesNotThrow(() -> terminologyQuery.getComparatorQuery(documentDto)); 
////	
////	} 
////}
