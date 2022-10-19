/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import org.bson.Document;

import java.util.Date;
import java.util.List;

public interface IStructureRepo {
    void insertInto(String source, Document doc) throws EdsDbException;
    List<Document> readFromLatestDoc(String source, String field) throws EdsDbException;
    List<Document> readFromStagingDoc(String staging) throws EdsDbException;
    MongoCollection<Document> cloneFromParent(String source, String dest, String field) throws EdsDbException;
    Date getLastSyncFromParent(String name, String field) throws EdsDbException;
    void createEmptyDocOnParent(String name, String ...fields) throws EdsDbException;
    void updateDueToEmptyOnParent(String name) throws EdsDbException;
    boolean isEmpty(String name) throws EdsDbException;
}
