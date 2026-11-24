package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.*;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.RenameCollectionOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl.ExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;

@Tag("ISOLATION")
@SpringJUnitConfig(ExecutorRepo.class)
class ExecutorRepositoryIsolationTest {
    @Autowired ExecutorRepo repo;
    @MockitoBean MongoTemplate mongo;
    @MockitoBean MongoDatabase database;
    @MockitoBean(name="source") MongoCollection<Document> source;
    @MockitoBean(name="destination") MongoCollection<Document> destination;
    ListCollectionNamesIterable names;
    FindIterable<Document> found;
    MongoCursor<Document> cursor;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void defaults() {
        names = mock(ListCollectionNamesIterable.class); found = mock(FindIterable.class);
        cursor = mock(MongoCursor.class);
        when(mongo.getDb()).thenReturn(database);
        when(database.getName()).thenReturn("isolation");
        when(database.listCollectionNames()).thenReturn(names);
        when(names.into(any())).thenAnswer(call -> { Collection<String> out = call.getArgument(0); out.add("source"); return out; });
        when(mongo.getCollection("source")).thenReturn(source);
        when(mongo.createCollection("dest")).thenReturn(destination);
        when(source.find()).thenReturn(found);
        when(found.sort(any())).thenReturn(found);
        when(found.batchSize(anyInt())).thenReturn(found);
        when(found.cursor()).thenReturn(cursor);
    }

    @Test
    void existenceCreateDropAndRenameRespectCollectionState() throws Exception {
        assertTrue(repo.exists("source")); assertFalse(repo.exists("dest"));
        assertSame(destination, repo.create("dest"));
        assertThrows(EdsDbException.class, () -> repo.create("source"));
        assertThrows(EdsDbException.class, () -> repo.rename("missing", "dest"));
        repo.rename("source", "dest");
        ArgumentCaptor<RenameCollectionOptions> options = ArgumentCaptor.forClass(RenameCollectionOptions.class);
        verify(source).renameCollection(eq(new MongoNamespace("isolation", "dest")), options.capture());
        assertTrue(options.getValue().isDropTarget());
        repo.drop("dest"); verify(mongo).dropCollection("dest");
    }

    @Test
    void cloneCopiesDocumentsButNeverCallsInsertManyForEmptySource() throws Exception {
        assertSame(destination, repo.clone("source", "dest"));
        verify(destination, never()).insertMany(anyList());
        verify(cursor).close();
        Document doc = new Document("field", "value");
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(doc);
        assertSame(destination, repo.clone("source", "dest"));
        verify(destination).insertMany(List.of(doc));
        verify(cursor, times(2)).close();
        assertThrows(EdsDbException.class, () -> repo.clone("missing", "dest"));
        assertThrows(EdsDbException.class, () -> repo.clone("source", "source"));
    }

    @ParameterizedTest
    @ValueSource(ints={1000,1001,2000})
    void cloneCopiesFullBatchesAndRemainingDocuments(int count) throws Exception {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < count; i++) documents.add(new Document("index", i));
        Iterator<Document> iterator = documents.iterator();
        when(cursor.hasNext()).thenAnswer(call -> iterator.hasNext());
        when(cursor.next()).thenAnswer(call -> iterator.next());
        List<List<Document>> batches = new ArrayList<>();
        when(destination.insertMany(anyList())).thenAnswer(call -> {
            batches.add(new ArrayList<>(call.getArgument(0)));
            return null;
        });

        assertSame(destination, repo.clone("source", "dest"));

