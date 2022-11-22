/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
@Slf4j
public class TerminologyRepo implements ITerminologyRepo {
	
	@Autowired
	private MongoTemplate mongo;
	
	@Override
	public List<TerminologyMapDTO> getAllCodeSystemVersions(String collection) throws EdsDbException {

		List<TerminologyMapDTO> res;
		
		try {
			// Define group operation
			GroupOperation group = Aggregation
				.group(
					TerminologyETY.FIELD_SYSTEM, 
					TerminologyETY.FIELD_VERSION
				)
				.first(TerminologyETY.FIELD_CODE).as(TerminologyMapDTO.FIELD_CODE)
				.first(TerminologyETY.FIELD_LAST_UPDATE).as(TerminologyMapDTO.FIELD_CREATION_DATE)
				.first(TerminologyETY.FIELD_RELEASE_DATE).as(TerminologyMapDTO.FIELD_RELEASE_DATE);
			// Init aggregation pipeline
			AggregationOperation project = project(
				TerminologyETY.FIELD_SYSTEM_ID_REF, 
				TerminologyETY.FIELD_VERSION_ID_REF,
				TerminologyMapDTO.FIELD_CODE,
				TerminologyMapDTO.FIELD_CREATION_DATE,
				TerminologyMapDTO.FIELD_RELEASE_DATE
			);
			// Create aggregation definition
			Aggregation agg = Aggregation.newAggregation(group, project);
			// Execute
			res = mongo.aggregate(agg, collection, TerminologyMapDTO.class).getMappedResults();
		} catch(MongoException ex) {
			throw new EdsDbException("Unable to aggregate code-system versions", ex);
		}
		
		return res;
	}

}
