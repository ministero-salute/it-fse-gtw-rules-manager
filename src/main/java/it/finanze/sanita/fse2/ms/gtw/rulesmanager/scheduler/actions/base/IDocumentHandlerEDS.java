package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base.BaseSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import org.bson.Document;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public interface IDocumentHandlerEDS<T> {
     IActionHandlerEDS<T> onInsertion();
     IActionHandlerEDS<T> onDeletions();

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