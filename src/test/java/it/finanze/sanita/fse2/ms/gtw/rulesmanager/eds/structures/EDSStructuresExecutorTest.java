package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.structures;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents.DefinitionCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents.MapCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents.ValuesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.structures.base.EDSStructureHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.StructureExecutor;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EDSStructuresExecutorTest extends EDSStructureHandler {

    @Autowired
    private MapCFG map;
    @Autowired
    private ValuesetCFG valueset;
    @Autowired
    private DefinitionCFG definition;
    @MockBean
    private IStructureRepo repository;
    @SpyBean
    private StructureExecutor executor;

    @Test
    void decode() {
        // Case #1 - Everything is down
        List<ActionRes> res = asList(KO, KO, KO);
        assertEquals(KO, executor.onDecode(res));
        // Case #2 - Something is down
        res = asList(OK, EMPTY, KO);
        assertEquals(KO, executor.onDecode(res));
        // Case #3 - Everything is up
        res = asList(OK, OK, OK);
        assertEquals(OK, executor.onDecode(res));
        // Case #4 - Most is up but someone is empty
        res = asList(OK, OK, EMPTY);
        assertEquals(OK, executor.onDecode(res));
        // Case #5 - Everything is empty
        res = asList(EMPTY, EMPTY, EMPTY);
        assertEquals(EMPTY, executor.onDecode(res));
    }

    @Test
    void push() {
        // Check mongo exception is handled
        assertDoesNotThrow(() -> {
            // Provide knowledge
            doThrow(
                new EdsDbException("Test error")
            ).when(repository).insertInto(anyString(), any(Document.class));
            // Execute
            assertEquals(KO, executor.onPush(new Document()));
        });
    }

    @Test
    void merge() {
        // Create fake codes
        Map<ChangesetCFG, ActionRes> codes = new HashMap<>();
        // Put data
        codes.put(map, OK);
        codes.put(valueset, EMPTY);
        codes.put(definition, OK);
        // Create empty document
        Document doc = createTestParentDocument();
        // Case #1 - No merge issues
        // Start assertions
        assertDoesNotThrow(() -> {
            // Provide knowledge
            when(repository.readFromLatestDoc(anyString(), anyString())).thenReturn(
                createTestDocumentAsList()
            );
            when(repository.readFromStagingDoc(anyString())).thenReturn(
                createTestDocumentAsList()
            );
            // Execute
            assertEquals(OK, executor.onMerge(codes, doc));
            // Assert document
            assertTrue(doc.containsKey(FIELD_LAST_UPDATE));
            assertTrue(doc.containsKey(map.getProduction()));
            assertTrue(doc.containsKey(valueset.getProduction()));
            assertTrue(doc.containsKey(definition.getProduction()));
            assertEquals(1, doc.getList(map.getProduction(), Document.class).size());
            assertEquals(1, doc.getList(valueset.getProduction(), Document.class).size());
            assertEquals(1, doc.getList(definition.getProduction(), Document.class).size());
        });
        // Case #2 - Merge issues
        // Start assertions
        assertDoesNotThrow(() -> {
            // Provide knowledge
            when(repository.readFromLatestDoc(anyString(), anyString())).thenThrow(
                new EdsDbException("Test error")
            );
            when(repository.readFromStagingDoc(anyString())).thenThrow(
                new EdsDbException("Test error")
            );
            // Execute
            assertEquals(KO, executor.onMerge(codes, doc));
        });
    }

    @Test
    void empty() {
        // Check mongo exception is handled
        assertDoesNotThrow(() -> {
            // Provide knowledge
            when(repository.isEmpty(anyString())).thenThrow(new EdsDbException("Test error"));
            // Execute
            assertEquals(KO, executor.onEmpty());
        });
    }

}
