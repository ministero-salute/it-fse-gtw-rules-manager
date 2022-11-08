/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IDictionaryRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DictionaryRepo implements IDictionaryRepo {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void renewCodeSystemVersions(List<DictionaryETY> codeSystemVersions) {
		try {
			mongoTemplate.dropCollection(DictionaryETY.class);
			mongoTemplate.insertAll(codeSystemVersions);
		} catch(Exception ex) {
			log.error("Error while saving codeSystemVersions : ",ex);
			throw new BusinessException(ex);
		}
	}
	
}
