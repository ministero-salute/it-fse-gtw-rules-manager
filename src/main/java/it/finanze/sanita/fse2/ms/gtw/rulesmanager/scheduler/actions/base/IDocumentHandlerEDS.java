package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import org.bson.Document;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public interface IDocumentHandlerEDS<T> {
     IActionHandlerEDS<T> onInsertion();
     IActionHandlerEDS<T> onModification();
     IActionHandlerEDS<T> onDeletions();

     default int onInsertionProcessing(MongoCollection<Document> mongo, ChangeSetDTO<T> changeset) {
          // Create evaluator instance
          IActionHandlerEDS<T> hnd = onInsertion();
          // Processing
          return changeset.getInsertions().stream()
              .map(doc -> onEvaluator(mongo, hnd, doc))
              .reduce(Integer::sum).orElse(0);
     }
     default int onModificationProcessing(MongoCollection<Document> mongo, ChangeSetDTO<T> changeset) {
          // Create evaluator instance
          IActionHandlerEDS<T> hnd = onModification();
          // Processing
          return changeset.getModifications().stream()
              .map(doc -> onEvaluator(mongo, hnd, doc))
              .reduce(Integer::sum).orElse(0);
     }
     default int onDeletionsProcessing(MongoCollection<Document> mongo, ChangeSetDTO<T> changeset) {
          // Create evaluator instance
          IActionHandlerEDS<T> hnd = onDeletions();
          // Processing
          return changeset.getDeletions().stream()
              .map(doc -> onEvaluator(mongo, hnd, doc))
              .reduce(Integer::sum).orElse(0);
     }
     default int onEvaluator(MongoCollection<Document> mongo, IActionHandlerEDS<T> hnd, T doc) {
          // Invoke handler
          return hnd.handle(mongo, doc) == OK ? 1 : 0;
     }
}