        assertEquals((count + 999) / 1000, batches.size());
        for (int i = 0; i < batches.size(); i++) {
            assertEquals(documents.subList(i * 1000, Math.min((i + 1) * 1000, count)), batches.get(i));
        }
        verify(found).batchSize(1000);
        verify(cursor).close();
    }

    @Test
    void syncUpdatesEveryDocumentAndLastSyncUsesDescendingSort() throws Exception {
        Date sync = new Date(1234);
        assertNull(repo.getLastSync("source"));
        when(found.first()).thenReturn(new Document("last_sync", sync));
        assertEquals(sync, repo.getLastSync("source"));
        verify(found, times(2)).sort(new Document("last_sync", -1));
        repo.sync("source", sync);
        verify(source).updateMany(new BasicDBObject(), new BasicDBObject("$set", new BasicDBObject("last_sync", sync)));
        assertThrows(EdsDbException.class, () -> repo.sync("missing", sync));
    }

    @Test
    @SuppressWarnings("unchecked")
    void countsAndAggregatesOnlyActiveIds() throws Exception {
        when(source.countDocuments(any(Bson.class))).thenReturn(7L);
        assertEquals(7, repo.countActiveDocuments(source));
        assertEquals(7, repo.countActiveDocuments("source"));
        ArgumentCaptor<Bson> filter = ArgumentCaptor.forClass(Bson.class);
        verify(source, times(2)).countDocuments(filter.capture());
        assertEquals(Document.parse("{deleted:{$ne:true}}").toBsonDocument(),
                filter.getValue().toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry()));
        AggregateIterable<Document> aggregate = mock(AggregateIterable.class);
        when(source.aggregate(anyList())).thenReturn(aggregate);
        assertEquals(List.of(), repo.getActiveDocumentsId("source"));
        ObjectId id = new ObjectId();
        when(aggregate.first()).thenReturn(new Document("docs", List.of(id)));
        assertEquals(List.of(id), repo.getActiveDocumentsId("source"));
        ArgumentCaptor<List<Bson>> pipeline = ArgumentCaptor.forClass(List.class);
        verify(source, times(2)).aggregate(pipeline.capture());
        assertEquals(2, pipeline.getValue().size());
        assertEquals(Document.parse("{$group:{_id:null,docs:{$push:'$_id'}}}").toBsonDocument(),
                pipeline.getValue().get(1).toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry()));
        assertThrows(EdsDbException.class, () -> repo.getActiveDocumentsId("missing"));
    }

    @ParameterizedTest
    @ValueSource(strings={"exists","create","drop","clone","rename","lastSync","sync","countCollection","countName","ids"})
    void wrapsDriverFailuresAndPreservesOriginalCause(String operation) throws Exception {
        MongoException cause = new MongoException("offline");
        org.junit.jupiter.api.function.Executable action;
        switch(operation) {
            case "exists" -> { when(database.listCollectionNames()).thenThrow(cause); action = () -> repo.exists("source"); }
            case "create" -> { when(mongo.createCollection("dest")).thenThrow(cause); action = () -> repo.create("dest"); }
            case "drop" -> { doThrow(cause).when(mongo).dropCollection("source"); action = () -> repo.drop("source"); }
            case "clone" -> {
                when(cursor.hasNext()).thenReturn(true, false);
                when(cursor.next()).thenReturn(new Document());
                when(destination.insertMany(anyList())).thenThrow(cause); action = () -> repo.clone("source", "dest"); }
            case "rename" -> { doThrow(cause).when(source).renameCollection(any(MongoNamespace.class), any(RenameCollectionOptions.class)); action = () -> repo.rename(source, "dest"); }
            case "lastSync" -> { when(found.first()).thenThrow(cause); action = () -> repo.getLastSync("source"); }
            case "sync" -> { when(source.updateMany(any(Bson.class), any(Bson.class))).thenThrow(cause); action = () -> repo.sync("source", new Date()); }
            case "countCollection" -> { when(source.countDocuments(any(Bson.class))).thenThrow(cause); action = () -> repo.countActiveDocuments(source); }
            case "countName" -> { when(source.countDocuments(any(Bson.class))).thenThrow(cause); action = () -> repo.countActiveDocuments("source"); }
            default -> { when(source.aggregate(anyList())).thenThrow(cause); action = () -> repo.getActiveDocumentsId("source"); }
        }
        assertSame(cause, assertThrows(EdsDbException.class, action).getCause());
        if (operation.equals("clone")) verify(cursor).close();
    }
}
