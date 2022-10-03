package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchematronCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.SchematronSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.SchematronQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchematronExecutor extends ExecutorEDS<SchematronSetDTO> {

    @Autowired
    private SchematronQuery query;

    protected SchematronExecutor(SchematronCFG config, BridgeEDS bridge, LoggerHelper loggerHelper) {
        super(config, bridge, loggerHelper);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<SchematronSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<SchematronSetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<SchematronSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, SchematronDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<SchematronSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }
}
