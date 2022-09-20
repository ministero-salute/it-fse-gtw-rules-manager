package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.VocabularyEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
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
	
//	@Autowired
//	private IDictionaryRepo dictionaryRepo;
	
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
					log.debug("Save new version vocabulary");
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
		log.debug("Vocabulary saved on db : " + recordSaved);
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
	
	
}
