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
			File directory = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "Files" + File.separator + "dictionary");
			
			//only first level files.
			String[] actualFiles = directory.list();
			
			if (actualFiles!=null && actualFiles.length>0) {
				for (String namefile : actualFiles) {
					File file = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "Files" + File.separator + "dictionary" + File.separator + namefile);
					byte[] content = Files.readAllBytes(file.toPath());
					DictionaryETY dic = new DictionaryETY();
					dic.setContentFile(new Binary(BsonBinarySubType.BINARY, content));
					dic.setFileName(namefile);
					out.add(dic);
				}
				dictionaryRepo.insertAll(out);
			}
			
			
		} catch(Exception ex) {
			log.error("Error while save dictionary file : " + ex);
			throw new BusinessException("Error while save dictionary file : " + ex);
		}
		return out;
	}
}
