package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.RenameCollectionOptions;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY.FIELD_LAST_SYNC;

@Repository
public class ExecutorRepo implements IExecutorRepo {

    @Autowired
    private MongoTemplate mongo;

    public void rename(MongoCollection<Document> src, String target) throws EdsDbException {
        try {
            // Execute
            src.renameCollection(
                new MongoNamespace(mongo.getDb().getName(), target),
                new RenameCollectionOptions().dropTarget(true)
            );
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new EdsDbException("Unable to rename collections", e);
        }
    }

    public boolean exists(String name) throws EdsDbException {
        // Working var
        boolean exists;
        try {
            // Verify
            exists = mongo.collectionExists(name);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new EdsDbException("Unable to verify collection existence", e);
        }
        return exists;
    }

    public void drop(String name) throws EdsDbException {
        try {
            // Execute
            mongo.dropCollection(name);
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new EdsDbException("Unable to drop collection", e);
        }
    }

    public MongoCollection<Document> create(String name) throws EdsDbException {
        // Working var
        MongoCollection<Document> collection;
        // Verify we do not overwrite an existing collection
        if(mongo.collectionExists(name)) {
            throw new EdsDbException("The collection already exists: " + name);
        }
        try {
            // Execute
            collection = mongo.createCollection(name);
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new EdsDbException("Unable to create collection: " + name, e);
        }
        // Return collection
        return collection;
    }

    public MongoCollection<Document> clone(String source, String dest) throws EdsDbException {
        // Verify we are working on an existing collection
        if(!exists(source)) {
            throw new EdsDbException("The source collection does not exists: " + source);
        }
        // Verify we do not overwrite an existing collection
        if(exists(dest)) {
            throw new EdsDbException("The destination collection already exists: " + dest);
        }
        // Get collections
        MongoCollection<Document> src = mongo.getCollection(source);
        MongoCollection<Document> dst = mongo.createCollection(dest);
        // Get iterator
        List<Document> docs = src.find().into(new ArrayList<>());
        // Note: We need to check docs.size() because insertMany() does not allow empty lists
        if(!docs.isEmpty()) {
            try {
                dst.insertMany(docs);
            } catch (MongoException e) {
                // Catch data-layer runtime exceptions and turn into a checked exception
                throw new EdsDbException("Unable to clone collection", e);
            }
        }
        // Return the new copied collection
        return dst;
    }

    @Override
    public Date getLastSync(String name) throws EdsDbException {
        // We accept null as value, if no record is found
        Date lastUpdate = null;
        Document doc;
        // Get collection
        MongoCollection<Document> src = mongo.getCollection(name);
        try {
            // Get date
            doc = src.find()
                .sort(new Document().append(FIELD_LAST_SYNC, -1))
                .first();
        }catch (MongoException e) {
            throw new EdsDbException("Unable to sort by last_sync field to retrieve date", e);
        }
        // Extract
        if(doc != null) {
            // Pick first document on the list and readFromLatestDoc last_sync
            lastUpdate = doc.get(FIELD_LAST_SYNC, Date.class);
        }
        // Bye
        return lastUpdate;
    }

    @Override
    public void sync(String name, Date sync) throws EdsDbException {
        // Verify we are working on an existing collection
        if(!exists(name)) {
            throw new EdsDbException("The source collection does not exists: " + name);
        }
        // Get collection
        MongoCollection<Document> src = mongo.getCollection(name);
        // Create update
        BasicDBObject update = new BasicDBObject(FIELD_LAST_SYNC, sync);
        try {
            // Execute sync
            src.updateMany(new BasicDBObject(), new BasicDBObject("$set", update));
        }catch (MongoException e) {
            throw new EdsDbException("Unable to add last_sync field on the given collection", e);
        }
    }

}
