package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchematronCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.SchematronSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchematronQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.ExecutorBridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static java.lang.String.format;

@Slf4j
@Component
public class SchematronExecutor extends ExecutorEDS<SchematronSetDTO> {

    @Autowired
    private SchematronQuery query;

    protected SchematronExecutor(SchematronCFG config, ExecutorBridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<SchematronSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<SchematronSetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<SchematronSetDTO> onInsertion() {
        return (staging, info) -> {
            // Working var
            Optional<SchematronDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> client.getDocument(config, info.getId(), SchematronDTO.class), config, log);
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Insert
                    staging.insertOne(query.getUpsertQuery(dto.get()));
                }catch (MongoException ex) {
                    log.error(
                        format("[%s] Unable to insert document", config.getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[{}] Unable to retrieve document for insertion", config.getTitle());
            }
            // Bye, have a nice day
            return res;
        };
    }

    @Override
    public IActionHandlerEDS<SchematronSetDTO> onModification() {
        return (staging, info) -> {
            // Working var
            Optional<SchematronDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> client.getDocument(config, info.getId(), SchematronDTO.class), config, log);
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Insert
                    staging.replaceOne(query.getFilterQuery(info.getId()), query.getUpsertQuery(dto.get()));
                }catch (MongoException ex) {
                    log.error(
                        format("[%s] Unable to modify document", config.getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[{}] Unable to retrieve document for modification", config.getTitle());
            }
            // Bye, have a nice day
            return res;
        };
    }

    @Override
    public IActionHandlerEDS<SchematronSetDTO> onDeletions() {
        return (staging, info) -> {
            // Working var
            ActionRes res = KO;
            try {
                // Delete
                staging.deleteOne(query.getDeleteQuery(info.getId()));
                // Set the flag
                res = OK;
            }catch (MongoException ex) {
                log.error(
                    format("[%s] Unable to delete document", config.getTitle()),
                    ex
                );
            }
            return res;
        };
    }
}
