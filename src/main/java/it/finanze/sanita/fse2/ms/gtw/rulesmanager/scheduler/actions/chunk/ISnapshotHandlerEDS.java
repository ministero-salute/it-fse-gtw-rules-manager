
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
