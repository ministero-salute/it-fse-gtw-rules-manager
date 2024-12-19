
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.ExecutorRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
class TimeCollectionTest {
	
	static final String START_NAME = "test_schema";
	static final String END_NAME = "CollectionProduction";
	static long start;
	static Query query = new Query(Criteria.where("name_schema").is("test"));
	
	@Autowired
	ExecutorRepo collectionsRepo;

	@Autowired
	MongoTemplate template;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@AfterEach
	void afterEach() throws EdsDbException {
		template.getCollection(START_NAME).drop();
		collectionsRepo.drop(END_NAME);
	}
	
	@ParameterizedTest
    @ValueSource(ints = {10, 1000, 1000000})
    @DisplayName("Insert N items in collection and time counting")
    void insertNitems(int numRecords) throws EdsDbException {
		
        MongoCollection<Document> collection = mongoTemplate.getCollection(START_NAME);
		
		SchemaETY entity = new SchemaETY();
		entity.setNameSchema("test");
		entity.setTypeIdExtension("test");
		
		List<SchemaETY> entities = Collections.nCopies(numRecords, entity);
		mongoTemplate.insertAll(entities);

        start();
		collectionsRepo.rename(collection, END_NAME);
		end(numRecords);
		
		assertEquals(0, mongoTemplate.find(query, SchemaETY.class, START_NAME).size()); //in schema (prima del rename)
		assertEquals(numRecords, mongoTemplate.find(query, SchemaETY.class, END_NAME).size()); //in collection (rinominato)
	}
	
	private static void start() {
        start = System.currentTimeMillis();
    }

    private static void end(int numRecords) {
        log.info("Test for " + numRecords + ". Time in ms: " + (System.currentTimeMillis() - start));
    }
}
