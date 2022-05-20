package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.opencsv.bean.CsvToBeanBuilder;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyBuilderTestDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.VocabularyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ISchemaSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ISchematronSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IVocabularySRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IXslTransformSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.FileUtility;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
public class PersistenceTest extends AbstractTest {

	@Autowired
	private ISchemaSRV schemaSRV;
	
	@Autowired
	private ISchematronSRV schematronSRV;
	
	@Autowired
	private IVocabularySRV vocabularySRV;
	 
	@Autowired
	private IXslTransformSRV xslTtransformSRV;

	
	@Test
	@DisplayName("Populate and get schema collection")
	void populateSchemaCollection() {
		dropSchemaCollection();
		List<String> schemaFiles = new ArrayList<>();
		schemaFiles.add("datatypes.xsd");
		schemaFiles.add("datatypes-base.xsd");
		schemaFiles.add("datatypes-rX-cs.xsd");
		schemaFiles.add("infrastructureRoot.xsd");
		schemaFiles.add("labExtension_1.2_gen.xsd");
		schemaFiles.add("NarrativeBlock.xsd");
		schemaFiles.add("POCD_MT000040UV02.xsd");
		schemaFiles.add("sdtcExtension.xsd");
		schemaFiles.add("voc.xsd");
		
		List<SchemaETY> list = new ArrayList<>();
		for(String fileName : schemaFiles) {
			String path = "Files" + File.separator + "schema" + File.separator + "coreschemas" + File.separator + fileName;
			SchemaETY ety = getContentAndSave(path,false);
			list.add(ety);
		}
		schemaSRV.insertAll(list);
		
		String path = "Files" + File.separator + "schema" + File.separator + "CDA.xsd";
		SchemaETY ety = getContentAndSave(path,true);
		schemaSRV.insert(ety);
	}

	private SchemaETY getContentAndSave(String fileName,boolean rootFile) {
		byte[] schemaContent = FileUtility.getFileFromInternalResources(fileName);
		String newFilename = StringUtility.getFilename(fileName);
		SchemaETY ety = new SchemaETY();
		ety.setCdaType("CDA TEST");
		ety.setContentSchema(new Binary(BsonBinarySubType.BINARY, schemaContent));
		ety.setNameSchema(newFilename);
		ety.setVersion("1.0.0");
		if(rootFile) {
			ety.setRootSchema(true);	
		}
		return ety;
	}
	
	@Test
	@DisplayName("Populate and get schematron collection")
	void populateSchematronCollection() {
		dropSchematronCollection();
		String schematronFilename = "schematronFSE.sch.xsl";
		byte[] schematronContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "schematron" + File.separator + schematronFilename);
		SchematronETY ety = new SchematronETY();
		ety.setContentSchematron(new Binary(BsonBinarySubType.BINARY, schematronContent));
		ety.setNameSchematron(schematronFilename);
		ety.setCdaCode("11502-2");
		ety.setCdaCodeSystem("2.16.840.1.113883.6.1");
		ety.setTemplateIdRoot("1.3");
		ety.setRootSchematron(true);
		ety = schematronSRV.insert(ety);
		
		SchematronETY out = schematronSRV.findById(ety.getId());
		assertEquals(ety.getId(), out.getId());
		
		schematronFilename = "2.16.840.1.113883.6.1.xml";
		schematronContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "schematron" + File.separator + schematronFilename);
		ety = new SchematronETY();
		ety.setContentSchematron(new Binary(BsonBinarySubType.BINARY, schematronContent));
		ety.setNameSchematron(schematronFilename);
		ety.setCdaCode("11502-2");
		ety.setCdaCodeSystem("2.16.840.1.113883.6.1");
		ety.setTemplateIdRoot("1.3");
		ety = schematronSRV.insert(ety);
	}
	
	@Test
	@DisplayName("Populate and get vocabulary collection")
	void populateVocabularyCollection() {
		dropVocabularyCollection();
		String csvFileName = "LoincTableCore.csv";
		byte[] csvContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "vocabulary" + File.separator + csvFileName);
		InputStream targetStream = new ByteArrayInputStream(csvContent);
		Reader reader = new InputStreamReader(targetStream);
		List<VocabularyBuilderTestDTO> vocabularyListDTO = buildDTOFromCsv(reader);
		vocabularyListDTO.remove(0);

		List<VocabularyETY> listToSave = new ArrayList<>();
		for(VocabularyBuilderTestDTO vocabularyDTO : vocabularyListDTO) {
			VocabularyETY ety = new VocabularyETY();
			ety.setCode(vocabularyDTO.getCode());
			ety.setDescription(vocabularyDTO.getDescription());
			ety.setSystem("2.16.840.1.113883.6.1");
			listToSave.add(ety);
		}
		vocabularySRV.insertAll(listToSave);
	}

	@Test
	@DisplayName("Populate and get xsl transform collection")
	void populateXslTranformCollection() {
		dropXslTransformCollection();

		String xslTransformFileName = "vaccination.xsl";
		byte[] xslTransformContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "xslTransform" + File.separator + xslTransformFileName);
		XslTransformETY ety = new XslTransformETY();
		ety.setCdaType("CDA TEST");
		ety.setContentXslTransform(new Binary(BsonBinarySubType.BINARY, xslTransformContent));
		ety.setNameXslTransform(xslTransformFileName);
		ety.setVersion("1.0.0");
		ety = xslTtransformSRV.insert(ety);
		
		XslTransformETY out = xslTtransformSRV.findById(ety.getId());
		assertEquals(ety.getId(), out.getId());
	}
 
	private List<VocabularyBuilderTestDTO> buildDTOFromCsv(Reader reader){
		List<VocabularyBuilderTestDTO> output = null; 
		output = new CsvToBeanBuilder(reader).withType(VocabularyBuilderTestDTO.class).withSeparator(',').build().parse();
		return output;
	}
	
}
