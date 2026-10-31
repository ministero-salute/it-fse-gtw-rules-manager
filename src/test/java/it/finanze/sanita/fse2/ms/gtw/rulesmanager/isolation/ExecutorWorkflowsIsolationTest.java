package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.*;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.*;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.base.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base.BaseSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.chunks.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({BridgeEDS.class, CollectionNaming.class, ProfileUtility.class,
        SchemaCFG.class, SchematronCFG.class, FhirStructuresCFG.class, TerminologyCFG.class,
        DictionaryCFG.class, EngineCFG.class, SchemaExecutor.class, SchematronExecutor.class,
        FhirStructuresExecutors.class, TermsExecutor.class, DictionaryExecutor.class, EnginesExecutor.class,
        SchemaQuery.class, SchematronQuery.class, FhirStructuresQuery.class, TerminologyQuery.class})
@TestPropertySource(properties = {
        "eds.changeset.schema.status=http://eds/schema/status", "eds.changeset.schema.data=http://eds/schema/",
        "eds.changeset.schematron.status=http://eds/schematron/status", "eds.changeset.schematron.data=http://eds/schematron/",
        "eds.changeset.fhir.status=http://eds/fhir/status", "eds.changeset.fhir.data=http://eds/fhir/",
        "eds.changeset.terminology.chunks.status=http://eds/terms/status",
        "eds.changeset.terminology.chunks.ins=http://eds/terms/insert/",
        "eds.changeset.terminology.chunks.del=http://eds/terms/delete/"})
class ExecutorWorkflowsIsolationTest {
    static final String ID = "0123456789abcdef01234567";
    @Autowired SchemaExecutor schema;
    @Autowired SchematronExecutor schematron;
    @Autowired FhirStructuresExecutors fhir;
    @Autowired TermsExecutor terms;
    @Autowired DictionaryExecutor dictionary;
    @Autowired EnginesExecutor engines;
    @MockitoBean IExecutorRepo repo;
    @MockitoBean IEDSClient client;
    @MockitoBean LoggerHelper logger;
    @MockitoBean IConfigSRV config;
    @MockitoBean IDictionarySRV dictionaries;
    @MockitoBean IEngineSRV engineService;
    @MockitoBean ITerminologySRV terminology;
    @MockitoBean MongoCollection<Document> collection;

