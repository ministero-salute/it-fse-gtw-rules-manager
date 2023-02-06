package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.sub.EngineMap;
import org.bson.Document;

import java.util.List;

public interface IEngineRepo {
    void insert(MongoCollection<Document> src, Document engine) throws EdsDbException;
    List<EngineMap> getActiveMaps(String fhir) throws EdsDbException;
}
