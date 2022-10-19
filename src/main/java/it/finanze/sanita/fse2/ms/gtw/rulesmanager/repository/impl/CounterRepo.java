/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICounterRepo;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CounterRepo implements ICounterRepo {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Integer countCfgItems(final String collectionName) {
		Integer counter = 0;
		try {
			counter = (int)mongoTemplate.count(new Query(), collectionName);
		} catch(Exception ex) {
			log.error("Error while perform count cfg items query : ",ex);
			throw new BusinessException(ex);
		}
		
		return counter;
	}

}
