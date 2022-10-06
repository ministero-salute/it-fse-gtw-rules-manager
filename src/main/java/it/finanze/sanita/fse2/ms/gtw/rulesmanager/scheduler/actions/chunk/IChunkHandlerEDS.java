package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import org.bson.Document;

import java.util.AbstractMap.SimpleImmutableEntry;

@FunctionalInterface
public interface IChunkHandlerEDS {
    SimpleImmutableEntry<ActionRes, Integer> handle(MongoCollection<Document> staging, int idx);
}
