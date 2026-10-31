package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.mongodb.MongoException;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.*;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({EngineRepo.class, TerminologyRepo.class})
class RepositoriesIsolationTest {
    @Autowired private EngineRepo engines;
    @Autowired private TerminologyRepo terminology;
    @MockitoBean private IExecutorRepo executor;
    @MockitoBean private MongoTemplate mongo;
    @MockitoBean private MongoCollection<Document> collection;

    @Test
    void insertsEngineDocumentAndWrapsMongoFailure() throws Exception {
        Document document = new Document("available", false);
        engines.insert(collection, document);
        verify(collection).insertOne(document);
        MongoException cause = new MongoException("write failed");
        doThrow(cause).when(collection).insertOne(document);
        assertSame(cause, assertThrows(EdsDbException.class,
                () -> engines.insert(collection, document)).getCause());
    }

    @Test
    void refusesMissingEngineSourceBeforeQueryingMongo() throws Exception {
        assertEquals("The source collection does not exists: missing",
                assertThrows(EdsDbException.class, () -> engines.getActiveMaps("missing")).getMessage());
        verifyNoInteractions(mongo);
    }

    @Test
    @SuppressWarnings("unchecked")
    void filtersDeletedOrRootlessDocumentsAndMapsEngineFields() throws Exception {
        ObjectId id = new ObjectId();
        when(executor.exists("fhir")).thenReturn(true);
        when(mongo.getCollection("fhir")).thenReturn(collection);
        FindIterable<Document> found = mock(FindIterable.class);
        MongoCursor<Document> cursor = mock(MongoCursor.class);
        when(collection.find(any(Bson.class))).thenReturn(found);
        when(found.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(new Document("_id", id).append("template_id_root", List.of("root"))
                .append("uri", "urn:engine").append("version", "1"));

        var maps = engines.getActiveMaps("fhir");
        assertEquals(1, maps.size());
        assertEquals(id, maps.getFirst().getOid());
        assertEquals(List.of("root"), maps.getFirst().getRoot());
        assertEquals("urn:engine", maps.getFirst().getUri());
        assertEquals("1", maps.getFirst().getVersion());
        ArgumentCaptor<Bson> query = ArgumentCaptor.forClass(Bson.class);
        verify(collection).find(query.capture());
        assertEquals(Document.parse("{\"$and\":[{\"deleted\":{\"$ne\":true}},{\"template_id_root\":{\"$exists\":true}}]}")
                .toBsonDocument(), query.getValue().toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry()));
    }

    @Test
    void buildsCodeSystemAggregationAndReturnsMappedResults() throws Exception {
        List<TerminologyMapDTO> rows = List.of(new TerminologyMapDTO("system", "1", "code", null, null, false));
        when(mongo.aggregate(any(Aggregation.class), eq("terms"), eq(TerminologyMapDTO.class)))
                .thenReturn(new AggregationResults<>(rows, new Document()));
        assertEquals(rows, terminology.getAllCodeSystemVersions("terms"));
        ArgumentCaptor<Aggregation> query = ArgumentCaptor.forClass(Aggregation.class);
        verify(mongo).aggregate(query.capture(), eq("terms"), eq(TerminologyMapDTO.class));
        List<Document> pipeline = query.getValue().toPipeline(Aggregation.DEFAULT_CONTEXT);
        assertEquals(2, pipeline.size());
        Document group = pipeline.getFirst().get("$group", Document.class);
        assertEquals(new Document("system", "$system").append("version", "$version").append("deleted", "$deleted"),
                group.get("_id"));
        assertEquals(new Document("$first", "$code"), group.get("code"));
        assertTrue(pipeline.get(1).containsKey("$project"));
    }

    @Test
    void wrapsAggregationFailure() {
        MongoException cause = new MongoException("aggregation failed");
        when(mongo.aggregate(any(Aggregation.class), eq("terms"), eq(TerminologyMapDTO.class))).thenThrow(cause);
        assertSame(cause, assertThrows(EdsDbException.class,
                () -> terminology.getAllCodeSystemVersions("terms")).getCause());
    }

    @Test
    @SuppressWarnings("removal")
    void createsCompoundBackgroundIndexAndWrapsIndexFailure() throws Exception {
        IndexOperations indexes = mock(IndexOperations.class);
        when(mongo.indexOps("terms")).thenReturn(indexes);
        terminology.applyIndexes("terms");
        ArgumentCaptor<IndexDefinition> index = ArgumentCaptor.forClass(IndexDefinition.class);
        verify(indexes).ensureIndex(index.capture());
        assertEquals(new Document("system", 1).append("code", 1).append("deleted", 1).append("version", 1),
                index.getValue().getIndexKeys());
        assertEquals(true, index.getValue().getIndexOptions().get("background"));
        MongoException cause = new MongoException("index failed");
        when(indexes.ensureIndex(any())).thenThrow(cause);
        assertSame(cause, assertThrows(EdsDbException.class,
                () -> terminology.applyIndexes("terms")).getCause());
    }
}
