package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import org.bson.Document;

@FunctionalInterface
public interface IActionHandlerEDS<T> {
    ActionRes handle(MongoCollection<Document> staging, T info);
}