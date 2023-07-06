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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.DictionaryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.*;
import static java.lang.Integer.parseInt;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class TerminologyRepo implements ITerminologyRepo {

	@Autowired
	private MongoTemplate mongo;

	@Override
	public List<DictionaryDTO> createDictionaries(String collection) throws EdsDbException {

		List<DictionaryDTO> resources;

		// Sort the documents by their resource version
		// So we can acquire the first (latest) document of a given system+version
		SortOperation sort = sort(
			Sort.by(DESC, FIELD_REF_VERSION)
		);
		// Retrieve each system+version in the database
		GroupOperation group = group(
			FIELD_SYSTEM,
			FIELD_VERSION
		)
		// For each system+version, retrieve the value of the first document for each field
		.first(FIELD_RELEASE_DATE).as(FIELD_RELEASE_DATE)
		.first(FIELD_WHITELIST).as(FIELD_WHITELIST)
		.first(FIELD_DELETED).as(FIELD_DELETED)
		.first(FIELD_REF_VERSION).as(DictionaryDTO.FIELD_SOURCE);
		// Now project to compose the final document
		ProjectionOperation projection = project()
			.and(FIELD_SYSTEM_ID_REF).as(FIELD_SYSTEM)
			.and(FIELD_VERSION_ID_REF).as(FIELD_VERSION)
			.andInclude(
				FIELD_RELEASE_DATE,
				FIELD_WHITELIST,
				FIELD_DELETED,
				DictionaryDTO.FIELD_SOURCE
			);

		try {
			// Create aggregation definition
			Aggregation agg = newAggregation(sort, group, projection);
			// Execute
			resources = mongo.aggregate(agg, collection, DictionaryDTO.class).getMappedResults();
		} catch(MongoException ex) {
			throw new EdsDbException("Unable to aggregate code-system versions", ex);
		}

		return resources;
	}

	@Override
	public List<DictionaryETY> getDictionaries() throws EdsDbException {
		List<DictionaryETY> dictionaries;
		try {
			dictionaries = mongo.findAll(DictionaryETY.class);
		}catch (MongoException ex) {
			throw new EdsDbException("Unable to retrieve all available dictionaries", ex);
		}
		return dictionaries;
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

	@Override
	public long countActiveResources(String resource, String version, String collection) throws EdsDbException {
		long size;
		Query q = new Query(
			where(FIELD_REF_ID).is(resource)
			.and(FIELD_REF_VERSION).is(parseInt(version))
			.and(FIELD_DELETED).ne(true)
		);
		try {
			size = mongo.count(q, collection);
		}catch (MongoException e) {
			throw new EdsDbException(String.format("Unable to count resource items on terminology for %s %s", resource, version), e);
		}
		return size;
	}

}
