package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import org.bson.Document;

public interface IEngineSRV {
    boolean synthesize(String fhir, MongoCollection<Document> engines) throws EdsDbException;
}