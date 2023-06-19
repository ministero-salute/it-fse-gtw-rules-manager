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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base.BaseSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import org.bson.Document;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public interface IDocumentHandlerEDS<T> {
     default IActionHandlerEDS<T> onInsertion() {
          throw new UnsupportedOperationException("onInsertion() is not implemented!");
     }
     default IActionHandlerEDS<T> onDeletions() {
          throw new UnsupportedOperationException("onDeletions() is not implemented!");
     }

     default int onInsertionProcessing(MongoCollection<Document> mongo, ChangeSetDTO<T> changeset) {
          // Working var
          ActionRes res = OK;
          int process = 0;
          // Create evaluator instance
          IActionHandlerEDS<T> hnd = onInsertion();
          // Retrieve query list
          List<BaseSetDTO<T>> queries = changeset.getInsertions();
          // Process
          for (int i = 0; i < queries.size() && res == OK; i++) {
               // Execute and get result
               res = onEvaluator(mongo, hnd, queries.get(i));
               // Apply conversion logic
               if(res == OK) process += 1;
          }
          // Return dataset
          return process;
     }
     default int onDeletionsProcessing(MongoCollection<Document> mongo, ChangeSetDTO<T> changeset) {
          // Working var
          ActionRes res = OK;
          int process = 0;
          // Create evaluator instance
          IActionHandlerEDS<T> hnd = onDeletions();
          // Retrieve query list
          List<BaseSetDTO<T>> queries = changeset.getDeletions();
          // Process
          for (int i = 0; i < queries.size() && res == OK; i++) {
               // Execute and get result
               res = onEvaluator(mongo, hnd, queries.get(i));
               // Apply conversion logic
               if(res == OK) process += 1;
          }
          // Return dataset
          return process;
     }
     default ActionRes onEvaluator(MongoCollection<Document> mongo, IActionHandlerEDS<T> hnd, BaseSetDTO<T> doc) {
          // Invoke handler
          return hnd.handle(mongo, doc);
     }
}
