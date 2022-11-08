/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TerminologyRepo implements ITerminologyRepo {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<TerminologyMapDTO> getAllCodeSystemVersions() {
		try {					
			
			GroupOperation group = Aggregation
					.group("system", "version")
					.first("code").as("code")
					.first("last_update_date").as("creationDate")
					.first("release_date").as("releaseDate");
			
			AggregationOperation project = project(
					"_id.system", 
					"_id.version", 
					"code",
					"creationDate",
					"releaseDate"
					);
			
			Aggregation agg = Aggregation.newAggregation(group, project);

			return mongoTemplate
					.aggregate(agg, "terminology", TerminologyMapDTO.class) 
					.getMappedResults();
		} catch(Exception ex) {
			log.error("Error while perform count cfg items query : ",ex);
			throw new BusinessException(ex);
		}
	}

}
