/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.*;
import static java.lang.Integer.parseInt;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
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
					FIELD_SYSTEM,
					FIELD_VERSION,
					FIELD_RELEASE_DATE,
					FIELD_WHITELIST,
					FIELD_DELETED
				)
				.first(FIELD_CODE).as(TerminologyMapDTO.FIELD_CODE)
				.first(FIELD_RELEASE_DATE).as(TerminologyMapDTO.FIELD_RELEASE_DATE)
				.first(FIELD_DELETED).as(TerminologyMapDTO.FIELD_DELETED)
				.first(FIELD_WHITELIST).as(TerminologyMapDTO.FIELD_WHITELIST);
			// Init aggregation pipeline
			AggregationOperation project = project(
				FIELD_SYSTEM_ID_REF,
				FIELD_VERSION_ID_REF,
				FIELD_DELETED,
				FIELD_RELEASE_DATE,
				FIELD_WHITELIST,
				TerminologyMapDTO.FIELD_CODE,
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

	@Override
	public void applyIndexes(String collection) throws EdsDbException {
		// Retrieve index op instance
		IndexOperations op = mongo.indexOps(collection);
		// Build index definition
		Index idx = new Index()
			.on(FIELD_SYSTEM, ASC)
			.on(FIELD_CODE, ASC)
			.on(FIELD_DELETED, ASC)
			.on(FIELD_VERSION, ASC)
			.background();
		// Execute
		try {
			op.ensureIndex(idx);
		}catch (MongoException e) {
			throw new EdsDbException("Unable to apply indexes on terminology", e);
		}
	}

	@Override
	public boolean exists(String resource, String version, String collection) throws EdsDbException {
		boolean exist;
		Query q = new Query(
			where(FIELD_REF_ID).is(resource)
			.and(FIELD_REF_VERSION).is(parseInt(version))
			.and(FIELD_DELETED).ne(true)
		);
		try {
			exist = mongo.exists(q, collection);
		}catch (MongoException e) {
			throw new EdsDbException(String.format("Unable to check if resource exists on terminology for %s %s", resource, version), e);
		}
		return exist;
	}

	@Override
	public long countActiveResources(String collection) throws EdsDbException {
		long size;
		MongoCollection<Document> c = mongo.getCollection(collection);
		// Prepare query
		Query q = new Query(where(FIELD_DELETED).ne(true));
		// Execute
		try {
			size = c.countDocuments(q.getQueryObject());
		}catch (MongoException e) {
			throw new EdsDbException("Unable to count active resources on terminology", e);
		}
		return size;
	}

}
