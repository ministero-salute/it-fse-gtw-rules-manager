package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.sub.EngineMap;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl.*;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({DictionarySRV.class, EngineSRV.class, TerminologySRV.class})
class ServicesIsolationTest {
    @Autowired private DictionarySRV dictionary;
    @Autowired private EngineSRV engine;
    @Autowired private TerminologySRV terminology;
    @MockitoBean private ITerminologyRepo terms;
    @MockitoBean private IExecutorRepo executor;
    @MockitoBean private IEngineRepo engines;
    @MockitoBean private MongoCollection<Document> collection;

    @Test
    void convertsDictionaryFieldsAndFiltersNullEntries() throws Exception {
        Date created = new Date(1000), released = new Date(2000);
        when(terms.getAllCodeSystemVersions("terms")).thenReturn(Arrays.asList(null,
                new TerminologyMapDTO("system", "v1", "#WHITELIST#", released, created, false)));

        assertEquals(1, dictionary.syncCodeSystemVersions("terms", collection));
        ArgumentCaptor<List<Document>> inserted = ArgumentCaptor.forClass(List.class);
        verify(collection).insertMany(inserted.capture());
        assertEquals(List.of(new Document("system", "system").append("version", "v1")
                .append("creation_date", created).append("release_date", released)
                .append("whitelist", true).append("deleted", false)), inserted.getValue());
    }

    @Test
    void avoidsEmptyMongoInsert() throws Exception {
        when(terms.getAllCodeSystemVersions("empty")).thenReturn(List.of());
        assertEquals(0, dictionary.syncCodeSystemVersions("empty", collection));
        verifyNoInteractions(collection);
    }

    @Test
    void wrapsMongoWriteFailureAndPreservesCause() throws Exception {
        when(terms.getAllCodeSystemVersions("terms")).thenReturn(List.of(
                new TerminologyMapDTO("system", "v1", null, null, null, false)));
        MongoException cause = new MongoException("write failed");
        doThrow(cause).when(collection).insertMany(any());
        EdsDbException failure = assertThrows(EdsDbException.class,
                () -> dictionary.syncCodeSystemVersions("terms", collection));
        assertSame(cause, failure.getCause());
        assertEquals("Unable to sync code-system versions", failure.getMessage());
    }

    @Test
    void doesNotSynthesizeAnEmptyEngine() throws Exception {
        when(executor.getActiveDocumentsId("fhir")).thenReturn(List.of());
        assertFalse(engine.synthesize("fhir", collection));
        verify(executor, never()).getLastSync(any());
        verifyNoInteractions(engines, collection);
    }

    @Test
    void synthesizesUnavailableEngineWithFilesRootsAndSyncTime() throws Exception {
        ObjectId id = new ObjectId();
        Date sync = new Date(1000);
        when(executor.getActiveDocumentsId("fhir")).thenReturn(List.of(id));
        when(executor.getLastSync("fhir")).thenReturn(sync);
        when(engines.getActiveMaps("fhir")).thenReturn(List.of(
                new EngineMap(id, List.of("root"), "urn:engine", "1")));

        assertTrue(engine.synthesize("fhir", collection));
        ArgumentCaptor<Document> inserted = ArgumentCaptor.forClass(Document.class);
        verify(engines).insert(eq(collection), inserted.capture());
        assertEquals(new Document("roots", List.of(new Document("oid", id)
                .append("template_id_root", List.of("root")).append("uri", "urn:engine")
                .append("version", "1"))).append("files", List.of(id))
                .append("last_sync", sync).append("available", false), inserted.getValue());
    }

    @Test
    void propagatesEngineReadFailureWithoutWriting() throws Exception {
        EdsDbException cause = new EdsDbException("read failed");
        when(executor.getActiveDocumentsId("fhir")).thenThrow(cause);
        assertSame(cause, assertThrows(EdsDbException.class, () -> engine.synthesize("fhir", collection)));
        verifyNoInteractions(engines, collection);
    }

    @Test
    void appliesIndexesAndPropagatesRepositoryFailure() throws Exception {
        terminology.applyIndexes("terms");
        verify(terms).applyIndexes("terms");
        EdsDbException cause = new EdsDbException("index failed");
        doThrow(cause).when(terms).applyIndexes("broken");
        assertSame(cause, assertThrows(EdsDbException.class, () -> terminology.applyIndexes("broken")));
    }
}
