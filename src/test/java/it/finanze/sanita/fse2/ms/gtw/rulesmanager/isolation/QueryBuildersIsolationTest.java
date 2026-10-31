package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.bson.types.*;
import org.junit.jupiter.api.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;

@Tag("ISOLATION")
class QueryBuildersIsolationTest {
    final String id = "0123456789abcdef01234567";
    final Date date = new Date(1234);
    final ObjectMapper mapper = new ObjectMapper();

    @Test void schemaConversionPreservesMetadataAndComparatorIgnoresExtraFields() throws Exception {
        SchemaDTO dto = mapper.readValue("{\"document\":{\"id\":\""+id+"\",\"nameSchema\":\"schema.xsd\",\"contentSchema\":\"YWJj\",\"typeIdExtension\":\"type\",\"rootSchema\":true,\"lastUpdateDate\":1234}}", SchemaDTO.class);
        SchemaQuery query = new SchemaQuery();
        Document doc = query.getUpsertQuery(dto);
        assertEquals("schema.xsd", doc.getString("name_schema"));
        assertEquals(true, doc.getBoolean("root_schema"));
        assertArrayEquals(new byte[]{97,98,99}, doc.get("content_schema", Binary.class).getData());
        assertComparator(query, doc);
    }
    @Test void schematronConversionOmitsAbsentSystemButKeepsPresentSystem() throws Exception {
        SchematronDTO dto = mapper.readValue("{\"document\":{\"id\":\""+id+"\",\"name\":\"rules.sch\",\"content\":\"YWJj\",\"templateIdRoot\":\"root\",\"version\":\"1\",\"lastUpdateDate\":1234}}", SchematronDTO.class);
        SchematronQuery query = new SchematronQuery();
        assertFalse(query.getUpsertQuery(dto).containsKey("system"));
        dto.getDocument().setSystem("urn:system");
        Document doc = query.getUpsertQuery(dto); assertEquals("urn:system", doc.getString("system"));
        assertComparator(query, doc);
    }
    @Test void fhirConversionIncludesOptionalRootsOnlyWhenPresent() throws Exception {
        FhirStructuresDTO dto = mapper.readValue("{\"document\":{\"id\":\""+id+"\",\"filename\":\"fhir.json\",\"content\":\"YWJj\",\"type\":\"fhir\",\"uri\":\"urn:fhir\",\"version\":\"1\",\"lastUpdateDate\":1234,\"deleted\":false}}", FhirStructuresDTO.class);
        FhirStructuresQuery query = new FhirStructuresQuery();
        assertFalse(query.getUpsertQuery(dto).containsKey("template_id_root"));
        dto.getDocument().setTemplateIdRoot(List.of("root"));
        Document doc = query.getUpsertQuery(dto); assertEquals(List.of("root"), doc.getList("template_id_root", String.class));
        assertComparator(query, doc);
    }
    @Test void terminologyBuildsBulkInsertionDeletionAndComparatorQueries() throws Exception {
        TerminologyDTO dto = mapper.readValue("{\"document\":{\"id\":\""+id+"\",\"system\":\"urn:system\",\"version\":\"1\",\"code\":\"code\",\"description\":\"description\",\"lastUpdateDate\":1234,\"releaseDate\":1234}}", TerminologyDTO.class);
        TerminologyQuery query = new TerminologyQuery();
        Document doc = query.getUpsertQuery(dto);
        assertEquals("code", doc.getString("code")); assertEquals(date, doc.getDate("release_date"));
        assertEquals(List.of(doc), query.getUpsertQueries(List.of(dto.getDocument())));
        assertEquals(Document.parse("{_id:{$in:[{$oid:'"+id+"'}]}}").toBsonDocument(),
                query.getDeleteQueries(List.of(id)).toBsonDocument(Document.class, com.mongodb.MongoClientSettings.getDefaultCodecRegistry()));
        // Release date is deliberately not part of this comparator's documented projection.
        doc.remove("release_date"); assertComparator(query, doc);
    }
    void assertComparator(IQueryEDS<?> query, Document doc) {
        assertEquals(new ObjectId(id), doc.getObjectId("_id")); assertEquals(date, doc.getDate("last_update_date"));
        doc.put("last_sync", date);
        Document expected = new Document(doc); doc.put("unrelated", "ignored");
        assertEquals(expected, query.getComparatorQuery(doc));
        assertEquals(new Document("_id", new ObjectId(id)), query.getFilterQuery(id));
        assertEquals(query.getFilterQuery(id), query.getDeleteQuery(id));
        assertThrows(IllegalArgumentException.class, () -> query.getFilterQuery("invalid-id"));
    }
}
