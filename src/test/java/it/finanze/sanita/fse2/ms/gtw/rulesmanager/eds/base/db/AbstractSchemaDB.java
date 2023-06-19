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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.EDSTestUtils.compareDeeply;

import org.springframework.data.mongodb.core.MongoTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;

public abstract class AbstractSchemaDB<T> {
	
	public static String TEST_BASE_COLLECTION = "eds-test-x";

    private final MongoTemplate mongo;
    private final AbstractEntityHandler<T> hnd;
    private final ChangesetCFG config;

    public AbstractSchemaDB(MongoTemplate mongo, AbstractEntityHandler<T> hnd, ChangesetCFG config) {
        this.mongo = mongo;
        this.hnd = hnd;
        this.config = config;
    }
    
    public void setupTest() throws Exception {
    	setupRepository(TEST_BASE_COLLECTION);
    }
    
    public void setupProduction() throws Exception {
    	setupRepository(config.getProduction());
    }
    
    public void setupStaging() throws Exception {
    	setupRepository(config.getStaging());
    }

    private void setupRepository(String name) throws Exception {
        // Verify if previous test database exists
        if (isSchemaAvailable(name)) {
            // Drop it, we are going to create a new one
            mongo.dropCollection(name);
        }
        // Make test collection
        createEmptySchema(name);
        // Init entities
        hnd.initTestEntities();
        // Add to collection
        addEntityToSchema(name);
    }  
    
    public void clearTest() {
    	clearRepository(TEST_BASE_COLLECTION);
    }
    
    public void clearProduction() {
    	clearRepository(config.getProduction());
    }
    
    public void clearStaging() {
    	clearRepository(config.getStaging());
    }

    private void clearRepository(String name) {
        mongo.dropCollection(name);
        hnd.clearTestEntities();
    }

    private void createEmptySchema(String name) {
        mongo.createCollection(name);
    }

    private void addEntityToSchema(String name) {
        mongo.insert(hnd.getEntities(), name);
    }
    
    public boolean isProductionAvailable() {
    	return isSchemaAvailable(config.getProduction());
    }
    
    public boolean isStagingAvailable() {
    	return isSchemaAvailable(config.getStaging());
    }

    private boolean isSchemaAvailable(String name) {
        return mongo.getCollectionNames().contains(name);
    }

    public AbstractEntityHandler<T> handler() {
        return hnd;
    }
    
    public boolean verifyIntegrityProduction() {
    	return verifyIntegrity(config.getProduction());
    }
    
    public boolean verifyIntegrityStaging() {
    	return verifyIntegrity(config.getStaging());
    }

    private boolean verifyIntegrity(String name) {
        return compareDeeply(hnd.getDocuments(), mongo.getCollection(name));
    }
}
