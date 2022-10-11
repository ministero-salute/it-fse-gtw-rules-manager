package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.FhirStructuresCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.FhirStructuresSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.FhirStructuresQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class FhirStructuresExecutors extends ExecutorEDS<FhirStructuresSetDTO> {

    @Autowired
    private FhirStructuresQuery query;
  

    protected FhirStructuresExecutors(FhirStructuresCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<FhirStructuresSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<FhirStructuresSetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<FhirStructuresSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, FhirStructuresDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<FhirStructuresSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }
}
