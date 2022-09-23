package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.structures;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.structures.base.EDSStructureHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.ComponentScan.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

@DataMongoTest
@ComponentScans( value = {
    @ComponentScan(CONFIG_MONGO),
    @ComponentScan(REPOSITORY),
    @ComponentScan(SCHEDULER_QUERIES),
    @ComponentScan(UTILITY)
})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EDSRepoStructuresTest extends EDSStructureHandler {

    @SpyBean
    private MongoTemplate mongo;
    @Autowired
    private IStructureRepo repository;

    @BeforeEach
    void setup() {
        mongo.dropCollection(STRUCT_TEST_COLL);
        mongo.dropCollection(STRUCT_TEST_COLL_B);
    }

    @AfterAll
    void quit() {
        mongo.dropCollection(STRUCT_TEST_COLL);
        mongo.dropCollection(STRUCT_TEST_COLL_B);
    }

    @Test
    void insertInto() {
        // Case #1 - Create collection and insert document
        assertDoesNotThrow(() -> repository.insertInto(
            STRUCT_TEST_COLL, createTestDocument())
        );
        // Verify document has been inserted
        assertEquals(mongo.getCollection(STRUCT_TEST_COLL).countDocuments(), 1);
        // Drop test collection
        mongo.dropCollection(STRUCT_TEST_COLL);
        // Case #2 - Create collection, insert document but fails
        doThrow(new MongoException("test")).when(mongo).getCollection(anyString());
        // Verify exception re-thrown
        assertThrows(EdsDbException.class, () -> repository.insertInto(
            STRUCT_TEST_COLL, createTestDocument()
        ));
        // Drop test collection
        mongo.dropCollection(STRUCT_TEST_COLL);
    }

    @Test
    void readFromStagingDoc() {
        // Case #1 - Staging does not exist
        assertThrows(EdsDbException.class, () -> repository.readFromStagingDoc(
            STRUCT_TEST_COLL_B
        ));
        // Case #2 - Staging exists but no documents available
        // Create collection
        mongo.createCollection(STRUCT_TEST_COLL_B);
        // Retrieve empty array list
        assertDoesNotThrow(() -> {
            // No document returned
            List<Document> docs = repository.readFromStagingDoc(STRUCT_TEST_COLL_B);
            // Assert it
            assertTrue(docs.isEmpty());
        });
        // Case #3 - Staging exists and one document inserted
        mongo.getCollection(STRUCT_TEST_COLL_B).insertOne(createTestDocument());
        // Retrieve array list with one document
        assertDoesNotThrow(() -> {
            // No document returned
            List<Document> docs = repository.readFromStagingDoc(STRUCT_TEST_COLL_B);
            // Assert it
            assertEquals(1, docs.size());
        });
    }

    @Test
    void readFromLatestDoc() {
        // Case #1 - No document available
        assertDoesNotThrow(() -> {
            // Check for emptiness
            List<Document> doc = repository.readFromLatestDoc(STRUCT_TEST_COLL, FIELD_MAP);
            // Assert
            assertTrue(doc.isEmpty());
        });
        // Case #2 - One document available but no such field exists
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(createTestParentDocument());
        assertThrows(EdsDbException.class, () -> repository.readFromLatestDoc(STRUCT_TEST_COLL, FIELD_MAP));
        // Case #3 - One document available and empty field
        mongo.dropCollection(STRUCT_TEST_COLL);
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument(createTestParentFields())
        );
        assertDoesNotThrow(() -> {
            // Check for emptiness
            List<Document> doc = repository.readFromLatestDoc(STRUCT_TEST_COLL, FIELD_MAP);
            // Assert
            assertTrue(doc.isEmpty());
        });
        // Case #4 - One document available and not-empty array
        mongo.dropCollection(STRUCT_TEST_COLL);
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument(
                createTestParentFields(createTestDocument())
            )
        );
        assertDoesNotThrow(() -> {
            // Check for emptiness
            List<Document> doc = repository.readFromLatestDoc(STRUCT_TEST_COLL, FIELD_MAP);
            // Assert
            assertEquals(1, doc.size());
        });
    }

    @Test
    void cloneFromParent() {
        // Case #1 - No source exists
        assertThrows(EdsDbException.class, () -> repository.cloneFromParent(
            STRUCT_TEST_COLL,
            STRUCT_TEST_COLL_B,
            FIELD_MAP
        ));
        // Case #2 - Dest already exists
        mongo.createCollection(STRUCT_TEST_COLL_B);
        // Invoke
        assertThrows(EdsDbException.class, () -> repository.cloneFromParent(
            STRUCT_TEST_COLL,
            STRUCT_TEST_COLL_B,
            FIELD_MAP
        ));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL_B);
        // Case #3 - Source exists but no documents inside
        mongo.createCollection(STRUCT_TEST_COLL);
        assertDoesNotThrow(() -> {
            // Get new collection
            MongoCollection<Document> docs = repository.cloneFromParent(
                STRUCT_TEST_COLL,
                STRUCT_TEST_COLL_B,
                FIELD_MAP
            );
            // Check size
            assertEquals(0, docs.countDocuments());
        });
        mongo.dropCollection(STRUCT_TEST_COLL_B);
        // Case #4 - Source exists with one document inside but no field available
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument()
        );
        assertThrows(EdsDbException.class, () -> repository.cloneFromParent(
            STRUCT_TEST_COLL,
            STRUCT_TEST_COLL_B,
            FIELD_MAP
        ));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL);
        mongo.dropCollection(STRUCT_TEST_COLL_B);
        // Case #5 - Source exists with one document inside and field available
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument(
                createTestParentFields(createTestDocument())
            )
        );
        assertDoesNotThrow(() -> {
            // Get new collection
            MongoCollection<Document> docs = repository.cloneFromParent(
                STRUCT_TEST_COLL,
                STRUCT_TEST_COLL_B,
                FIELD_MAP
            );
            // Check size
            assertEquals(1, docs.countDocuments());
        });
    }

    @Test
    void getLastSyncFromParent() {
        // Case #1 - Parent doesn't exists (no docs)
        assertDoesNotThrow(() -> assertNull(
            repository.getLastSyncFromParent(STRUCT_TEST_COLL, FIELD_MAP)
        ));
        // Case #2 - Parent exists (no docs)
        mongo.createCollection(STRUCT_TEST_COLL);
        assertDoesNotThrow(() -> assertNull(
            repository.getLastSyncFromParent(STRUCT_TEST_COLL, FIELD_MAP)
        ));
        // Case #3 - Parent exists with one document but no field
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument()
        );
        assertDoesNotThrow(() -> assertNull(
            repository.getLastSyncFromParent(STRUCT_TEST_COLL, FIELD_MAP)
        ));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL);
        // Case #4 - Parent with one document but empty field
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument(createTestParentFields())
        );
        assertDoesNotThrow(() -> assertNull(
            repository.getLastSyncFromParent(STRUCT_TEST_COLL, FIELD_MAP)
        ));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL);
        // Case #5 - Parent with one document with one item but no field
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument(createTestParentFields(
                createTestDocument()
            ))
        );
        assertDoesNotThrow(() -> assertNull(
            repository.getLastSyncFromParent(STRUCT_TEST_COLL, FIELD_MAP)
        ));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL);
        // Case #6 - Parent with one document with one item and field
        Document item = createTestItemDocument();
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestParentDocument(createTestParentFields(
                item
            ))
        );
        assertDoesNotThrow(() -> assertEquals(
            item.get(FIELD_LAST_SYNC, Date.class),
            repository.getLastSyncFromParent(STRUCT_TEST_COLL, FIELD_MAP)
        ));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL);
    }

    @Test
    void createEmptyDocOnParent() {
        // Create document
        assertDoesNotThrow(() -> repository.createEmptyDocOnParent(
            STRUCT_TEST_COLL,
            FIELD_MAP,
            FIELD_DEFINITION,
            FIELD_VALUESET)
        );
        // Retrieve document
        Document doc = mongo.getCollection(STRUCT_TEST_COLL).find().first();
        // Field check
        assertNotNull(doc);
        assertTrue(doc.containsKey(FIELD_LAST_UPDATE));
        assertTrue(doc.containsKey(FIELD_MAP));
        assertTrue(doc.containsKey(FIELD_DEFINITION));
        assertTrue(doc.containsKey(FIELD_VALUESET));
        assertTrue(doc.getList(FIELD_MAP, Document.class).isEmpty());
        assertTrue(doc.getList(FIELD_DEFINITION, Document.class).isEmpty());
        assertTrue(doc.getList(FIELD_VALUESET, Document.class).isEmpty());
    }

    @Test
    void updateDueToEmptyOnParent() {
        // Case #1 - No parent document to update
        assertThrows(EdsDbException.class, () -> repository.updateDueToEmptyOnParent(STRUCT_TEST_COLL));
        // Case #2 - Parent document with missing field
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            createTestDocument()
        );
        assertThrows(EdsDbException.class, () -> repository.updateDueToEmptyOnParent(STRUCT_TEST_COLL));
        // Clean
        mongo.dropCollection(STRUCT_TEST_COLL);
        // Case #3 - Parent document with field
        Document doc = createTestParentDocument();
        mongo.getCollection(STRUCT_TEST_COLL).insertOne(
            doc
        );
        assertDoesNotThrow(() -> {
            // Execute update
            repository.updateDueToEmptyOnParent(STRUCT_TEST_COLL);
            // Retrieve new one
            Document parent = mongo.getCollection(STRUCT_TEST_COLL).find().first();
            // Assert nullity
            assertNotNull(parent);
            // Assert no equality (last update was updated)
            assertNotEquals(parent.get(FIELD_LAST_UPDATE, Date.class), doc.get(FIELD_LAST_UPDATE, Date.class));
        });
    }

    @Test
    void isEmpty() {
        // Case #1 - No collection exists
        assertDoesNotThrow(() -> assertTrue(repository.isEmpty(STRUCT_TEST_COLL)));
        // Case #2 - Collection exists but it's empty
        mongo.createCollection(STRUCT_TEST_COLL_B);
        assertDoesNotThrow(() -> assertTrue(repository.isEmpty(STRUCT_TEST_COLL_B)));
        // Case #3 - Collection exists and has one document
        mongo.getCollection(STRUCT_TEST_COLL_B).insertOne(createTestDocument());
        assertDoesNotThrow(() -> assertFalse(repository.isEmpty(STRUCT_TEST_COLL_B)));
    }
}