    @BeforeEach
    void defaults() throws Exception {
        when(repo.exists(anyString())).thenReturn(true);
        when(repo.create(anyString())).thenReturn(collection);
        when(repo.clone(anyString(), anyString())).thenReturn(collection);
        when(repo.countActiveDocuments(anyString())).thenReturn(1L);
        when(repo.countActiveDocuments(collection)).thenReturn(1L);
        when(client.getStatus(any(), any(), any())).thenReturn(changes(true), changes(false));
        when(client.getSnapshot(any(), any(), eq(ChangeSetChunkDTO.class))).thenReturn(snapshot(true), snapshot(false));
        ObjectMapper mapper = new ObjectMapper();
        when(client.getDocument(any(), eq(ID), eq(SchemaDTO.class))).thenReturn(mapper.readValue(
                "{\"document\":{\"id\":\"" + ID + "\",\"nameSchema\":\"schema.xsd\",\"contentSchema\":\"YWJj\",\"rootSchema\":true}}", SchemaDTO.class));
        when(client.getDocument(any(), eq(ID), eq(SchematronDTO.class))).thenReturn(mapper.readValue(
                "{\"document\":{\"id\":\"" + ID + "\",\"name\":\"rules.sch\",\"content\":\"YWJj\",\"system\":\"urn:sys\"}}", SchematronDTO.class));
        when(client.getDocument(any(), eq(ID), eq(FhirStructuresDTO.class))).thenReturn(mapper.readValue(
                "{\"document\":{\"id\":\"" + ID + "\",\"filename\":\"fhir.json\",\"content\":\"YWJj\",\"templateIdRoot\":[\"root\"]}}", FhirStructuresDTO.class));
        TerminologyDTO.Terminology doc = new TerminologyDTO.Terminology();
        doc.setId(ID); doc.setCode("code"); doc.setSystem("urn:system");
        TerminologyChunkInsDTO ins = new TerminologyChunkInsDTO(); ins.setDocuments(List.of(doc));
        TerminologyChunkDelDTO del = new TerminologyChunkDelDTO(); del.setDocuments(List.of(ID));
        when(client.getChunkIns(any(), anyString(), anyInt(), eq(TerminologyChunkInsDTO.class))).thenReturn(ins);
        when(client.getChunkDel(any(), anyString(), anyInt(), eq(TerminologyChunkDelDTO.class))).thenReturn(del);
        when(collection.insertMany(anyList())).thenReturn(InsertManyResult.acknowledged(Map.of(0, new BsonInt32(1))));
        when(collection.updateMany(any(Bson.class), any(Bson.class))).thenReturn(UpdateResult.acknowledged(1, 1L, null));
        for (ExecutorEDS<?> e : List.of(schema, schematron, fhir, terms, dictionary, engines)) {
            e.setOnBeforeSwap(null); e.setOnFailedSwap(null); e.setOnSuccessSwap(null);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void completeUpdateProcessesChangesVerifiesSyncsAndSwaps(int kind) throws Exception {
        ExecutorEDS<?> e = executor(kind);
        assertEquals(OK, e.execute());
        verify(repo).sync(eq(e.getConfig().getStaging()), any(Date.class));
        verify(repo).rename(collection, e.getConfig().getProduction());
        if (kind == 3) {
            verify(collection).insertMany(argThat(docs -> docs.size() == 1 && "code".equals(docs.getFirst().get("code"))));
            verify(terminology).applyIndexes(e.getConfig().getStaging());
        } else {
            verify(collection).insertOne(argThat(doc -> doc.getObjectId("_id").toHexString().equals(ID)
                    && doc.values().stream().anyMatch(value -> value instanceof org.bson.types.Binary binary
                        && Arrays.equals(binary.getData(), new byte[]{97,98,99}))));
            verify(collection).findOneAndUpdate(any(Bson.class), any(Bson.class));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void fullRecoveryRebuildsFromRemoteInsteadOfCloning(int kind) throws Exception {
        ExecutorEDS<?> e = executor(kind);
        assertEquals(OK, e.recovery());
        verify(repo).create(e.getConfig().getStaging());
        verify(repo, never()).clone(anyString(), anyString());
        verify(repo).rename(collection, e.getConfig().getProduction());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void emptyAlignedProductionExitsWithoutStaging(int kind) throws Exception {
        when(client.getStatus(any(), any(), any())).thenReturn(changes(false));
        when(client.getSnapshot(any(), any(), eq(ChangeSetChunkDTO.class))).thenReturn(snapshot(false));
        assertEquals(EXIT, executor(kind).execute());
        verify(repo, never()).create(anyString());
        verify(repo, never()).rename(any(MongoCollection.class), anyString());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void firstUpdateCreatesStagingWhenProductionMissing(int kind) throws Exception {
        when(repo.exists(anyString())).thenReturn(false);
        ExecutorEDS<?> e = executor(kind);
        assertEquals(OK, e.execute());
        verify(repo).create(e.getConfig().getStaging());
    }

    @ParameterizedTest
    @ValueSource(strings = {"drop", "status", "exists", "clone", "count", "sync", "rename", "insert", "delete", "alignment", "index", "create"})
    void schemaFailuresStopBeforeUnsafeSwap(String failure) throws Exception {
        failBoundary(failure, schema);
        assertEquals(KO, schema.execute());
        if (!failure.equals("rename")) verify(repo, never()).rename(any(MongoCollection.class), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"drop", "status", "exists", "clone", "count", "sync", "rename", "insert", "delete", "alignment", "index", "create"})
    void terminologyFailuresStopBeforeUnsafeSwap(String failure) throws Exception {
        failBoundary(failure, terms);
        assertEquals(KO, terms.execute());
        if (!failure.equals("rename")) verify(repo, never()).rename(any(MongoCollection.class), anyString());
    }

    void failBoundary(String failure, ExecutorEDS<?> e) throws Exception {
        EdsDbException db = new EdsDbException("database unavailable");
        switch (failure) {
            case "drop" -> doThrow(db).when(repo).drop(anyString());
            case "status" -> { when(client.getStatus(any(), any(), any())).thenReturn(null);
                when(client.getSnapshot(any(), any(), eq(ChangeSetChunkDTO.class))).thenReturn(null); }
            case "exists" -> when(repo.exists(anyString())).thenThrow(db);
            case "clone" -> when(repo.clone(anyString(), anyString())).thenThrow(db);
            case "count" -> when(repo.countActiveDocuments(collection)).thenThrow(db);
            case "sync" -> doThrow(db).when(repo).sync(anyString(), any());
            case "rename" -> doThrow(db).when(repo).rename(collection, e.getConfig().getProduction());
            case "insert" -> { doThrow(new MongoException("insert failed")).when(collection).insertOne(any());
                when(collection.insertMany(anyList())).thenThrow(new MongoException("insert failed")); }
            case "delete" -> { when(collection.findOneAndUpdate(any(Bson.class), any(Bson.class))).thenThrow(new MongoException("delete failed"));
                when(collection.updateMany(any(Bson.class), any(Bson.class))).thenThrow(new MongoException("delete failed")); }
            case "alignment" -> { when(client.getStatus(any(), any(), any())).thenReturn(changes(true));
                when(client.getSnapshot(any(), any(), eq(ChangeSetChunkDTO.class))).thenReturn(snapshot(true)); }
            case "index" -> { if (e == terms) doThrow(db).when(terminology).applyIndexes(anyString());
                else when(repo.countActiveDocuments(collection)).thenReturn(2L); }
            case "create" -> { when(repo.exists(anyString())).thenReturn(false); when(repo.create(anyString())).thenThrow(db); }
            default -> fail("unknown failure");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void swapCallbacksRespectFailureAndSuccessWithoutChangingCommittedOutcome(boolean callbackOk) throws Exception {
        var callback = callbackOk ? CB_OK : CB_KO;
        schema.setOnBeforeSwap(() -> callback);
        schema.setOnFailedSwap(() -> callback);
        schema.setOnSuccessSwap(() -> CB_KO);
        assertEquals(callbackOk ? OK : KO, schema.execute());
        if (callbackOk) verify(repo).rename(collection, schema.getConfig().getProduction());
        else verify(repo, times(2)).drop(schema.getConfig().getStaging());
    }

    @Test
    void missingRemoteDocumentIsRetriedAndNeverInserted() throws Exception {
        when(client.getDocument(any(), anyString(), eq(SchemaDTO.class))).thenReturn(null);
        assertEquals(KO, schema.execute());
        verify(client, times(3)).getDocument(any(), eq(ID), eq(SchemaDTO.class));
        verify(collection, never()).insertOne(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void mismatchedEmptyProductionAndCountFailuresAreNotTreatedAsAligned(int kind) throws Exception {
        when(client.getStatus(any(), any(), any())).thenReturn(changes(false));
        when(client.getSnapshot(any(), any(), eq(ChangeSetChunkDTO.class))).thenReturn(snapshot(false));
        when(repo.countActiveDocuments(anyString())).thenReturn(2L);
        assertEquals(KO, executor(kind).execute());
        when(repo.countActiveDocuments(anyString())).thenThrow(new EdsDbException("count failed"));
        assertEquals(KO, executor(kind).execute());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void derivedCollectionsCreateBackupsAndSupportRollback(boolean exists) throws Exception {
        when(repo.exists(anyString())).thenReturn(exists);
        when(dictionaries.syncCodeSystemVersions(anyString(), eq(collection))).thenReturn(2);
        when(engineService.synthesize(anyString(), eq(collection))).thenReturn(true);
        assertEquals(OK, dictionary.execute());
        assertEquals(OK, engines.execute());
        assertEquals(CB_OK, dictionary.onRecovery());
        assertEquals(CB_OK, engines.onRecovery());
        verify(repo).rename(dictionary.getConfig().getBackup(), dictionary.getConfig().getProduction());
        verify(repo).rename(engines.getConfig().getBackup(), engines.getConfig().getProduction());
    }

    @ParameterizedTest
    @ValueSource(strings = {"drop", "create", "clone", "service", "rename", "stats"})
    void derivedCollectionFailurePathsDoNotReportSuccess(String failure) throws Exception {
        EdsDbException db = new EdsDbException("failure");
        switch (failure) {
            case "drop" -> doThrow(db).when(repo).drop(anyString());
            case "create" -> when(repo.create(anyString())).thenThrow(db);
            case "clone" -> when(repo.clone(anyString(), anyString())).thenThrow(db);
            case "service" -> { when(dictionaries.syncCodeSystemVersions(anyString(), any())).thenThrow(db);
                when(engineService.synthesize(anyString(), any())).thenThrow(db); }
            case "rename" -> doThrow(db).when(repo).rename(any(MongoCollection.class), anyString());
            case "stats" -> when(repo.countActiveDocuments(anyString())).thenThrow(db);
        }
        assertEquals(failure.equals("stats") ? OK : KO, dictionary.execute());
        assertEquals(failure.equals("stats") || failure.equals("create") ? OK : KO, engines.execute());
        doThrow(db).when(repo).rename(anyString(), anyString());
        assertEquals(CB_KO, dictionary.onRecovery());
        assertEquals(CB_KO, engines.onRecovery());
    }

    @Test
    void emptyChunksAvoidMongoWritesAndUnavailableChunksFailVerification() throws Exception {
        TerminologyChunkInsDTO ins = new TerminologyChunkInsDTO(); ins.setDocuments(List.of());
        TerminologyChunkDelDTO del = new TerminologyChunkDelDTO(); del.setDocuments(List.of());
        when(client.getChunkIns(any(), anyString(), anyInt(), any())).thenReturn(ins);
        when(client.getChunkDel(any(), anyString(), anyInt(), any())).thenReturn(del);
        assertEquals(KO, terms.execute());
        verify(collection, never()).insertMany(anyList());
        verify(collection, never()).updateMany(any(Bson.class), any(Bson.class));
        when(client.getChunkIns(any(), anyString(), anyInt(), any())).thenReturn(null);
        when(client.getChunkDel(any(), anyString(), anyInt(), any())).thenReturn(null);
        when(client.getSnapshot(any(), any(), eq(ChangeSetChunkDTO.class))).thenReturn(snapshot(true));
        assertEquals(KO, terms.execute());
    }

    ExecutorEDS<?> executor(int kind) { return switch(kind) { case 0 -> schema; case 1 -> schematron; case 2 -> fhir; default -> terms; }; }
    static ChangeSetDTO changes(boolean pending) {
        return new ChangeSetDTO(null, null, new Date(1000), new Date(2000),
                pending ? List.of(new BaseSetDTO<>(ID, null)) : List.of(),
                pending ? List.of(new BaseSetDTO<>(ID, null)) : List.of(), pending ? 2 : 0, 1);
    }
    static ChangeSetChunkDTO snapshot(boolean pending) {
        return new ChangeSetChunkDTO(null, null, new Date(1000), new Date(2000),
                new ChunksDTO("snapshot", new ChunkStatsDTO(pending ? 1 : 0, 1, pending ? 1 : 0),
                        new ChunkStatsDTO(pending ? 1 : 0, 1, pending ? 1 : 0)), pending ? 2 : 0, 1);
    }
}
