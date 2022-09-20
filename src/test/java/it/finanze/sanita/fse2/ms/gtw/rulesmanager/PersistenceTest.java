package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ITerminologySRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IXslTransformSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.FileUtility;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
public class PersistenceTest extends AbstractTest {
	
	@Autowired
	private ITerminologySRV vocabularySRV;
	 
	@Autowired
	private IXslTransformSRV xslTtransformSRV;
	
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

		List<TerminologyETY> listToSave = new ArrayList<>();
		for(VocabularyBuilderTestDTO vocabularyDTO : vocabularyListDTO) {
			TerminologyETY ety = new TerminologyETY();
			ety.setCode(vocabularyDTO.getCode());
			ety.setDescription(vocabularyDTO.getDescription());
			ety.setSystem("2.16.840.1.113883.6.1");
			listToSave.add(ety);
			
		}
		
				
		assertNotNull(listToSave);
		vocabularySRV.insertAll(listToSave);

		
		List<VocabularyDTO> vocabulariesDTO = new ArrayList<>();
		List<VocabularyEntryDTO> entriesDTO = new ArrayList<>();
		
		VocabularyEntryDTO entryDTO = VocabularyEntryDTO.builder()
				.code("")
				.description("")
				.build();
		
		entryDTO.setCode("genericCode");
		entryDTO.setDescription("genericDescription");
		
		
		VocabularyDTO voc = VocabularyDTO.builder()
				.system("")
				.entryDTO(entriesDTO)
				.build();
		
		entriesDTO.add(entryDTO);
		
		voc.setEntryDTO(entriesDTO);
		voc.setSystem("2.16.840.1.113883.6.1");

		vocabulariesDTO.add(voc);
		Integer vocSysSaved = vocabularySRV.saveNewVocabularySystems(vocabulariesDTO);
		assertNotNull(vocSysSaved); 
		 
	}
	
	@Test
	@DisplayName("test equals terminology entities ")	
	void equalsTerminologyEty() {
		//first obj
		TerminologyETY ety1 = new TerminologyETY();
		VocabularyBuilderTestDTO vocabularyDTO1 = new VocabularyBuilderTestDTO();
		
		vocabularyDTO1.setCode("code");
		vocabularyDTO1.setDescription("descrption");
		
		ety1.setCode(vocabularyDTO1.getCode());
		ety1.setDescription(vocabularyDTO1.getDescription());
		ety1.setSystem("2.16.840.1.113883.6.1");
		
		//second obj
		TerminologyETY ety2 = new TerminologyETY();
		VocabularyBuilderTestDTO vocabularyDTO2 = new VocabularyBuilderTestDTO();
		
		vocabularyDTO2.setCode("code");
		vocabularyDTO2.setDescription("description");
		
		ety2.setCode(vocabularyDTO2.getCode());
		ety2.setDescription(vocabularyDTO2.getDescription());
		ety2.setSystem("2.16.840.1.113883.6.1");

		assertFalse(ety1.equals(null));
		assertFalse(ety2.equals(null));
		assertTrue(ety1.equals(ety2));

		
	}
	
	@Test
	@DisplayName("Insert Terminilogy ETY Test")
	void insertTerminologyTest() {
		String csvFileName = "LoincTableCore.csv";
		byte[] csvContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "vocabulary" + File.separator + csvFileName);
		InputStream targetStream = new ByteArrayInputStream(csvContent);
		Reader reader = new InputStreamReader(targetStream);
		List<VocabularyBuilderTestDTO> vocabularyListDTO = buildDTOFromCsv(reader);
		vocabularyListDTO.remove(0);
		
		VocabularyBuilderTestDTO vocabularyDTO = vocabularyListDTO.get(0); 
		
		TerminologyETY ety = new TerminologyETY();
		ety.setCode(vocabularyDTO.getCode());
		ety.setDescription(vocabularyDTO.getDescription());
		ety.setSystem("2.16.840.1.113883.6.1"); 
		
		TerminologyETY insertedEty = vocabularySRV.insert(ety); 
		
		assertNotNull(insertedEty); 
	}
	
	@Test
	@DisplayName("Insert Schematron ETY Test") 
	void insertSchematron() {
		
	}
	
	@Test
	@DisplayName("Populate and get xsl transform collection")
	void populateXslTranformCollection() {
		dropXslTransformCollection();

		String xslTransformFileName = "vaccination.xsl";
		byte[] xslTransformContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "xslTransform" + File.separator + xslTransformFileName);
		XslTransformETY ety = new XslTransformETY();
		ety.setContentXslTransform(new Binary(BsonBinarySubType.BINARY, xslTransformContent));
		ety.setNameXslTransform(xslTransformFileName);
		ety.setTemplateIdExtension("1.0.0");
		ety = xslTtransformSRV.insert(ety);
		
		XslTransformETY out = xslTtransformSRV.findById(ety.getId());
		assertEquals(ety.getId(), out.getId());
	}
 
	@SuppressWarnings({"unchecked", "rawtypes"})
	private List<VocabularyBuilderTestDTO> buildDTOFromCsv(Reader reader){
		List<VocabularyBuilderTestDTO> output = null; 
		output = new CsvToBeanBuilder(reader).withType(VocabularyBuilderTestDTO.class).withSeparator(',').build().parse();
		return output;
	}
	
}
