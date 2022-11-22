/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IDictionaryRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICodeSystemVersionSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CodeSystemVersionSRV implements ICodeSystemVersionSRV {

	@Autowired
	private IDictionaryRepo dictionaryRepo;
	
	@Autowired
	private ITerminologyRepo terminologyRepo;
	
	@Override
	public void syncCodeSystemVersions() {
		try {
			List<DictionaryETY> codeSystemVersions = getCodeSystemVersions();
			dictionaryRepo.renewCodeSystemVersions(codeSystemVersions);
		} catch(Exception ex) {
			log.error("Error while sync the CodeSystemVersion collection" , ex);
			throw new BusinessException("Error while sync the CodeSystemVersion collection" , ex);
		}
	}

	private List<DictionaryETY> getCodeSystemVersions() {
		return terminologyRepo
				.getAllCodeSystemVersions()
				.stream()
				.filter(Objects::nonNull)
				.map(DictionaryETY::fromMap)
				.collect(Collectors.toList());
	}
	
}
