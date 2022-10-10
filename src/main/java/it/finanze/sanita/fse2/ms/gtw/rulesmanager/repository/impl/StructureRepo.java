//package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;
//
//import com.mongodb.MongoException;
//import com.mongodb.client.MongoCollection;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
//import org.bson.Document;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static com.mongodb.client.model.Filters.eq;
//import static com.mongodb.client.model.Updates.currentDate;
//import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.StructureValuesetETY.FIELD_LAST_SYNC; 
//
//@Repository
//public class StructureRepo implements IStructureRepo {
//
//    @Autowired
//    private MongoTemplate mongo;
//
//    private Document getCurrentDocByDate(MongoCollection<Document> src) throws EdsDbException {
//        // Working var
//        Document doc;
//        // Execute
//        try {
//            // Get first parent document by date
//            doc = src.find()
//                .sort(new Document().append("last_update_date", -1))
//                .first();
//        }catch (MongoException e) {
//            throw new EdsDbException("Unable to sort by last update field to retrieve current document", e);
//        }
//        return doc;
//    }
//
//    @Override
//    public Date getLastSyncFromParent(String name, String field) throws EdsDbException {
//        // We accept null as value, if no record is found
//        Date lastUpdate = null;
//        List<Document> docs;
//        Document doc;
//        // Get collection
//        MongoCollection<Document> src = mongo.getCollection(name);
//        // Get first parent document by date
//        doc = getCurrentDocByDate(src);
//        // Check we have at least one document and exists the requested field
//        if(doc != null && doc.containsKey(field)) {
//            // Pick first document on the list and readFromLatestDoc last_sync
//            docs = doc.getList(field, Document.class);
//            // Check if field is not empty
//            if(!docs.isEmpty()) {
//                lastUpdate = docs.get(0).get(FIELD_LAST_SYNC, Date.class);
//            }
//        }
//        // Bye
//        return lastUpdate;
//    }
//
//    @Override
//    public void createEmptyDocOnParent(String name, String... fields) throws EdsDbException {
//        // Get collection
//        MongoCollection<Document> src = mongo.getCollection(name);
//        // Working var
//        Document document = new Document();
//        // Append fields
//        document = document.append("last_update_date", new Date());
//        // Iterate
//        for(String field: fields) {
//            document = document.append(field, new ArrayList<>());
//        }
//        // Insert document
//        try {
//            // Execute
//            src.insertOne(document);
//        }catch (MongoException e) {
//            throw new EdsDbException("Unable to insert an empty document into: " + name, e);
//        }
//    }
//
//    @Override
//    public void updateDueToEmptyOnParent(String name) throws EdsDbException {
//        // Get collection
//        MongoCollection<Document> src = mongo.getCollection(name);
//        // Retrieve current document
//        Document parent = getCurrentDocByDate(src);
//        // Check we have at least one document
//        if(parent == null) {
//            throw new EdsDbException("Unable to update collection due to no document returned");
//        }
//        // Check we have the field
//        if(!parent.containsKey("last_update_date")) {
//            throw new EdsDbException("No field exists on parent document matching: " + "last_update_date");
//        }
//        // Update document
//        try {
//            // Execute
//            src.updateOne(
//                eq("_id", parent.getObjectId("_id")),
//                currentDate("last_update_date")
//            );
//        }catch (MongoException e) {
//            // Catch data-layer runtime exceptions and turn into a checked exception
//            throw new EdsDbException("Unable to update parent document", e);
//        }
//    }
//
//    @Override
//    public boolean isEmpty(String name) throws EdsDbException {
//        // Working var
//        long docs;
//        // Get collection
//        MongoCollection<Document> src = mongo.getCollection(name);
//        // Verify size
//        try {
//            // Execute
//            docs = src.countDocuments();
//        }catch (MongoException e) {
//            throw new EdsDbException("Unable to count documents inside: " + name, e);
//        }
//        // Return comparison
//        return docs == 0;
//    }
//
//    @Override
//    public MongoCollection<Document> cloneFromParent(String source, String dest, String field) throws EdsDbException {
//        // Verify we are working on an existing collection
//        if(!mongo.collectionExists(source)) {
//            throw new EdsDbException("The source collection does not exists: " + source);
//        }
//        // Verify we do not overwrite an existing collection
//        if(mongo.collectionExists(dest)) {
//            throw new EdsDbException("The destination collection already exists: " + dest);
//        }
//        // Get collections
//        MongoCollection<Document> src = mongo.getCollection(source);
//        MongoCollection<Document> dst = mongo.createCollection(dest);
//        // Get first parent document by date
//        Document parent = getCurrentDocByDate(src);
//        // Check we have at least one document
//        if(parent != null) {
//            // Return data if available
//            if(!parent.containsKey(field)) {
//                throw new EdsDbException("Unable to clone due no key on parent document matching: " + field);
//            }
//            // Obtain documents to insert
//            List<Document> docs = parent.getList(field, Document.class);
//            // Note: We need to check docs.size() because insertMany does not allow empty lists
//            if(!docs.isEmpty()) {
//                try {
//                    dst.insertMany(docs);
//                } catch (MongoException e) {
//                    // Catch data-layer runtime exceptions and turn into a checked exception
//                    throw new EdsDbException("Unable to clone collection", e);
//                }
//            }
//        }
//        // Return the new collection
//        return dst;
//    }
//
//    @Override
//    public void insertInto(String source, Document doc) throws EdsDbException {
//        try {
//            mongo.getCollection(source).insertOne(doc);
//        }catch (MongoException ex) {
//            throw new EdsDbException("Unable to insert document into: " + source, ex);
//        }
//    }
//
//    @Override
//    public List<Document> readFromLatestDoc(String source, String field) throws EdsDbException {
//        // Working var
//        List<Document> docs;
//        // Retrieve current document
//        Document doc = getCurrentDocByDate(mongo.getCollection(source));
//        // Check existence
//        if(doc == null) {
//            // No document available, set as empty docs
//            docs = new ArrayList<>();
//        }else {
//            // Return data if available
//            if(!doc.containsKey(field)) {
//                throw new EdsDbException("Unable to readFromLatestDoc due no key on parent document matching: " + field);
//            }
//            // Retrieve
//            docs = doc.getList(field, Document.class);
//            // Check existence
//            if(docs == null) {
//                throw new EdsDbException("Unable to readFromLatestDoc due to null-instance returned by parent on field: " + field);
//            }
//        }
//        return docs;
//    }
//
//    @Override
//    public List<Document> readFromStagingDoc(String staging) throws EdsDbException {
//        // Verify we are working on an existing collection
//        if(!mongo.collectionExists(staging)) {
//            throw new EdsDbException("The staging collection does not exists: " + staging);
//        }
//        // Get collections
//        MongoCollection<Document> src = mongo.getCollection(staging);
//        // Retrieve documents
//        return src.find().into(new ArrayList<>());
//    }
//}
