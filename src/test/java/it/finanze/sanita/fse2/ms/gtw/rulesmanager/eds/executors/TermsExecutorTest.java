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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.executors;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSTermsDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.impl.MockTermsExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.impl.MockTermsExecutor.createChangesetChunk;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.impl.MockTermsExecutor.emptyChangesetChunk;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
class TermsExecutorTest {

    @SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClient client;
    @Autowired
    private MockTermsExecutor executor;
    @Autowired
    private EDSTermsDB db;
    @SpyBean
    private IExecutorRepo repository;

    @BeforeAll
    public void init() { resetDB(); }

    @AfterEach
    public void reset() { resetDB(); }

    @Test
    void changeset() throws EdsClientException {
        // Setup production
        setupProduction();
        // Provide knowledge
        when(client.getSnapshot(any(), any(), any())).thenReturn(emptyChangesetChunk());
        // Changeset should be retrieved correctly
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getSnapshot(any(), any(), any())).thenReturn(createChangesetChunk(10, 1, 10));
        // Changeset is not empty, it should be OK
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        when(client.getSnapshot(any(), any(), any())).thenReturn(createChangesetChunk(0,0,0));
        assertEquals(OK, executor.onChangeset(executor.onLastUpdateProd()));
        // Provide knowledge
        when(client.getSnapshot(any(), any(), any())).thenThrow(
            new EdsClientException("Test error", new IOException("Test error"))
        );
        // Get status errored, it should be KO
        assertEquals(KO, executor.onChangeset(executor.onLastUpdateProd()));
    }
    
    @Test
    void changesetChunkEmpty() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock to trigger empty changeset check
        executor.setSnapshot(createChangesetChunk(0, 0, size));
        // Setup production
        setupProduction();
        // Verify size
        assertEquals(EXIT, executor.onChangesetEmpty());
    }
    
    @Test
    void changesetNotEmpty() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock to trigger empty changeset check
        executor.setSnapshot(createChangesetChunk(10, 0, size));
        // Verify size
        assertEquals(OK, executor.onChangesetEmpty());
    }
    
    @Test
    void processing() {
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setSnapshot(createChangesetChunk(10, 1, 9));
        // Call processing
        assertEquals(OK, executor.onProcessing());
        // Now check size
        assertEquals(10, executor.getSnapshot().getChunks().getInsertions().getChunksCount());
        assertEquals(1, executor.getSnapshot().getChunks().getDeletions().getChunksCount());
        assertEquals(11, executor.getSnapshot().getTotalNumberOfElements());
    }

    @Test
    void processingEmptyChangeset() {
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setSnapshot(emptyChangesetChunk());
        // Call processing
        assertEquals(OK, executor.onProcessing());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    void verify() {
        // Define expected size
        final long size = 9;
        // Provide mock-knowledge (for staging that doesn't exist)
        assertDoesNotThrow(() -> {
            doReturn(size).when(repository).countActiveDocuments(nullable(MongoCollection.class));
        });
        // Setup production
        setupProduction();
        // Setup changeset
        executor.setSnapshot(createChangesetChunk(10,1, size));
        // Call processing with verified flag
        assertEquals(OK, executor.onProcessing(true));
        assertEquals(OK, executor.onVerify());
        // Call processing with unverified flag
        assertEquals(OK, executor.onProcessing(false));
        assertEquals(KO, executor.onVerify());
    }
    
    @Test
    void sync() throws Exception {
        // Setup production
        setupProduction();
        // Setup repository with document
        db.setupStaging();
        // Create
        ChangeSetChunkDTO chunkDTO = createChangesetChunk(10, 1, 9);
        // Setup changeset
        executor.setSnapshot(chunkDTO);
        // Call it
        assertEquals(OK, executor.onSync());
        // Retrieve
        Date lastSync = repository.getLastSync(executor.getConfig().getStaging());
        // Verify it matches
        assertEquals(lastSync, chunkDTO.getTimestamp());
    }
    
    @Test
    void onChangesetAlignment() {
        // Setup production
        setupProduction();
        // Create empty snapshot
        ChangeSetChunkDTO chunkDTO = emptyChangesetChunk();
        // Set
        executor.setSnapshot(chunkDTO);
        // Call it
        assertEquals(OK, executor.onChangesetAlignment());
        // Create snapshot
        chunkDTO = createChangesetChunk(10,3, 7);
        // Set
        executor.setSnapshot(chunkDTO);
        // Call it
        assertEquals(KO, executor.onChangesetAlignment());
    }
    
    @Test
    void onReset() {
    	assertEquals(OK, executor.onReset());
    }
    
    @Test
    void size() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock due to stats printing on log
        executor.setSnapshot(createChangesetChunk(size, 0, size));
        // Setup production
        setupProduction();
        // Verify size
        assertEquals(EXIT, executor.onVerifyProductionSize());
    }

    @Test
    void sizeMismatch() throws Exception {
        // Get loaded entities
        db.handler().initTestEntities();
        int size = db.handler().getEntities().size();
        // Mock due to stats printing on log
        executor.setSnapshot(createChangesetChunk(size, 0, size + 2));
        // Setup production
        setupProduction();
        // Verify size
        assertEquals(KO, executor.onVerifyProductionSize());
    }
    
    private void setupProduction() {
        assertDoesNotThrow(() -> db.setupProduction());
    }

    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        try {
        	assertFalse(repository.exists(executor.getConfig().getStaging()));
        	assertFalse(repository.exists(executor.getConfig().getProduction()));
        } catch(Exception ex) {
        	throw new BusinessException(ex);
        }
//        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
//        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

}
