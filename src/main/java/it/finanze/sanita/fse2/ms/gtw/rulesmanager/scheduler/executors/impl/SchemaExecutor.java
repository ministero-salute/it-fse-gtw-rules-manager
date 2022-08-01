package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchemaCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.SchemaSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
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
public class SchemaExecutor extends ExecutorEDS<SchemaSetDTO> {

    @Autowired
    private SchemaQuery query;

    protected SchemaExecutor(SchemaCFG config, ExecutorBridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<SchemaSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<SchemaSetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onInsertion() {
        return (staging, info) -> {
            // Working var
            Optional<SchemaDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> client.getDocument(config, info.getId(), SchemaDTO.class), config, log);
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Insert
                    staging.insertOne(query.getUpsertQuery(dto.get()));
                }catch (MongoException ex) {
                    log.error(
                        format("[EDS][%s] Unable to insert document", config.getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[EDS][{}] Unable to retrieve document for insertion", config.getTitle());
            }
            // Bye, have a nice day
            return res;
        };
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onModification() {
        return (staging, info) -> {
            // Working var
            Optional<SchemaDTO> dto;
            // Retrieve data
            dto = retryOnException(() -> client.getDocument(config, info.getId(), SchemaDTO.class), config, log);
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Insert
                    staging.replaceOne(query.getFilterQuery(info.getId()), query.getUpsertQuery(dto.get()));
                }catch (MongoException ex) {
                    log.error(
                        format("[EDS][%s] Unable to modify document", config.getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[EDS][{}] Unable to retrieve document for modification", config.getTitle());
            }
            // Bye, have a nice day
            return res;
        };
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onDeletions() {
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
                    format("[EDS][%s] Unable to delete document", config.getTitle()),
                    ex
                );
            }
            return res;
        };
    }
}
