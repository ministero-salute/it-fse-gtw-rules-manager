package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IVocabularyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.VocabularyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IVocabularySRV;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Vocabulary service.
 */
@Service
@Slf4j
public class VocabularySRV implements IVocabularySRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 5518734661226532636L;

	@Autowired
	private IVocabularyRepo vocabularyRepo;
	
	@Override
	public VocabularyETY insert(final VocabularyETY ety) {
		VocabularyETY output = null;
		try {
			output = vocabularyRepo.insert(ety);
		} catch(Exception ex) {
			log.error("Error inserting ety vocabulary :" , ex);
			throw new BusinessException("Error inserting ety vocabulary :" , ex);
		}
		return output;
	}

	@Override
	public void insertAll(List<VocabularyETY> etys) {
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
				
				List<VocabularyETY> vocabularyETYS = buildDtoToETY(entry.getEntryDTO(), entry.getSystem());
				if(Boolean.TRUE.equals(exist)) {
					List<String> codeList = vocabularyETYS.stream().map(e-> e.getCode()).collect(Collectors.toList());
					List<VocabularyETY> vocabularyFinded = vocabularyRepo.findByInCodeAndSystem(codeList,entry.getSystem());
					List<VocabularyETY> vocabularyToSave = minus(vocabularyETYS, vocabularyFinded);
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
	
	
	private List<VocabularyETY> minus(List<VocabularyETY> base, List<VocabularyETY> toRemove) {
		List<VocabularyETY> out = new ArrayList<>(); 
		for (VocabularyETY s:base) {
			if (!toRemove.contains(s) && s!=null) {
				out.add(s);
			} 
		}
		return out;    
	}
	
	private List<VocabularyETY> buildDtoToETY(List<VocabularyEntryDTO> vocabularyEntriesDTO, String system) {
		List<VocabularyETY> output = new ArrayList<>();
		for(VocabularyEntryDTO vocabularyEntryDTO : vocabularyEntriesDTO) {
			VocabularyETY ety = new VocabularyETY();
			ety.setId(null);
			ety.setCode(vocabularyEntryDTO.getCode());
			ety.setDescription(vocabularyEntryDTO.getDescription());
			ety.setSystem(system);
			output.add(ety);
		}
		return output;
	}
}
