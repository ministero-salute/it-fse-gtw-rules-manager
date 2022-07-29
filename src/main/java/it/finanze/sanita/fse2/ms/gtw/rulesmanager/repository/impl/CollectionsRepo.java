package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.RenameCollectionOptions;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICollectionsRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class CollectionsRepo implements ICollectionsRepo {

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
        // Start copying
        // Note: This is just a simple 1:1 insert operation
        // considering we still do not know all the implementation details
        // this may be further optimized in the near future
        try {
            for (Document document : src.find()) {
                dst.insertOne(document);
            }
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new EdsDbException("Unable to clone collection", e);
        }
        // Return the new copied collection
        return dst;
    }

    @Override
    public Date getLastSync(String name) throws EdsDbException {
        // We accept null as value, if no record is found
        Date lastUpdate = null;
        FindIterable<Document> doc;
        // Get collection
        MongoCollection<Document> src = mongo.getCollection(name);
        try {
            // Get date
            doc = src.find()
                .sort(new Document().append(SchemaETY.FIELD_LAST_SYNC, -1))
                .limit(1);
        }catch (MongoException e) {
            throw new EdsDbException("Unable to sort by last_sync field to retrieve date", e);
        }
        // Extract
        for (Document document : doc) {
            // Gotta cast what you gotta cast
            lastUpdate = (Date) document.get(SchemaETY.FIELD_LAST_SYNC);
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
        BasicDBObject update = new BasicDBObject(SchemaETY.FIELD_LAST_SYNC, sync);
        try {
            // Execute sync
            src.updateMany(new BasicDBObject(), new BasicDBObject("$set", update));
        }catch (MongoException e) {
            throw new EdsDbException("Unable to add last_sync field on the given collection", e);
        }
    }

}
