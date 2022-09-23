package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IExecutableEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.StructureBase;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.impl.DefinitionExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.impl.MapExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.impl.ValuesetExecutor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.StructureValuesetETY.FIELD_LAST_UPDATE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryExecutorOnException;
import static java.lang.String.format;

@Slf4j
@Component
public class StructureExecutor implements IExecutableEDS {

    private static final String TITLE = "Structures";
    
    @Autowired
    private IStructureRepo repository;

    @Autowired
    private MapExecutor map;

    @Autowired
    private ValuesetExecutor valueset;

    @Autowired
    private DefinitionExecutor definition;

    @Override
    public ActionRes execute() {
        // Log me
        log.debug("[{}] Starting multi-onUpdate process", TITLE);
        // Iterate each executor and onUpdate them
        Map<ChangesetCFG, ActionRes> codes = onUpdate(map, valueset, definition);
        // Decode
        ActionRes res = onDecode(new ArrayList<>(codes.values()));
        // Act according to res
        switch (res){
            // At least one executor is waiting to onUpdate
            case OK:
                // Create document
                Document doc = createEmptyParentDoc();
                // Build document to onPush
                res = onMerge(codes, doc);
                // Check we are good to go
                if(res == OK) res = onPush(doc);
                break;
            // At least one executor is unable to onUpdate
            case KO:
                // Log me
                log.debug(
                    "[{}] One or more executors are unable to onUpdate",
                    TITLE
                );
                break;
            // No need to onUpdate any executor
            case EMPTY:
                res = onEmpty();
                break;
        }
        // Call clean on each executor
        clean(map, valueset, definition);
        // Log me
        log.debug("[{}] Ending multi-onUpdate process", TITLE);
        // Bye
        return res;
    }

    public Map<ChangesetCFG, ActionRes> onUpdate(StructureBase<?>...executors) {
        // Working var
        boolean quit = false;
        Map<ChangesetCFG, ActionRes> res = new HashMap<>();
        // Set to KO each of them
        for (ExecutorEDS<?> executor : executors) {
            res.put(executor.getConfig(), KO);
        }
        // Iterate each one
        for (int i = 0; i < executors.length && !quit; i++) {
            // Get key
            final ChangesetCFG key = executors[i].getConfig();
            // Get current executor
            final ExecutorEDS<?> current = executors[i];
            // Execute the processing without swap
            res.put(key, retryExecutorOnException(current, log));
            // Log if anything wrong
            if(res.get(key) == KO) {
                // Log me
                log.debug(
                    "[{}] Unable to process {} executor ",
                    TITLE,
                    executors[i].getConfig().getTitle()
                );
                // Mark as quit
                quit = true;
            }
        }
        return res;
    }

    public ActionRes onDecode(List<ActionRes> res) {
        // Working var
        ActionRes map;
        // Check if any KO
        map = res.stream().filter(i -> i == KO).findFirst().orElse(OK);
        // There is no KO
        if(map == OK) {
            // Now check if all three of them are onEmpty flags
            long size = res.stream().filter(i -> i == EMPTY).count();
            // Verify we got as many as onEmpty flags as the executors count
            if (size == res.size()) map = EMPTY;
        }
        // If every other check failed, it's going to return OK
        return map;
    }

    public ActionRes onEmpty() {
        // Log me
        log.debug(
            "[{}] There are no documents to modify",
            TITLE
        );
        // Working var
        ActionRes res = KO;
        try {
            // Case #1
            // No documents inside, just create one empty
            if(repository.isEmpty(map.getConfig().getParent())) {
                repository.createEmptyDocOnParent(
                    map.getConfig().getParent(),
                    map.getConfig().getProduction(),
                    definition.getConfig().getProduction(),
                    valueset.getConfig().getProduction()
                );
                // Log me
                log.debug(
                    "[{}] No previous documents were available, created an empty one",
                    TITLE
                );
            }else {
                // Case #2
                // At least document inside, just update the last_update_date
                repository.updateDueToEmptyOnParent(map.getConfig().getParent());
                // Log me
                log.debug(
                    "[{}] Latest document has been updated",
                    TITLE
                );
            }
            // Mark as done
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to verify collection emptiness", TITLE),
                ex
            );
        }
        // Bye
        return res;
    }

    public ActionRes onMerge(Map<ChangesetCFG, ActionRes> codes, Document document) {
        // Log me
        log.debug("[{}] Merging changes", TITLE);
        // Working var
        ActionRes res = KO;
        try {
            for (Entry<ChangesetCFG, ActionRes> entry : codes.entrySet()) {
                // Props
                ActionRes status = entry.getValue();
                String target = entry.getKey().getParent();
                String staging = entry.getKey().getStaging();
                String field = entry.getKey().getProduction();
                // Switch operation according to status code
                switch (status) {
                    case OK:
                        // Log me
                        log.debug(
                            "[{}] Retrieving changes from staging for {}",
                            TITLE,
                            field
                        );
                        // Retrieve document from staging and append
                        document = document.append(field, repository.readFromStagingDoc(staging));
                        break;
                    case EMPTY:
                        // Log me
                        log.debug(
                            "[{}] Retrieving changes from latest document for {}",
                            TITLE,
                            field
                        );
                        // Retrieve document from parent (if available otherwise empty list) and append
                        document = document.append(field, repository.readFromLatestDoc(
                            target,
                            field
                        ));
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected status code: " + status);
                }
            }
            // Set ok
            res = OK;
        }catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to organise data into one document", TITLE),
                ex
            );
        }
        // Bye
        return res;
    }

    public ActionRes onPush(Document doc) {
        // Log me
        log.debug("[{}] Pushing new changes", TITLE);
        // Working var
        ActionRes res = KO;
        try {
            // Insert
            repository.insertInto(map.getConfig().getParent(), doc);
            // Mark
            res = OK;
        }catch (EdsDbException ex) {
            // Log me
            log.error(
                format("[%s] Unable to push document to collection", TITLE),
                ex
            );
        }
        return res;
    }

    private void clean(StructureBase<?>...executors) {
        for (StructureBase<?> executor : executors) {
            // Remove staging repo
            ActionRes res = executor.clean();
            // Log
            if(res == OK) {
                // Log me
                log.debug(
                    "[{}] Removing staging repository: {}",
                    TITLE,
                    executor.getConfig().getStaging()
                );
            } else if(res == KO) {
                // Log me
                log.debug(
                    "[{}] Unable to remove staging repository: {}",
                    TITLE,
                    executor.getConfig().getStaging()
                );
            }
        }
    }

    private Document createEmptyParentDoc() {
        // Create document to insert
        Document doc = new Document();
        // Append last update
        doc = doc.append(FIELD_LAST_UPDATE, new Date());
        // Return
        return doc;
    }

}
