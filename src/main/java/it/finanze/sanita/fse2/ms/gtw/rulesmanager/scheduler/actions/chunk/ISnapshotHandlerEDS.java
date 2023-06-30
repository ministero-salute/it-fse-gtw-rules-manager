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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryDeleteDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryInsertDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import org.bson.Document;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public interface ISnapshotHandlerEDS {
     default IChunkHandlerEDS<HistoryInsertDTO> onChunkInsertion() {
          throw new UnsupportedOperationException("onChunkInsertion() is not implemented!");
     }
     default IChunkHandlerEDS<HistoryDeleteDTO> onChunkDeletions() {
          throw new UnsupportedOperationException("onChunkDeletions() is not implemented!");
     }

     default int onResourceInsert(MongoCollection<Document> mongo, ChangeSetChunkDTO changeset) {
          // Working var
          SimpleImmutableEntry<ActionRes, Integer> res = new SimpleImmutableEntry<>(OK, 0);
          int process = 0;
          List<HistoryInsertDTO> resources = changeset.getInsertions();
          // Create evaluator instance
          IChunkHandlerEDS<HistoryInsertDTO> hnd = onChunkInsertion();
          // Retrieve chunk offset
          int size = changeset.getInsertions().size();
          // Process
          for (int i = 0; i < size && res.getKey() == OK; i++) {
               // Execute and get result
               res = onInsertEvaluator(mongo, hnd, resources.get(i));
               // Apply conversion logic
               if(res.getKey() == OK) process += res.getValue();
          }
          // Return dataset
          return process;
     }
     default int onResourceDelete(MongoCollection<Document> mongo, ChangeSetChunkDTO changeset) {
          // Working var
          SimpleImmutableEntry<ActionRes, Integer> res = new SimpleImmutableEntry<>(OK, 0);
          int process = 0;
          List<HistoryDeleteDTO> resources = changeset.getDeletions();
          // Create evaluator instance
          IChunkHandlerEDS<HistoryDeleteDTO> hnd = onChunkDeletions();
          // Retrieve chunk offset
          int size = changeset.getDeletions().size();
          // Process
          for (int i = 0; i < size && res.getKey() == OK; i++) {
               // Execute and get result
               res = onDeleteEvaluator(mongo, hnd, resources.get(i));
               // Apply conversion logic
               if(res.getKey() == OK) process += res.getValue();
          }
          // Return dataset
          return process;
     }

     default SimpleImmutableEntry<ActionRes, Integer> onInsertEvaluator(
         MongoCollection<Document> mongo,
         IChunkHandlerEDS<HistoryInsertDTO> hnd,
         HistoryInsertDTO data
     ) {
          // Invoke handler
          return hnd.handle(mongo, data);
     }

     default SimpleImmutableEntry<ActionRes, Integer> onDeleteEvaluator(
         MongoCollection<Document> mongo,
         IChunkHandlerEDS<HistoryDeleteDTO> hnd,
         HistoryDeleteDTO data
     ) {
          // Invoke handler
          return hnd.handle(mongo, data);
     }
}
