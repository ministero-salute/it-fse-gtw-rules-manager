package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.structures.base;

import org.bson.Document;

import java.util.*;

public abstract class EDSStructureHandler {
    public static final String STRUCT_TEST_COLL = "struct-test-0";
    public static final String STRUCT_TEST_COLL_B = "struct-test-1";

    public static final String FIELD_MAP = "map";
    public static final String FIELD_VALUESET = "valueset";
    public static final String FIELD_DEFINITION = "definition";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_LAST_SYNC = "last_sync";

    public Document createTestDocument() {
        return new Document("key", "value");
    }

    public List<Document> createTestDocumentAsList() {
        List<Document> docs = new ArrayList<>();
        docs.add(createTestDocument());
        return docs;
    }

    public Document createTestItemDocument() {
        return new Document(FIELD_LAST_SYNC, new Date());
    }

    public Document createTestParentDocument() {
        return createTestParentDocument(new HashMap<>());
    }

    public Document createTestParentDocument(Map<String, Object> fields) {
        // Create document to insert
        final Document doc = new Document();
        // Append last update
        doc.append(FIELD_LAST_UPDATE, new Date());
        // Iterate and attach values
        fields.forEach(doc::append);
        // Return
        return doc;
    }

    public Map<String, Object> createTestParentFields() {
        // Working var
        Map<String, Object> obj = new HashMap<>();
        ArrayList<Document> arr = new ArrayList<>();
        // Add
        obj.put(FIELD_MAP, arr);
        obj.put(FIELD_DEFINITION, arr);
        obj.put(FIELD_VALUESET, arr);
        // Return
        return obj;
    }

    public Map<String, Object> createTestParentFields(Document ...docs) {
        // Working var
        Map<String, Object> obj = new HashMap<>();
        ArrayList<Document> arr = new ArrayList<>(Arrays.asList(docs));
        // Add
        obj.put(FIELD_MAP, arr);
        obj.put(FIELD_DEFINITION, arr);
        obj.put(FIELD_VALUESET, arr);
        // Return
        return obj;
    }

}
