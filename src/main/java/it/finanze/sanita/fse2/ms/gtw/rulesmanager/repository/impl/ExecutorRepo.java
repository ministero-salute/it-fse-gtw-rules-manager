
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY.FIELD_DELETED;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY.FIELD_ID;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY.FIELD_LAST_SYNC;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.RenameCollectionOptions;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;

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

    public void rename(String src, String target) throws EdsDbException {
        // Must exists
        boolean exists = exists(src);
        // Throw if it doesn't
        if(!exists) throw new EdsDbException("The src collection does not exists");
        // Now rename
        rename(mongo.getCollection(src), target);
    }

//    public boolean exists(String name) throws EdsDbException {
//        // Working var
//        boolean exists;
//        try {
//            // Verify
//            exists = mongo.collectionExists(name);
//        } catch (MongoException e) {
//            // Catch data-layer runtime exceptions and turn into a checked exception
//            throw new EdsDbException("Unable to verify collection existence", e);
//        }
//        return exists;
//    }
    
    public boolean exists(String name) throws EdsDbException {
        try {
            // Get the MongoDatabase instance from MongoTemplate
            MongoDatabase database = mongo.getDb();
            
            // Get the list of collection names in the database
            List<String> collectionNames = new ArrayList<>();
            database.listCollectionNames().into(collectionNames);

            // Check if the collection exists
            return collectionNames.contains(name);
        } catch (MongoException e) {
            // Catch MongoDB exceptions and throw a custom exception
            throw new EdsDbException("Unable to verify collection existence", e);
        }
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
//        if(mongo.collectionExists(name)) {
        if(exists(name)) {
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

    @Override
    public long countActiveDocuments(MongoCollection<Document> src) throws EdsDbException {
        long size;
        try {
            size = src.countDocuments(
                Filters.ne(FIELD_DELETED, true)
            );
        }catch (MongoException e) {
            throw new EdsDbException("Unable to count document inside collection", e);
        }
        return size;
    }

    @Override
    public long countActiveDocuments(String src) throws EdsDbException {
        long size;
        try {
            // We are using getCollection because it may not exist,
            // at the same time we don't want to enforce an exists() check
            // because it may be legit. For example, when you run rules manager on
            // an empty database, the changeset will return empty and if it's
            // the first iteration there won't be any production collection yet
            size = mongo.getCollection(src).countDocuments(
                Filters.ne(FIELD_DELETED, true)
            );
        }catch (MongoException e) {
            throw new EdsDbException("Unable to count document inside collection", e);
        }
        return size;
    }

    @Override
    public List<ObjectId> getActiveDocumentsId(String name) throws EdsDbException {
        // Verify we are working on an existing collection
        if(!exists(name)) {
            throw new EdsDbException("The source collection does not exists: " + name);
        }

        List<ObjectId> ids = new ArrayList<>();

        try {
            // Get collection
            MongoCollection<Document> src = mongo.getCollection(name);
            // Create query
            Document agg = src.aggregate(
                asList(
                    match(Filters.ne(FIELD_DELETED, true)),
                    group(null, push("docs", String.format("$%s", FIELD_ID)))
                )
            ).first();
            // We should always have at least one not-null document
            if(agg != null) ids = new ArrayList<>(agg.getList("docs", ObjectId.class));
        }catch (MongoException e) {
            throw new EdsDbException("Unable to get active document ids inside collection", e);
        }

        return ids;
    }
}
