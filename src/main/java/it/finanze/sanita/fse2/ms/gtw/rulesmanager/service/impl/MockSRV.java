package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyBuilderTestDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IXslTransformRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.StructureDefinitionETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.StructureMapETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.ValuesetETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;
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

	private static final String LOINC = "2.16.840.1.113883.6.1";

	@Autowired
	private ITerminologyRepo vocabularyRepo;
 

	@Autowired
	private IXslTransformRepo xslTransformRepo;

	@Autowired
	private MongoTemplate mongoTemplate;


	@Override
	public void saveMockConfigurationItem() {
		try {
			saveSchematronFiles();
			saveXslFiles();
			saveTerminologyFromExcel();
			saveOtherTerminology();
			saveStructureDefinition();
			saveStructureMap();
			saveValuset();
		} catch (Exception ex) {
			log.error("Error encountered while while save mock configuration item", ex);
			throw new BusinessException(ex);
		}
	}


	@SuppressWarnings("all")
	private void saveTerminologyFromExcel() {

		List<TerminologyETY> terminologyList = new ArrayList<>();
		final String csvFileName = "LoincTableCore.csv";
		final byte[] csvContent = FileUtility.getFileFromInternalResources("Files" + File.separator + "vocabulary" + File.separator + csvFileName);

		try (InputStream targetStream = new ByteArrayInputStream(csvContent);
				Reader reader = new InputStreamReader(targetStream);) {
			List<VocabularyBuilderTestDTO> vocabularyListDTO = new CsvToBeanBuilder(reader).withType(VocabularyBuilderTestDTO.class).withSeparator(',').build().parse();
			vocabularyListDTO.remove(0);

			for(VocabularyBuilderTestDTO vocabularyDTO : vocabularyListDTO) {
				TerminologyETY terminology = new TerminologyETY();
				terminology.setCode(vocabularyDTO.getCode());
				terminology.setDescription(vocabularyDTO.getDescription());
				terminology.setSystem(LOINC);
				terminologyList.add(terminology);
			}
			mongoTemplate.insertAll(terminologyList);
		} catch (Exception e) {
			log.error("Error while reading vocabulary file", e);
			throw new BusinessException(e);
		}

	} 

	private List<VocabularyEntryDTO> buildLoincList() {
		List<VocabularyEntryDTO> out = new ArrayList<>(); 
		VocabularyEntryDTO loinc1 = VocabularyEntryDTO.builder().code("11502-2").description("11502-2").build();
		VocabularyEntryDTO loinc2 = VocabularyEntryDTO.builder().code("18729-4").description("18729-4").build();
		VocabularyEntryDTO loinc3 = VocabularyEntryDTO.builder().code("14957-5").description("14957-5").build();
		VocabularyEntryDTO loinc4 = VocabularyEntryDTO.builder().code("48767-8").description("48767-8").build();
		VocabularyEntryDTO loinc5 = VocabularyEntryDTO.builder().code("30525-0").description("30525-0").build();
		VocabularyEntryDTO loinc6 = VocabularyEntryDTO.builder().code("LA28752-6").description("LA28752-6").build();
		VocabularyEntryDTO loinc7 = VocabularyEntryDTO.builder().code("LA16666-2").description("LA16666-2").build();
		VocabularyEntryDTO loinc8 = VocabularyEntryDTO.builder().code("LA18821-1").description("LA18821-1").build();
		VocabularyEntryDTO loinc9 = VocabularyEntryDTO.builder().code("LA18632-2").description("LA18632-2").build();
		VocabularyEntryDTO loinc10 = VocabularyEntryDTO.builder().code("LA4270-0").description("LA4270-0").build();

		out.add(loinc1);
		out.add(loinc2);
		out.add(loinc3);
		out.add(loinc4);
		out.add(loinc5);
		out.add(loinc6);
		out.add(loinc7);
		out.add(loinc8);
		out.add(loinc9);
		out.add(loinc10);
		return out;
	}

	private List<VocabularyEntryDTO> build6103Vocabulary(){
		List<VocabularyEntryDTO> out = new ArrayList<>();
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

		out.add(voc30001);
		out.add(voc1535);
		out.add(voc0321);
		out.add(voc3960);
		out.add(voc477);
		out.add(voc4280);
		out.add(voc5756);
		out.add(voc87);
		out.add(vocD);
		out.add(vocXXXX);
		out.add(voc2512);
		out.add(voc9054);
		out.add(voc79021);
		out.add(voc410);
		out.add(voc8872);
		out.add(voc493);
		out.add(voc787);
		out.add(voc369);
		out.add(voc41000);
		out.add(vocCOD_CONSRISK);
		return out;
	}

	private List<VocabularyEntryDTO> build54Vocabulary() {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocOINT = VocabularyEntryDTO.builder().code("OINT").description("OINT").build();
		VocabularyEntryDTO vocSEV = VocabularyEntryDTO.builder().code("SEV").description("SEV").build();
		VocabularyEntryDTO vocALG = VocabularyEntryDTO.builder().code("ALG").description("ALG").build();
		VocabularyEntryDTO vocIMMUNIZ = VocabularyEntryDTO.builder().code("IMMUNIZ").description("IMMUNIZ").build();
		VocabularyEntryDTO vocIMP = VocabularyEntryDTO.builder().code("IMP").description("IMP").build();
		VocabularyEntryDTO vocCARD = VocabularyEntryDTO.builder().code("CARD").description("CARD").build();
		VocabularyEntryDTO vocASSERTION = VocabularyEntryDTO.builder().code("ASSERTION").description("ASSERTION").build();

		out.add(vocASSERTION);
		out.add(vocCARD);
		out.add(vocOINT);
		out.add(vocSEV);
		out.add(vocALG);
		out.add(vocIMMUNIZ);
		out.add(vocIMP);
		return out;
	}

	private List<VocabularyEntryDTO> build57Vocabulary() {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO voc = VocabularyEntryDTO.builder().code("P").description("P").build();
		VocabularyEntryDTO vocR = VocabularyEntryDTO.builder().code("R").description("R").build();
		out.add(voc);
		out.add(vocR);
		return out;
	}

	private List<VocabularyEntryDTO> build188Vocabulary() {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO voc = VocabularyEntryDTO.builder().code("PRE").description("PRE").build(); 
		out.add(voc);
		return out;
	}

	private List<VocabularyEntryDTO> build525Vocabulary() {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO voc = VocabularyEntryDTO.builder().code("PRE").description("PRE").build(); 
		out.add(voc);
		VocabularyEntryDTO voc1 = VocabularyEntryDTO.builder().code("N").description("N").build(); 
		out.add(voc1);
		return out;
	}

	private List<VocabularyEntryDTO> build673Vocabulary() {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocC08CA01 = VocabularyEntryDTO.builder().code("C08CA01").description("C08CA01").build();
		VocabularyEntryDTO vocB01AX05 = VocabularyEntryDTO.builder().code("B01AX05").description("B01AX05").build();
		VocabularyEntryDTO vocC03CA01 = VocabularyEntryDTO.builder().code("C03CA01").description("C03CA01").build();
		VocabularyEntryDTO vocC02LA01 = VocabularyEntryDTO.builder().code("C02LA01").description("C02LA01").build();
		VocabularyEntryDTO vocN01AX10 = VocabularyEntryDTO.builder().code("N01AX10").description("N01AX10").build();
		out.add(vocN01AX10);
		out.add(vocC08CA01);
		out.add(vocB01AX05);
		out.add(vocC03CA01);
		out.add(vocC02LA01);
		return out;
	}

	private List<VocabularyEntryDTO> build5112Vocabulary() {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocIAB = VocabularyEntryDTO.builder().code("IABDINJ").description("IABDINJ").build();
		VocabularyEntryDTO vocPO = VocabularyEntryDTO.builder().code("PO").description("PO").build();
		VocabularyEntryDTO vocSQ = VocabularyEntryDTO.builder().code("SQ").description("SQ").build();
		VocabularyEntryDTO vocIM = VocabularyEntryDTO.builder().code("IM").description("IM").build();
		out.add(vocIAB);
		out.add(vocPO);
		out.add(vocSQ);
		out.add(vocIM);

		return out;
	}

	private List<VocabularyEntryDTO> build9999Vocabulary(VocabularyEntryDTO vocXXX) {
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocXX = VocabularyEntryDTO.builder().code("XX").description("XX").build();
		VocabularyEntryDTO vocC43168 = VocabularyEntryDTO.builder().code("C43168").description("C43168").build();
		VocabularyEntryDTO vocRDP_CODE = VocabularyEntryDTO.builder().code("[RDP_CODE]").description("[RDP_CODE]").build();

		out.add(vocXX);
		out.add(vocXXX);
		out.add(vocC43168);
		out.add(vocRDP_CODE);

		return out;
	}

	private List<VocabularyEntryDTO> build14Vocabulary(){
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocRAD_PROG = VocabularyEntryDTO.builder().code("RAD_PROG").description("RAD_PROG").build();
		VocabularyEntryDTO vocPROG = VocabularyEntryDTO.builder().code("PROG").description("PROG").build();
		VocabularyEntryDTO vocDIR = VocabularyEntryDTO.builder().code("DIR").description("DIR").build();
		VocabularyEntryDTO vocdimissione = VocabularyEntryDTO.builder().code("dimissione").description("dimissione").build();

		out.add(vocRAD_PROG);
		out.add(vocPROG);
		out.add(vocDIR);
		out.add(vocdimissione);

		return out;
	}

	private List<VocabularyEntryDTO> build15Vocabulary(){
		List<VocabularyEntryDTO> out = new ArrayList<>();

		VocabularyEntryDTO voc035606033 = VocabularyEntryDTO.builder().code("035606033").description("035606033").build();
		VocabularyEntryDTO voc043348022 = VocabularyEntryDTO.builder().code("043348022").description("043348022").build();
		VocabularyEntryDTO voc023993013 = VocabularyEntryDTO.builder().code("023993013").description("023993013").build();
		VocabularyEntryDTO voc035911015 = VocabularyEntryDTO.builder().code("035911015").description("035911015").build();

		out.add(voc035606033);
		out.add(voc043348022);
		out.add(voc023993013);
		out.add(voc035911015);
		return out;
	}

	private List<VocabularyEntryDTO> build164Vocabulary(){
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO voc121181 = VocabularyEntryDTO.builder().code("121181").description("121181").build();
		VocabularyEntryDTO voc113014 = VocabularyEntryDTO.builder().code("113014").description("113014").build();
		VocabularyEntryDTO voc113015 = VocabularyEntryDTO.builder().code("113015").description("113015").build();
		out.add(voc121181);
		out.add(voc113014);
		out.add(voc113015);
		return out;
	}

	private List<VocabularyEntryDTO> build28Vocabulary(){
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocPSSADI = VocabularyEntryDTO.builder().code("PSSADI").description("PSSADI").build();
		VocabularyEntryDTO vocPSSIT99 = VocabularyEntryDTO.builder().code("PSSIT99").description("PSSIT99").build();
		out.add(vocPSSADI);
		out.add(vocPSSIT99);
		return out;
	}

	private List<VocabularyEntryDTO> build3395Vocabulary(){
		List<VocabularyEntryDTO> out = new ArrayList<>();
		VocabularyEntryDTO vocPss1 = VocabularyEntryDTO.builder().code("257893003").description("257893003").build();
		VocabularyEntryDTO vocPss2 = VocabularyEntryDTO.builder().code("257893003").description("257893003").build();
		out.add(vocPss1);
		out.add(vocPss2);
		return out;
	}

	private void saveOtherTerminology() {
		VocabularyEntryDTO vocXXX = VocabularyEntryDTO.builder().code("XXX").description("XXX").build();
		VocabularyEntryDTO voc698 = VocabularyEntryDTO.builder().code("698.8").description("698.8").build();
		VocabularyEntryDTO voc3211 = VocabularyEntryDTO.builder().code("3211").description("3211").build();
		VocabularyEntryDTO vocMg = VocabularyEntryDTO.builder().code("mg{creat}").description("mg{creat}").build();
		VocabularyEntryDTO vocPRE = VocabularyEntryDTO.builder().code("PRE").description("PRE").build();
		VocabularyEntryDTO voc002 = VocabularyEntryDTO.builder().code("002").description("002").build();
		VocabularyEntryDTO vocQS = VocabularyEntryDTO.builder().code("qs").description("qs").build();
		VocabularyEntryDTO voc877 = VocabularyEntryDTO.builder().code("87.7").description("87.7").build();
		VocabularyEntryDTO voc7654 = VocabularyEntryDTO.builder().code("7654.321").description("7654.321").build();
		VocabularyEntryDTO voc012311B = VocabularyEntryDTO.builder().code("012311B").description("012311B").build();
		VocabularyEntryDTO vocQ13 = VocabularyEntryDTO.builder().code("Q13.1").description("Q13.1").build();
		VocabularyEntryDTO vocJ010103 = VocabularyEntryDTO.builder().code("J010103").description("J010103").build();
		VocabularyEntryDTO voc15 = VocabularyEntryDTO.builder().code("15").description("15").build();
		VocabularyEntryDTO voc02 = VocabularyEntryDTO.builder().code("02").description("02").build();
		VocabularyEntryDTO vocCODRISCHIO = VocabularyEntryDTO.builder().code("[COD_RISCHIO]").description("[COD_RISCHIO]").build();
		VocabularyEntryDTO vocMMG = VocabularyEntryDTO.builder().code("MMG").description("MMG").build();
		VocabularyEntryDTO vocIMP = VocabularyEntryDTO.builder().code("IMP").description("IMP").build();
		VocabularyEntryDTO vocUR = VocabularyEntryDTO.builder().code("UR").description("UR").build();
		VocabularyEntryDTO vocR = VocabularyEntryDTO.builder().code("R").description("R").build(); 
		VocabularyEntryDTO vocMTH = VocabularyEntryDTO.builder().code("MTH").description("MTH").build();
		VocabularyEntryDTO vocFTH = VocabularyEntryDTO.builder().code("FTH").description("FTH").build(); 
		VocabularyEntryDTO voc260152009 = VocabularyEntryDTO.builder().code("260152009").description("260152009").build();
		VocabularyEntryDTO voc111088007 = VocabularyEntryDTO.builder().code("111088007").description("111088007").build();
		VocabularyEntryDTO vocLA = VocabularyEntryDTO.builder().code("LA").description("LA").build();
		VocabularyEntryDTO vocRA = VocabularyEntryDTO.builder().code("RA").description("RA").build();
		VocabularyEntryDTO vocM = VocabularyEntryDTO.builder().code("M").description("M").build();
		VocabularyEntryDTO vocT2 = VocabularyEntryDTO.builder().code("46992007").description("46992007").build();
		VocabularyEntryDTO vocT1 = VocabularyEntryDTO.builder().code("385219001").description("385219001").build();
		VocabularyEntryDTO vocB = VocabularyEntryDTO.builder().code("B").description("B").build();
		VocabularyEntryDTO vocN = VocabularyEntryDTO.builder().code("N").description("N").build();
		VocabularyEntryDTO vocF = VocabularyEntryDTO.builder().code("F").description("F").build();
		VocabularyEntryDTO vocDiagnosi = VocabularyEntryDTO.builder().code("[COD_DIAGNOSI]").description("[COD_DIAGNOSI]").build();
		VocabularyEntryDTO voc402 = VocabularyEntryDTO.builder().code("0090334.02").description("0090334.02").build();

		Map<String, List<VocabularyEntryDTO>> mapVocabuary = new HashMap<>();
		mapVocabuary.put("2.16.840.1.113883.5.7", build57Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.2.9.5.1.88", build188Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.6.1", buildLoincList());
		mapVocabuary.put("2.16.840.1.113883.5.25", build525Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.5.112", build5112Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.6.103", build6103Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.6.73", build673Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.9.999", build9999Vocabulary(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.5.4", build54Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.5", build15Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.2.9.5.1.4", build14Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.4", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.11", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.10", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.13", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.12", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.5", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.6", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.1", Arrays.asList(vocXXX));
		mapVocabuary.put("2.16.840.1.113883.4.642.3.1426", Arrays.asList(vocQS));
		mapVocabuary.put("2.16.840.1.113883.6.99.99.99", Arrays.asList(voc7654));
		mapVocabuary.put("2.16.840.1.113883.2.9.2.120.4.4", Arrays.asList(voc002));
		mapVocabuary.put("2.16.840.1.113883.2.9.5.1.1.88", Arrays.asList(vocPRE));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.2.7", Arrays.asList(voc3211));
		mapVocabuary.put("2.16.840.1.113883.6.106", Arrays.asList(voc877));
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.4", Arrays.asList(voc698));
		mapVocabuary.put("2.16.840.1.113883.2.9.2.120.6.11", Arrays.asList(voc012311B));
		mapVocabuary.put("2.16.840.1.113883.6.8", Arrays.asList(vocMg));
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.17", Arrays.asList(vocQ13));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.48", Arrays.asList(vocJ010103));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.2", Arrays.asList(voc15));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.22", Arrays.asList(voc02));
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.56.2", Arrays.asList(vocCODRISCHIO));
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.13", Arrays.asList(vocMMG));
		mapVocabuary.put("2.16.840.1.113883.2.9.1.11.1.2.9", Arrays.asList(vocIMP));
		mapVocabuary.put("1.2.840.10008.2.16.4", build164Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.5.129", Arrays.asList(vocUR));
		mapVocabuary.put("2.16.840.1.113883.2.9.5.2.8", build28Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.4.642.3.395", build3395Vocabulary());
		mapVocabuary.put("2.16.840.1.113883.2.9.6.1.54.4", Arrays.asList(vocR,vocXXX));
		mapVocabuary.put("2.16.840.1.113883.5.111", Arrays.asList(vocMTH,vocFTH));
		mapVocabuary.put("2.16.840.1.113883.2.9.77.22.11.2", Arrays.asList(voc260152009,voc111088007));
		mapVocabuary.put("2.16.840.1.113883.5.1052", Arrays.asList(vocLA,vocRA));
		mapVocabuary.put("2.16.840.1.113883.5.1063", Arrays.asList(vocM,vocT2));
		mapVocabuary.put("2.16.840.1.113883.4.642.3.374", Arrays.asList(vocT1,vocT2));
		mapVocabuary.put("2.16.840.1.113883.5.83", Arrays.asList(vocB,vocN));
		mapVocabuary.put("2.16.840.1.113883.5.1", Arrays.asList(vocM,vocF));
		mapVocabuary.put("2.16.840.1.113883.2.9.2.30.6.11", Arrays.asList(vocDiagnosi,voc402));

		List<TerminologyETY> terminologyToSave = new ArrayList<>();
		for(Entry<String, List<VocabularyEntryDTO>> entry : mapVocabuary.entrySet()) {
			for(VocabularyEntryDTO terminologyEntry : entry.getValue()) {
				TerminologyETY terminology = new TerminologyETY();
				terminology.setSystem(entry.getKey());
				terminology.setCode(terminologyEntry.getCode());
				terminology.setDescription(terminologyEntry.getDescription());
				terminologyToSave.add(terminology);
			}
		}

		mongoTemplate.insertAll(terminologyToSave);
	}

	private void saveSchematronFiles() {
		List<SchematronETY> out = new ArrayList<>();
		Map<String,String> schematronTemplateMap = buildMapSchematronTemplate();
		try { 
			for(Entry<String, String> schematron : schematronTemplateMap.entrySet()) {
				String nameFile = schematron.getKey();
				byte[] content = FileUtility.getFileFromInternalResources("Files" + File.separator + "schematron" + File.separator + nameFile);
				SchematronETY ety = new SchematronETY();
				ety.setContentSchematron(new Binary(BsonBinarySubType.BINARY, content));
				ety.setLastUpdateDate(new Date());
				ety.setNameSchematron(nameFile);
				ety.setTemplateIdExtension("1.0");
				ety.setTemplateIdRoot(schematron.getValue());
				out.add(ety);
			}
			log.info("Schematron inseriti : " + out.size()); 
			mongoTemplate.insertAll(out);
		} catch(Exception ex) {
			log.error("Error while save schematron files : " + ex);
			throw new BusinessException("Error while save schematron files : " + ex);
		}
	}

	private Map<String,String> buildMapSchematronTemplate(){
		Map<String,String> mapSchematronRoot = new HashMap<>();
		try { 
			mapSchematronRoot.put("schematron_certificato_VACC v1.2.xslt" , "2.16.840.1.113883.2.9.10.1.11.1.2");
			mapSchematronRoot.put("schematron_PSS_v2.4.xslt" , "2.16.840.1.113883.2.9.10.1.4.1.1");
			mapSchematronRoot.put("schematron_RSA_v6.xslt" , "2.16.840.1.113883.2.9.10.1.9.1");
			mapSchematronRoot.put("schematron_singola_VACC v1.9.xslt" , "2.16.840.1.113883.2.9.10.1.11.1.1");
			mapSchematronRoot.put("schematron_VPS_v 2.4.xslt" , "2.16.840.1.113883.2.9.10.1.6.1");
			mapSchematronRoot.put("schematronFSE_LDO_V3.5.xslt" , "2.16.840.1.113883.2.9.10.1.5");
			mapSchematronRoot.put("schematronFSE_RAD_v2.5.xslt" , "2.16.840.1.113883.2.9.10.1.7.1");
			mapSchematronRoot.put("schematronFSEv15.xslt" , "2.16.840.1.113883.2.9.10.1.1");

		}catch(Exception ex) {
			log.error("Error while build map : " , ex);
			throw new BusinessException("Error while build map : " , ex);
		}
		return mapSchematronRoot;
	}

	@Override
	public void dropCollections() {
		mongoTemplate.dropCollection(SchematronETY.class);
		mongoTemplate.dropCollection(StructureDefinitionETY.class);
		mongoTemplate.dropCollection(StructureMapETY.class);
		mongoTemplate.dropCollection(ValuesetETY.class);
		vocabularyRepo.dropCollection();
		xslTransformRepo.dropCollection();
	}



	private void saveXslFiles() {
		List<XslTransformETY> out = new ArrayList<>();
		Map<String,String> xslTemplateMap = buildXslTrasformTemplate();
		try {
			for(Entry<String, String> xsl : xslTemplateMap.entrySet()) {
				String nameFile = xsl.getKey();
				byte[] content = FileUtility.getFileFromInternalResources("Files" + File.separator + "xslTransform" + File.separator + nameFile);
				XslTransformETY xslToSave = new XslTransformETY();
				xslToSave.setContentXslTransform(new Binary(BsonBinarySubType.BINARY, content));
				xslToSave.setNameXslTransform(nameFile);
				xslToSave.setTemplateIdExtension("1.0");
				xslToSave.setTemplateIdRoot(xsl.getValue());
				xslToSave.setLastUpdateDate(new Date());
				out.add(xslToSave);
			}

			log.info("Xsl inserite : " + out.size()); 
			mongoTemplate.insertAll(out);
		} catch(Exception ex) {
			log.error("Error while save xsl files : " + ex);
			throw new BusinessException("Error while save xsl files : " + ex);
		}
	}

	private Map<String,String> buildXslTrasformTemplate(){
		Map<String,String> mapXslRoot = new HashMap<>();
		try { 
			mapXslRoot.put("ref_med_lab.xsl" , "2.16.840.1.113883.2.9.10.1.1");

		}catch(Exception ex) {
			log.error("Error while build map xsl trasform : " , ex);
			throw new BusinessException("Error while build map xsl trasform : " , ex);
		}
		return mapXslRoot;
	}

	private void saveStructureDefinition() {
		List<StructureDefinitionETY> list = new ArrayList<>();
		try {
			for(String fileName : buildStructureDefinition()) {
				StructureDefinitionETY structureDef = new StructureDefinitionETY();
				structureDef.setContentFile(new Binary(FileUtility.getFileFromInternalResources("Files" + File.separator + "structureDefinition" + File.separator + fileName)));
				structureDef.setFileName(fileName);
				structureDef.setVersion("1.0");
				structureDef.setLastUpdateDate(new Date());
				list.add(structureDef);
			}
			log.info("Structure definition inserite : " + list.size());
			mongoTemplate.insertAll(list);
		} catch(Exception ex) {
			log.error("Error while save structure definition : " , ex);
			throw new BusinessException("Error while save structure definition : " , ex);
		}
	}

	private List<String> buildStructureDefinition(){
		List<String> out = new ArrayList<>();
		out.add("StructureDefinition-AssignedAuthor.json");
		out.add("StructureDefinition-AssignedCustodian.json");
		out.add("StructureDefinition-CD.json");
		out.add("StructureDefinition-PQ.json");
		out.add("StructureDefinition-Author.json");
		out.add("StructureDefinition-Custodian.json");
		out.add("StructureDefinition-BL.json");
		out.add("StructureDefinition-IVL-PQ.json");
		out.add("StructureDefinition-ADXP.json");
		out.add("StructureDefinition-IntendedRecipient.json");
		out.add("StructureDefinition-Birthplace.json");
		out.add("StructureDefinition-Order.json");
		out.add("StructureDefinition-AssociatedEntity.json");
		out.add("StructureDefinition-StructuredBody.json");
		out.add("StructureDefinition-DataEnterer.json");
		out.add("StructureDefinition-CE.json");
		out.add("StructureDefinition-Organization.json");
		out.add("StructureDefinition-SXPR-TS.json");
		out.add("StructureDefinition-Patient.json");
		out.add("StructureDefinition-ED.json");
		out.add("StructureDefinition-PlayingEntity.json");
		out.add("StructureDefinition-Section.json");
		out.add("StructureDefinition-PatientRole.json");
		out.add("StructureDefinition-INT.json");
		out.add("StructureDefinition-Observation.json");
		out.add("StructureDefinition-AD.json");
		out.add("StructureDefinition-Place.json");
		out.add("StructureDefinition-AssignedEntity.json");
		out.add("StructureDefinition-Component2.json");
		out.add("StructureDefinition-CS.json");
		out.add("StructureDefinition-ParticipantRole.json");
		out.add("StructureDefinition-InFulfillmentOf.json");
		out.add("StructureDefinition-ST.json");
		out.add("StructureDefinition-ObservationRange.json");
		out.add("StructureDefinition-RecordTarget.json");
		out.add("StructureDefinition-SubstanceAdministration.json");
		out.add("StructureDefinition-Person.json");
		out.add("StructureDefinition-Participant.json");
		out.add("StructureDefinition-IVL-TS.json");
		out.add("StructureDefinition-Act.json");
		out.add("StructureDefinition-DocumentationOf.json");
		out.add("StructureDefinition-ClinicalDocument.json");
		out.add("StructureDefinition-ServiceEvent.json");
		out.add("StructureDefinition-LegalAuthenticator.json");
		out.add("StructureDefinition-EN.json");
		out.add("StructureDefinition-CustodianOrganization.json");
		out.add("StructureDefinition-InformationRecipient.json");
		out.add("StructureDefinition-TS.json");
		out.add("StructureDefinition-EIVL-TS.json");
		out.add("StructureDefinition-II.json");
		out.add("StructureDefinition-TEL.json");
		out.add("StructureDefinition-ENXP.json");
		out.add("StructureDefinition-SpecimenRole.json");
		out.add("structuredefinition-tleft-snap.json");
		out.add("structuredefinition-tright-snap.json");
		return out;
	}

	private void saveStructureMap() {
		List<StructureMapETY> list = new ArrayList<>();
		try {
			for(String fileName : buildStructureMap()) {
				StructureMapETY structureMap = new StructureMapETY();
				structureMap.setContentStructureMap(new Binary(FileUtility.getFileFromInternalResources("Files" + File.separator + "structureMap" + File.separator + fileName)));
				structureMap.setLastUpdateDate(new Date());
				structureMap.setNameStructureMap(fileName.split("\\.")[0]);
				structureMap.setTemplateIdExtension("1.0");
				structureMap.setTemplateIdRoot("2.16.840.1.113883.2.9.2.30.10.8");
				list.add(structureMap);
			}
			log.info("Structure map inserite : " + list.size());
			mongoTemplate.insertAll(list);
		} catch(Exception ex) {
			log.error("Error while save structure definition : " , ex);
			throw new BusinessException("Error while save structure definition : " , ex);
		}
	}

	private List<String> buildStructureMap(){
		List<String> out = new ArrayList<>();
		out.add("CdaItRefertoMedicinaLaboratorio.map");
		out.add("CdaItToBundle.map");
		out.add("CdaToBundle.map");
		out.add("CDAtoFHIRTypes.map");
		out.add("step01.map");
		return out;
	}
	
	private void saveValuset() {
		List<ValuesetETY> list = new ArrayList<>();
		try {
			for(String fileName : buildValueset()) {
				ValuesetETY valueSet = new ValuesetETY();
				valueSet.setContentValueset(new Binary(FileUtility.getFileFromInternalResources("Files" + File.separator + "valueset" + File.separator + fileName)));
				int lastIndextOf = fileName.lastIndexOf(".");
				String nameValueset = fileName.substring(0, lastIndextOf);
				valueSet.setNameValuset(nameValueset);
				list.add(valueSet);
			}
			log.info("valueset inseriti : " + list.size());
			mongoTemplate.insertAll(list);
		} catch(Exception ex) {
			log.error("Error while save valueset : " , ex);
			throw new BusinessException("Error while save valueset : " , ex);
		}
	}

	private List<String> buildValueset(){
		List<String> out = new ArrayList<>();
		out.add("DocumentEntry.confidentialityCode.json");
		return out;
	}
}
