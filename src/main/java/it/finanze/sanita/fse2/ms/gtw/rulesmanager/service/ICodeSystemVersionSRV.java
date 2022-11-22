/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import org.bson.Document;

public interface ICodeSystemVersionSRV {

    void syncCodeSystemVersions(String terminology, MongoCollection<Document> dictionary) throws EdsDbException;
    
}
