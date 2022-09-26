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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyBuilderTestDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.DictionaryRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.SchemaRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.SchematronRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.TerminologyRepo;
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

	private static final String versionXsd = "POCD_MT000040UV02";

	private static final String cdaCodeSystem = "2.16.840.1.113883.6.1";

	@Autowired
	private TerminologyRepo terminologyRepo;
	
	@Autowired
	private SchemaRepo schemaRepo;
	
	@Autowired
	private SchematronRepo schematronRepo;
	
	@Autowired
	private DictionaryRepo dictionaryRepo;
	
	@Override
	public ConfigItemDTO mockConfigurationItem() {
		ConfigItemDTO configurationItems = null;
		try {
			configurationItems = ConfigItemDTO.builder()
					.schema(getSchemaDTO())
					.schematron(getSchematronDTO())
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
		VocabularyEntryDTO vocR = VocabularyEntryDTO.builder().code("R").description("R").build();
		vocabulary.add(voc);
		vocabulary.add(vocR);
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
		VocabularyEntryDTO voc6 = VocabularyEntryDTO.builder().code("LA28752-6").description("LA28752-6").build();
		VocabularyEntryDTO voc7 = VocabularyEntryDTO.builder().code("LA16666-2").description("LA16666-2").build();
		VocabularyEntryDTO voc8 = VocabularyEntryDTO.builder().code("LA18821-1").description("LA18821-1").build();
		VocabularyEntryDTO voc9 = VocabularyEntryDTO.builder().code("LA18632-2").description("LA18632-2").build();
		VocabularyEntryDTO voc10 = VocabularyEntryDTO.builder().code("LA4270-0").description("LA4270-0").build();
		
		vocabulary.add(voc1);
		vocabulary.add(voc2);
		vocabulary.add(voc3);
		vocabulary.add(voc4);
		vocabulary.add(voc4);
		vocabulary.add(voc5);
		vocabulary.add(voc6);
		vocabulary.add(voc7);
		vocabulary.add(voc8);
		vocabulary.add(voc9);
		vocabulary.add(voc10);
		
		mapVocabuary.put("2.16.840.1.113883.6.1", vocabulary);
		vocabulary = new ArrayList<>();
		voc = VocabularyEntryDTO.builder().code("UR").description("UR").build(); 
		vocabulary.add(voc);
		mapVocabuary.put("2.16.840.1.113883.5.129", vocabulary);
		vocabulary = new ArrayList<>();
		voc = VocabularyEntryDTO.builder().code("M").description("M").build();
		VocabularyEntryDTO vocF = VocabularyEntryDTO.builder().code("F").description("F").build();
		vocabulary.add(voc);
		vocabulary.add(vocF);
		mapVocabuary.put("2.16.840.1.113883.5.1", vocabulary);
		vocabulary = new ArrayList<>();
		voc = VocabularyEntryDTO.builder().code("0090334.02").description("0090334.02").build();
		VocabularyEntryDTO vocDiagnosi = VocabularyEntryDTO.builder().code("[COD_DIAGNOSI]").description("[COD_DIAGNOSI]").build();
		vocabulary.add(voc);
		vocabulary.add(vocDiagnosi);
		mapVocabuary.put("2.16.840.1.113883.2.9.2.30.6.11", vocabulary);
		vocabulary = new ArrayList<>();
		voc = VocabularyEntryDTO.builder().code("N").description("N").build(); 
		vocabulary.add(voc);
		voc = VocabularyEntryDTO.builder().code("B").description("B").build();
		vocabulary.add(voc);
		mapVocabuary.put("2.16.840.1.113883.5.83", vocabulary);

		VocabularyEntryDTO vocIAB = VocabularyEntryDTO.builder().code("IABDINJ").description("IABDINJ").build();
		VocabularyEntryDTO vocPO = VocabularyEntryDTO.builder().code("PO").description("PO").build();
		VocabularyEntryDTO vocSQ = VocabularyEntryDTO.builder().code("SQ").description("SQ").build();
		VocabularyEntryDTO vocIM = VocabularyEntryDTO.builder().code("IM").description("IM").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocIAB);
		vocabulary.add(vocPO);
		vocabulary.add(vocSQ);
		vocabulary.add(vocIM);
		mapVocabuary.put("2.16.840.1.113883.5.112", vocabulary);
		 
		VocabularyEntryDTO vocQS = VocabularyEntryDTO.builder().code("qs").description("qs").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocQS);
		mapVocabuary.put("2.16.840.1.113883.4.642.3.1426", vocabulary);
		
		VocabularyEntryDTO voc7654 = VocabularyEntryDTO.builder().code("7654.321").description("7654.321").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc7654);
		mapVocabuary.put("2.16.840.1.113883.6.99.99.99", vocabulary);
		
		
		VocabularyEntryDTO vocT1 = VocabularyEntryDTO.builder().code("385219001").description("385219001").build();
		VocabularyEntryDTO vocT2 = VocabularyEntryDTO.builder().code("46992007").description("46992007").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocT1);
		vocabulary.add(vocT2);
		mapVocabuary.put("2.16.840.1.113883.4.642.3.374", vocabulary);
		
		VocabularyEntryDTO vocM = VocabularyEntryDTO.builder().code("M").description("M").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocM);
		vocabulary.add(vocT2);
		mapVocabuary.put("2.16.840.1.113883.5.1063", vocabulary);
		
		VocabularyEntryDTO vocLA = VocabularyEntryDTO.builder().code("LA").description("LA").build();
		VocabularyEntryDTO vocRA = VocabularyEntryDTO.builder().code("RA").description("RA").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocLA);
		vocabulary.add(vocRA);
		mapVocabuary.put("2.16.840.1.113883.5.1052", vocabulary);
		
		VocabularyEntryDTO voc30001 = VocabularyEntryDTO.builder().code("30001").description("30001").build();
		VocabularyEntryDTO voc1535 = VocabularyEntryDTO.builder().code("1535").description("1535").build();
		VocabularyEntryDTO voc0321 = VocabularyEntryDTO.builder().code("0321").description("0321").build();
		VocabularyEntryDTO voc3960 = VocabularyEntryDTO.builder().code("3960").description("3960").build();
		VocabularyEntryDTO voc477 = VocabularyEntryDTO.builder().code("477.2").description("477.2").build();
		VocabularyEntryDTO voc4280 = VocabularyEntryDTO.builder().code("4280").description("4280").build();
		VocabularyEntryDTO voc5756 = VocabularyEntryDTO.builder().code("5756").description("5756").build();
		VocabularyEntryDTO voc87 = VocabularyEntryDTO.builder().code("87.3").description("87.3").build();
		VocabularyEntryDTO vocD = VocabularyEntryDTO.builder().code("[COD_DIAGNOSI]").description("[COD_DIAGNOSI]").build();

		VocabularyEntryDTO voc2512 = VocabularyEntryDTO.builder().code("2512").description("2512").build();
		VocabularyEntryDTO voc9054 = VocabularyEntryDTO.builder().code("9054").description("9054").build();
		VocabularyEntryDTO voc79021 = VocabularyEntryDTO.builder().code("79021").description("79021").build();
		VocabularyEntryDTO vocXXXX = VocabularyEntryDTO.builder().code("XXX").description("XXX").build();
		
		
		VocabularyEntryDTO voc493 = VocabularyEntryDTO.builder().code("493.9").description("493.9").build();
		VocabularyEntryDTO voc787 = VocabularyEntryDTO.builder().code("787.02").description("787.02").build();
		VocabularyEntryDTO voc369 = VocabularyEntryDTO.builder().code("369.2").description("369.2").build();
		VocabularyEntryDTO voc8872 = VocabularyEntryDTO.builder().code("8872").description("8872").build();
		VocabularyEntryDTO voc410 = VocabularyEntryDTO.builder().code("410").description("410").build();
		VocabularyEntryDTO voc41000 = VocabularyEntryDTO.builder().code("41000").description("41000").build();
		VocabularyEntryDTO vocCOD_CONSRISK = VocabularyEntryDTO.builder().code("[COD_CONSRISK]").description("[COD_CONSRISK]").build();
		
		
		vocabulary = new ArrayList<>();
		vocabulary.add(voc30001);
		vocabulary.add(voc1535);
		vocabulary.add(voc0321);
		vocabulary.add(voc3960);
		vocabulary.add(voc477);
		vocabulary.add(voc4280);
		vocabulary.add(voc5756);
		vocabulary.add(voc87);
		vocabulary.add(vocD);
		vocabulary.add(vocXXXX);
		
		vocabulary.add(voc2512);
		vocabulary.add(voc9054);
		vocabulary.add(voc79021);
		
		vocabulary.add(voc410);
		vocabulary.add(voc8872);
		vocabulary.add(voc493);
		vocabulary.add(voc787);
		vocabulary.add(voc369);
		vocabulary.add(voc41000);
		vocabulary.add(vocCOD_CONSRISK);
		

		mapVocabuary.put("2.16.840.1.113883.6.103", vocabulary);
		
		VocabularyEntryDTO voc260152009 = VocabularyEntryDTO.builder().code("260152009").description("260152009").build();
		VocabularyEntryDTO voc111088007 = VocabularyEntryDTO.builder().code("111088007").description("111088007").build();
		
		vocabulary = new ArrayList<>();
		vocabulary.add(voc260152009);
		vocabulary.add(voc111088007);
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.2", vocabulary);

		VocabularyEntryDTO vocOINT = VocabularyEntryDTO.builder().code("OINT").description("OINT").build();
		VocabularyEntryDTO vocSEV = VocabularyEntryDTO.builder().code("SEV").description("SEV").build();
		VocabularyEntryDTO vocALG = VocabularyEntryDTO.builder().code("ALG").description("ALG").build();
		VocabularyEntryDTO vocIMMUNIZ = VocabularyEntryDTO.builder().code("IMMUNIZ").description("IMMUNIZ").build();
		VocabularyEntryDTO vocIMP = VocabularyEntryDTO.builder().code("IMP").description("IMP").build();
		VocabularyEntryDTO vocCARD = VocabularyEntryDTO.builder().code("CARD").description("CARD").build();
		VocabularyEntryDTO vocASSERTION = VocabularyEntryDTO.builder().code("ASSERTION").description("ASSERTION").build();
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocASSERTION);
		vocabulary.add(vocCARD);
		vocabulary.add(vocOINT);
		vocabulary.add(vocSEV);
		vocabulary.add(vocALG);
		vocabulary.add(vocIMMUNIZ);
		vocabulary.add(vocIMP);
		
		mapVocabuary.put("2.16.840.1.113883.5.4", vocabulary);
		
		VocabularyEntryDTO voc035606033 = VocabularyEntryDTO.builder().code("035606033").description("035606033").build();
		VocabularyEntryDTO voc043348022 = VocabularyEntryDTO.builder().code("043348022").description("043348022").build();
		VocabularyEntryDTO voc023993013 = VocabularyEntryDTO.builder().code("023993013").description("023993013").build();
		VocabularyEntryDTO voc035911015 = VocabularyEntryDTO.builder().code("035911015").description("035911015").build();
		
		vocabulary = new ArrayList<>();
		vocabulary.add(voc035606033);
		vocabulary.add(voc043348022);
		vocabulary.add(voc023993013);
		vocabulary.add(voc035911015);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.5", vocabulary);
		
		VocabularyEntryDTO vocXX = VocabularyEntryDTO.builder().code("XX").description("XX").build();
		VocabularyEntryDTO vocXXX = VocabularyEntryDTO.builder().code("XXX").description("XXX").build();
		VocabularyEntryDTO vocC43168 = VocabularyEntryDTO.builder().code("C43168").description("C43168").build();
		VocabularyEntryDTO vocRDP_CODE = VocabularyEntryDTO.builder().code("[RDP_CODE]").description("[RDP_CODE]").build();
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXX);
		vocabulary.add(vocXXX);
		vocabulary.add(vocC43168);
		vocabulary.add(vocRDP_CODE);
		
		mapVocabuary.put("2.16.840.1.113883.9.999", vocabulary);
		
		VocabularyEntryDTO vocC08CA01 = VocabularyEntryDTO.builder().code("C08CA01").description("C08CA01").build();
		VocabularyEntryDTO vocB01AX05 = VocabularyEntryDTO.builder().code("B01AX05").description("B01AX05").build();
		VocabularyEntryDTO vocC03CA01 = VocabularyEntryDTO.builder().code("C03CA01").description("C03CA01").build();
		VocabularyEntryDTO vocC02LA01 = VocabularyEntryDTO.builder().code("C02LA01").description("C02LA01").build();
		VocabularyEntryDTO vocN01AX10 = VocabularyEntryDTO.builder().code("N01AX10").description("N01AX10").build();
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocN01AX10);
		vocabulary.add(vocC08CA01);
		vocabulary.add(vocB01AX05);
		vocabulary.add(vocC03CA01);
		vocabulary.add(vocC02LA01);
		mapVocabuary.put("2.16.840.1.113883.6.73", vocabulary);
		
		VocabularyEntryDTO vocMTH = VocabularyEntryDTO.builder().code("MTH").description("MTH").build();
		VocabularyEntryDTO vocFTH = VocabularyEntryDTO.builder().code("FTH").description("FTH").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocMTH);
		vocabulary.add(vocFTH);
		mapVocabuary.put("2.16.840.1.113883.5.111", vocabulary);
		

		VocabularyEntryDTO voc121181 = VocabularyEntryDTO.builder().code("121181").description("121181").build();
		VocabularyEntryDTO voc113014 = VocabularyEntryDTO.builder().code("113014").description("113014").build();
		VocabularyEntryDTO voc113015 = VocabularyEntryDTO.builder().code("113015").description("113015").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc121181);
		vocabulary.add(voc113014);
		vocabulary.add(voc113015);
		mapVocabuary.put("1.2.840.10008.2.16.4", vocabulary);
		
		VocabularyEntryDTO voc002 = VocabularyEntryDTO.builder().code("002").description("002").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc002);
		mapVocabuary.put("2.16.840.1.113883.2.9.2.120.4.4", vocabulary);
		
		VocabularyEntryDTO vocRAD_PROG = VocabularyEntryDTO.builder().code("RAD_PROG").description("RAD_PROG").build();
		VocabularyEntryDTO vocPROG = VocabularyEntryDTO.builder().code("PROG").description("PROG").build();
		VocabularyEntryDTO vocDIR = VocabularyEntryDTO.builder().code("DIR").description("DIR").build();
		VocabularyEntryDTO vocdimissione = VocabularyEntryDTO.builder().code("dimissione").description("dimissione").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocRAD_PROG);
		vocabulary.add(vocPROG);
		vocabulary.add(vocDIR);
		vocabulary.add(vocdimissione);
		mapVocabuary.put("2.16.840.1.113883.2.9.5.1.4", vocabulary);
		
		VocabularyEntryDTO vocPRE = VocabularyEntryDTO.builder().code("PRE").description("PRE").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocPRE);
		mapVocabuary.put("2.16.840.1.113883.2.9.5.1.1.88", vocabulary);
		
		VocabularyEntryDTO voc3211 = VocabularyEntryDTO.builder().code("3211").description("3211").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc3211);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.2.7", vocabulary);
		
		VocabularyEntryDTO voc877 = VocabularyEntryDTO.builder().code("87.7").description("87.7").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc877);
		mapVocabuary.put("2.16.840.1.113883.6.106", vocabulary);
		
		VocabularyEntryDTO voc698 = VocabularyEntryDTO.builder().code("698.8").description("698.8").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc698);
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.4", vocabulary);
		
		VocabularyEntryDTO voc012311B = VocabularyEntryDTO.builder().code("012311B").description("012311B").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc012311B);
		mapVocabuary.put("2.16.840.1.113883.2.9.2.120.6.11", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.4", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.11", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.10", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.13", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.12", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.5", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.6", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.1", vocabulary);
		
		vocabulary = new ArrayList<>();
		vocabulary.add(vocXXX);
		vocabulary.add(vocR);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.4", vocabulary);
		
		voc = VocabularyEntryDTO.builder().code("15").description("15").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.2", vocabulary);
		
		voc = VocabularyEntryDTO.builder().code("IMP").description("IMP").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc);
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.9", vocabulary);
		
		
		VocabularyEntryDTO vocPss1 = VocabularyEntryDTO.builder().code("257893003").description("257893003").build();
		VocabularyEntryDTO vocPss2 = VocabularyEntryDTO.builder().code("257893003").description("257893003").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocPss1);
		vocabulary.add(vocPss2);
		mapVocabuary.put("2.16.840.1.113883.4.642.3.395", vocabulary);
		
		
		VocabularyEntryDTO vocJ010103 = VocabularyEntryDTO.builder().code("J010103").description("J010103").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocJ010103);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.48", vocabulary);
		
		VocabularyEntryDTO vocMg = VocabularyEntryDTO.builder().code("mg{creat}").description("mg{creat}").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocMg);
		mapVocabuary.put("2.16.840.1.113883.6.8", vocabulary);
		
		VocabularyEntryDTO vocQ13 = VocabularyEntryDTO.builder().code("Q13.1").description("Q13.1").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocQ13);
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.17", vocabulary);
		
		VocabularyEntryDTO voc02 = VocabularyEntryDTO.builder().code("02").description("02").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(voc02);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.22", vocabulary);
		
		VocabularyEntryDTO vocPSSADI = VocabularyEntryDTO.builder().code("PSSADI").description("PSSADI").build();
		VocabularyEntryDTO vocPSSIT99 = VocabularyEntryDTO.builder().code("PSSIT99").description("PSSIT99").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocPSSADI);
		vocabulary.add(vocPSSIT99);
		mapVocabuary.put("2.16.840.1.113883.2.9.5.2.8", vocabulary);
		
		VocabularyEntryDTO vocMMG = VocabularyEntryDTO.builder().code("MMG").description("MMG").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocMMG);
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.13", vocabulary);
		
		VocabularyEntryDTO vocCODRISCHIO = VocabularyEntryDTO.builder().code("[COD_RISCHIO]").description("[COD_RISCHIO]").build();
		vocabulary = new ArrayList<>();
		vocabulary.add(vocCODRISCHIO);
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.56.2", vocabulary);
		  
		
		
		List<TerminologyETY> listToAdd = new ArrayList<>();
		for(Entry<String, List<VocabularyEntryDTO>> map : mapVocabuary.entrySet()) {
			for(VocabularyEntryDTO dto : map.getValue()) {
				TerminologyETY ety = new TerminologyETY();
				ety.setSystem(map.getKey());
				ety.setCode(dto.getCode());
				ety.setDescription(dto.getDescription());
				listToAdd.add(ety);
			}
		}

		terminologyRepo.insertAll(listToAdd);
	}
	 

	private List<SchematronDTO> getSchematronDTO() {
		List<SchematronDTO> out = getSchematronFiles();
		return out;
	}


	private SchemaDTO getSchemaDTO() {
		SchemaDTO out = SchemaDTO.builder().
				typeIdExtension(versionXsd).
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
	
	
	private List<SchematronDTO> getSchematronFiles() {
		List<SchematronDTO> out = new ArrayList<>();

		Map<String,String> schematronTemplateMap = buildMapSchematronTemplate();
		try {
			 
			for(Entry<String, String> schematron : schematronTemplateMap.entrySet()) {
				String nameFile = schematron.getKey();
				byte[] content = FileUtility.getFileFromInternalResources("Files" + File.separator + "schematron" + File.separator + nameFile);
				SchematronDTO schematronToSave = SchematronDTO.builder().
						contentSchematron(new Binary(BsonBinarySubType.BINARY, content)).
						nameSchematron(nameFile).
						templateIdExtension("4.0").
						templateIdRoot(schematron.getValue()).
						build();
				out.add(schematronToSave);
			}
			 
		} catch(Exception ex) {
			log.error("Error while get schematron files : " + ex);
			throw new BusinessException("Error while get schematron files : " + ex);
		}
		return out;
	}
	
	private Map<String,String> buildMapSchematronTemplate(){
		Map<String,String> mapSchematronRoot = new HashMap<>();
		try { 

			mapSchematronRoot.put("schematron_certificato_VACC v1.3.sch" , "2.16.840.1.113883.2.9.10.1.11.1.2");
			mapSchematronRoot.put("schematron_PSS_v2.7.sch" , "2.16.840.1.113883.2.9.10.1.4.1.1");
			mapSchematronRoot.put("schematron_RSA_v7.2.sch" , "2.16.840.1.113883.2.9.10.1.9.1");
			mapSchematronRoot.put("schematron_singola_VACC v2.2.sch" , "2.16.840.1.113883.2.9.10.1.11.1.1");
			mapSchematronRoot.put("schematron_VPS_v 2.7.sch" , "2.16.840.1.113883.2.9.10.1.6.1");
			mapSchematronRoot.put("schematronFSE_LDO_V4.8.sch" , "2.16.840.1.113883.2.9.10.1.5");
			mapSchematronRoot.put("schematronFSE_RAD_v3.0.sch" , "2.16.840.1.113883.2.9.10.1.7.1");
			mapSchematronRoot.put("schematronFSEv17.sch" , "2.16.840.1.113883.2.9.10.1.1");
			
		}catch(Exception ex) {
			log.error("Error while build map : " , ex);
			throw new BusinessException("Error while build map : " , ex);
		}
		return mapSchematronRoot;
	}

	@Override
	public void dropCollections() {
		terminologyRepo.dropCollection();
		dictionaryRepo.dropCollection();
		schemaRepo.dropCollection();
		schematronRepo.dropCollection();
	}

	 
}
