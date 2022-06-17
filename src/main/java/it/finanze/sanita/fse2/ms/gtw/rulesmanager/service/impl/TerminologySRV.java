package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IDictionaryRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ITerminologySRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Terminology service.
 */
@Service
@Slf4j
public class TerminologySRV implements ITerminologySRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 5518734661226532636L;

	@Autowired
	private ITerminologyRepo vocabularyRepo;
	
	@Autowired
	private IDictionaryRepo dictionaryRepo;
	
	@Override
	public TerminologyETY insert(final TerminologyETY ety) {
		TerminologyETY output = null;
		try {
			output = vocabularyRepo.insert(ety);
		} catch(Exception ex) {
			log.error("Error inserting ety vocabulary :" , ex);
			throw new BusinessException("Error inserting ety vocabulary :" , ex);
		}
		return output;
	}

	@Override
	public void insertAll(List<TerminologyETY> etys) {
		try {
			vocabularyRepo.insertAll(etys);
		} catch(Exception ex) {
			log.error("Error inserting all ety vocabulary :" , ex);
			throw new BusinessException("Error inserting all ety vocabulary :" , ex);
		}
	}


	@Override
	public Integer saveNewVocabularySystems(final List<VocabularyDTO> vocabulariesDTO) {
		Integer recordSaved = 0;
		if(vocabulariesDTO!=null && !vocabulariesDTO.isEmpty()) {
			for(VocabularyDTO entry : vocabulariesDTO) {
				boolean exist = vocabularyRepo.existsBySystem(entry.getSystem());
				
				List<TerminologyETY> vocabularyETYS = buildDtoToETY(entry.getEntryDTO(), entry.getSystem());
				if(Boolean.TRUE.equals(exist)) {
					List<String> codeList = vocabularyETYS.stream().map(e-> e.getCode()).collect(Collectors.toList());
					List<TerminologyETY> vocabularyFinded = vocabularyRepo.findByInCodeAndSystem(codeList,entry.getSystem());
					List<TerminologyETY> vocabularyToSave = minus(vocabularyETYS, vocabularyFinded);
					vocabularyRepo.insertAll(vocabularyToSave);
					recordSaved = recordSaved+vocabularyToSave.size();
				} else {
					vocabularyRepo.insertAll(vocabularyETYS);
					recordSaved = recordSaved+vocabularyETYS.size();
				}
			}
		}
		log.info("Vocabulary saved on db : " + recordSaved);
		return recordSaved;
	}
	
	
	private List<TerminologyETY> minus(List<TerminologyETY> base, List<TerminologyETY> toRemove) {
		List<TerminologyETY> out = new ArrayList<>(); 
		for (TerminologyETY s:base) {
			if (!toRemove.contains(s) && s!=null) {
				out.add(s);
			} 
		}
		return out;    
	}
	
	private List<TerminologyETY> buildDtoToETY(List<VocabularyEntryDTO> vocabularyEntriesDTO, String system) {
		List<TerminologyETY> output = new ArrayList<>();
		for(VocabularyEntryDTO vocabularyEntryDTO : vocabularyEntriesDTO) {
			TerminologyETY ety = new TerminologyETY();
			ety.setId(null);
			ety.setCode(vocabularyEntryDTO.getCode());
			ety.setDescription(vocabularyEntryDTO.getDescription());
			ety.setSystem(system);
			output.add(ety);
		}
		return output;
	}
	
	@Override
	public List<DictionaryETY> saveDictionaryFiles() {
		List<DictionaryETY> out = new ArrayList<>();
		
		try {
			
			List<String> dictionaries = getListDictionary();
				for (String namefile : dictionaries) {
					byte[] content = FileUtility.getFileFromInternalResources("Files" + File.separator + "dictionary" + File.separator + namefile);
					DictionaryETY dic = new DictionaryETY();
					dic.setContentFile(new Binary(BsonBinarySubType.BINARY, content));
					dic.setFileName(namefile);
					out.add(dic);
				}
				dictionaryRepo.insertAll(out);
			
			
		} catch(Exception ex) {
			log.error("Error while save dictionary file : " + ex);
			throw new BusinessException("Error while save dictionary file : " + ex);
		}
		return out;
	}
	
	private List<String> getListDictionary(){
		List<String> list = new ArrayList<>();
		list.add("2.16.840.1.113883.2.9.1.11.1.2.9.xml");
		list.add("2.16.840.1.113883.2.9.6.1.5.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.11.xml");
		list.add("2.16.840.1.113883.5.1052.xml");
		list.add("2.16.840.1.113883.5.112.xml");
		list.add("2.16.840.1.113883.2.9.5.2.8.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.4.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.17.xml");
		list.add("2.16.840.1.113883.11.22.12.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.15.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.10.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.6.xml");
		list.add("2.16.840.1.113883.6.73.xml");
		list.add("2.16.840.1.113883.5.111.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.9.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.18.xml");
		list.add("2.16.840.1.113883.5.1.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.14.xml");
		list.add("2.16.840.1.113883.11.22.15.xml");
		list.add("2.16.840.1.113883.1.11.12839.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.7.xml");
		list.add("2.16.840.1.113883.6.1.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.13.xml");
		list.add("2.16.840.1.113883.11.22.36.xml");
		list.add("2.16.840.1.113883.11.22.9.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.2.xml");
		list.add("2.16.840.1.113883.2.9.6.1.51.xml");
		list.add("2.16.840.1.113883.5.4.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.8.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.3.xml");
		list.add("2.16.840.1.113883.1.11.19700.xml");
		list.add("2.16.840.1.113883.11.22.61.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.12.xml");
		list.add("2.16.840.1.113883.11.22.17.xml");
		list.add("2.16.840.1.113883.2.9.77.22.11.16.xml");
		list.add("2.16.840.1.113883.1.11.1.xml");
		return list;
	}
}
