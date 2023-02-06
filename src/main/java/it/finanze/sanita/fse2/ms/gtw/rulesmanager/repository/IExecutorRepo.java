/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface IExecutorRepo {
    void rename(MongoCollection<Document> src, String target) throws EdsDbException;
    void rename(String src, String target) throws EdsDbException;
    boolean exists(String name) throws EdsDbException;
    void drop(String name) throws EdsDbException;
    MongoCollection<Document> create(String name) throws EdsDbException;
    MongoCollection<Document> clone(String source, String dest) throws EdsDbException;
    Date getLastSync(String name) throws EdsDbException;
    void sync(String name, Date sync) throws EdsDbException;
    long countActiveDocuments(MongoCollection<Document> src) throws EdsDbException;
    long countActiveDocuments(String src) throws EdsDbException;
    List<ObjectId> getActiveDocumentsId(String src) throws EdsDbException;
}
