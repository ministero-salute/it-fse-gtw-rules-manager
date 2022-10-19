/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import org.bson.Document;

import java.util.AbstractMap.SimpleImmutableEntry;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public interface ISnapshotHandlerEDS {
     default IChunkHandlerEDS onChunkInsertion() {
          throw new UnsupportedOperationException("onChunkInsertion() is not implemented!");
     }
     default IChunkHandlerEDS onChunkDeletions() {
          throw new UnsupportedOperationException("onChunkDeletions() is not implemented!");
     }

     default int onInsertionChunkProcessing(MongoCollection<Document> mongo, ChangeSetChunkDTO changeset) {
          // Working var
          SimpleImmutableEntry<ActionRes, Integer> res = new SimpleImmutableEntry<>(OK, 0);
          int process = 0;
          String snapshot = changeset.getChunks().getSnapshotID();
          // Create evaluator instance
          IChunkHandlerEDS hnd = onChunkInsertion();
          // Retrieve chunk offset
          int chunks = changeset.getChunks().getInsertions().getChunksCount();
          // Process
          for (int i = 0; i < chunks && res.getKey() == OK; i++) {
               // Execute and get result
               res = onEvaluator(mongo, hnd, snapshot, i, chunks);
               // Apply conversion logic
               if(res.getKey() == OK) process += res.getValue();
          }
          // Return dataset
          return process;
     }
     default int onDeletionsChunkProcessing(MongoCollection<Document> mongo, ChangeSetChunkDTO changeset) {
          // Working var
          SimpleImmutableEntry<ActionRes, Integer> res = new SimpleImmutableEntry<>(OK, 0);
          int process = 0;
          String snapshot = changeset.getChunks().getSnapshotID();
          // Create evaluator instance
          IChunkHandlerEDS hnd = onChunkDeletions();
          // Retrieve chunk offset
          int chunks = changeset.getChunks().getDeletions().getChunksCount();
          // Process
          for (int i = 0; i < chunks && res.getKey() == OK; i++) {
               // Execute and get result
               res = onEvaluator(mongo, hnd, snapshot, i, chunks);
               // Apply conversion logic
               if(res.getKey() == OK) process += res.getValue();
          }
          // Return dataset
          return process;
     }
     default SimpleImmutableEntry<ActionRes, Integer> onEvaluator(
         MongoCollection<Document> mongo,
         IChunkHandlerEDS hnd,
         String snapshot,
         int chunk,
         int max
     ) {
          // Invoke handler
          return hnd.handle(mongo, snapshot, chunk, max);
     }
}
