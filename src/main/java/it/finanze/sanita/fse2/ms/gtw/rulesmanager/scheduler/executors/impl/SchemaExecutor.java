package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchemaCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.SchemaSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchemaQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SchemaExecutor extends ExecutorEDS<SchemaSetDTO> {

    @Autowired
    private SchemaQuery query;

    protected SchemaExecutor(SchemaCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<SchemaSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<SchemaSetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, SchemaDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }
}
