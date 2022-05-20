package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchemaEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchematronEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyBuilderTestDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.XslTransformDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.XslTransformEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.VocabularyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.VocabularyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IMockSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MockSRV implements IMockSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 198908352076649014L;

	private static final String cdaType = "CDA_TYPE";
	
	private static final String versionXsd = "1.3";
	
	private static final String versionXsl = "1.0.0";
	
	private static final String templateIdRoot = "2.16.840.1.113883.2.9.10.1.1";
	
	private static final String templateIdExtension = "1.3";
	
	private static final String cdaCode = "11502-2";
	
	private static final String cdaCodeSystem = "2.16.840.1.113883.6.1";
	
	@Autowired
	private VocabularyRepo repo;
	@Override
	public ConfigItemDTO mockConfigurationItem() {
		ConfigItemDTO configurationItems = null;
        try {
            configurationItems = ConfigItemDTO.builder()
            	.schema(getSchemaDTO())
            	.schematron(getSchematronDTO())
                .xslTransform(getXslTransform())
                .vocabulary(getVocabulary())
                .build();

        } catch (Exception ex) {
            log.error("Error encountered while calling EDS Client at url: %s to retrieve configuration items", ex);
            throw new BusinessException(ex);
        }

        return configurationItems;
	}

	 @SuppressWarnings("all")
	    private List<VocabularyDTO> getVocabulary() {
		 saveCdaVocabulary();
	        List<VocabularyDTO> vocabularyList = new ArrayList<>();

	        final String csvFileName = "LoincTableCore.csv";
			final byte[] csvContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "vocabulary" + File.separator + csvFileName);

			List<VocabularyEntryDTO> entries = new ArrayList<>();
	        try (InputStream targetStream = new ByteArrayInputStream(csvContent);
	            Reader reader = new InputStreamReader(targetStream);) {
	            List<VocabularyBuilderTestDTO> vocabularyListDTO = new CsvToBeanBuilder(reader).withType(VocabularyBuilderTestDTO.class).withSeparator(',').build().parse();
	            vocabularyListDTO.remove(0);
	            
	            for(VocabularyBuilderTestDTO vocabularyDTO : vocabularyListDTO) {
	            	VocabularyEntryDTO entryDTO = VocabularyEntryDTO.builder().
	            			code(vocabularyDTO.getCode()).
	            			description(vocabularyDTO.getDescription()).
	            			build();
	            	entries.add(entryDTO);
	            }
	        } catch (Exception e) {
	            log.error("Error while reading vocabulary file", e);
	            throw new BusinessException(e);
	        }
	        VocabularyDTO vocabulary = VocabularyDTO.builder().
	        		entryDTO(entries).system(cdaCodeSystem).
	        		build();
	        vocabularyList.add(vocabulary);
	        return vocabularyList;
	    } 

	 	private void saveCdaVocabulary() {
	 		Map<String, List<VocabularyEntryDTO>> mapVocabuary = new HashMap<>();
	 		List<VocabularyEntryDTO> vocabulary = new ArrayList<>();
	 		VocabularyEntryDTO voc = VocabularyEntryDTO.builder().code("P").description("P").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.5.7", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		voc = VocabularyEntryDTO.builder().code("PRE").description("PRE").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.2.9.5.1.88", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		voc = VocabularyEntryDTO.builder().code("N").description("N").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.5.25", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		VocabularyEntryDTO voc1 = VocabularyEntryDTO.builder().code("11502-2").description("11502-2").build();
	 		VocabularyEntryDTO voc2 = VocabularyEntryDTO.builder().code("18729-4").description("18729-4").build();
	 		VocabularyEntryDTO voc3 = VocabularyEntryDTO.builder().code("14957-5").description("14957-5").build();
	 		VocabularyEntryDTO voc4 = VocabularyEntryDTO.builder().code("48767-8").description("48767-8").build();
	 		VocabularyEntryDTO voc5 = VocabularyEntryDTO.builder().code("30525-0").description("30525-0").build();
	 		vocabulary.add(voc1);
	 		vocabulary.add(voc2);
	 		vocabulary.add(voc3);
	 		vocabulary.add(voc4);
	 		vocabulary.add(voc4);
	 		vocabulary.add(voc5);
	 		mapVocabuary.put("2.16.840.1.113883.6.1", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		voc = VocabularyEntryDTO.builder().code("UR").description("UR").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.5.129", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		voc = VocabularyEntryDTO.builder().code("M").description("M").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.5.1", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		voc = VocabularyEntryDTO.builder().code("0090334.02").description("0090334.02").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.2.9.2.30.6.11", vocabulary);
	 		vocabulary = new ArrayList<>();
	 		voc = VocabularyEntryDTO.builder().code("N").description("N").build(); 
	 		vocabulary.add(voc);
	 		mapVocabuary.put("2.16.840.1.113883.5.83", vocabulary);
	 		 
	 		List<VocabularyETY> listToAdd = new ArrayList<>();
	 		for(Entry<String, List<VocabularyEntryDTO>> map : mapVocabuary.entrySet()) {
	 			for(VocabularyEntryDTO dto : map.getValue()) {
	 				VocabularyETY ety = new VocabularyETY();
	 				ety.setSystem(map.getKey());
	 				ety.setCode(dto.getCode());
	 				ety.setDescription(dto.getDescription());
	 				listToAdd.add(ety);
	 			}
	 		}
	 		
	 		repo.insertAll(listToAdd);
	 	}
	    private List<XslTransformDTO> getXslTransform() {
	    	List<XslTransformDTO> out = new ArrayList<>();

	    	XslTransformDTO xsl = XslTransformDTO.builder().
	    			version(versionXsl).
	    			entryListDTO(getEntryXslTransform()).build();
	    	out.add(xsl);
	    	return out;
	    }
	    
	    private List<XslTransformEntryDTO> getEntryXslTransform(){
	    	List<XslTransformEntryDTO> out = new ArrayList<>(); 
	    	String fileName = "vaccination.xsl";
	    	XslTransformEntryDTO ety = XslTransformEntryDTO.builder().
	    			cdaType(cdaType).
	    			contentXslTransform(new Binary(BsonBinarySubType.BINARY, FileUtility.getFileFromInternalResources("Files" + File.separator + "xslTransform" + File.separator + fileName))).
	    			nameXslTransform(fileName).build();
	    	out.add(ety);
	    	return out;
	    }
	    
	    private List<SchematronDTO> getSchematronDTO() {
	    	List<SchematronDTO> out = new ArrayList<>();
	    	SchematronDTO schematron = SchematronDTO.builder().
	    			templateIdRoot(templateIdRoot).
	    			fatherSchematron(getSchematronFatherFromFS()).
	    			childrenSchematronList(getSchematronListChildFromFS()).
	    			xsdVersion(versionXsd).
	    			build();
	    	out.add(schematron);
	    	return out;
	    }
	    
	    private SchematronEntryDTO getSchematronFatherFromFS() {
	    	String nameFile = "schematronFSE.sch.xsl";
	    	SchematronEntryDTO schematron = SchematronEntryDTO.builder().
	    			cdaCode(cdaCode).
	    			cdaCodeSystem(cdaCodeSystem).
	    			contentSchematron(new Binary(BsonBinarySubType.BINARY, FileUtility.getFileFromInternalResources("Files" + File.separator + "schematron" + File.separator + nameFile))).
	    			templateIdExtension(templateIdExtension).
	    			nameSchematron(nameFile).
	    			build();
	    	return schematron;
	    }

	    private List<SchematronEntryDTO> getSchematronListChildFromFS() {
	    	List<String> fileNameSchematronList = new ArrayList<>();
	    	fileNameSchematronList.add("2.16.840.1.113883.6.1.xml");
	    	
	    	List<SchematronEntryDTO> output = new ArrayList<>();
	    	for(String fileNameSchematron : fileNameSchematronList) {
	    		SchematronEntryDTO schematron = SchematronEntryDTO.builder().
	        			cdaCode(cdaCode).
	        			cdaCodeSystem(cdaCodeSystem).
	        			contentSchematron(new Binary(BsonBinarySubType.BINARY, FileUtility.getFileFromInternalResources("Files" + File.separator + "schematron" + File.separator + fileNameSchematron))).
	        			templateIdExtension(templateIdExtension).
	        			nameSchematron(fileNameSchematron).
	        			build();
	    		output.add(schematron);
	    	}
	    	return output;
	    }

	    private SchemaDTO getSchemaDTO() {
	    	SchemaDTO out = SchemaDTO.builder().
	    			version(versionXsd).
	    			schemaFatherEntryDTO(getSchemaFatherFromFS()).
	    			schemaChildEntryDTO(getSchemasChildFromFS()).
	    			build();
	    	return out;
	    }
	    
	    private List<SchemaEntryDTO> getSchemasChildFromFS() {
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

	        List<SchemaEntryDTO> output = new ArrayList<>();
	        for(String schemaFile : schemaFiles) {
	        	SchemaEntryDTO schema = SchemaEntryDTO.builder().
	        			cdaType(cdaType).
	        			contentSchema(new Binary(BsonBinarySubType.BINARY, FileUtility.getFileFromInternalResources("Files" + File.separator + "schema" + File.separator + "coreschemas" + File.separator + schemaFile))).
	        			nameSchema(schemaFile).build();
	        	output.add(schema);
	        }
	        return output;
	    }
	    
	    private SchemaEntryDTO getSchemaFatherFromFS() {
	    	String nameSchema = "CDA.xsd";
	    	SchemaEntryDTO schema = SchemaEntryDTO.builder().
	    			cdaType(cdaType).
	    			contentSchema(new Binary(BsonBinarySubType.BINARY, FileUtility.getFileFromInternalResources("Files" + File.separator + "schema" + File.separator + nameSchema))).
	    			nameSchema(nameSchema).build();
	    	return schema;
	    }
}
