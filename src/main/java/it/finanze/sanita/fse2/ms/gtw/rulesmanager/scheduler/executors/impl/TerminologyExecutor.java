package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.TerminologySetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.TerminologyQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TerminologyExecutor extends ExecutorEDS<TerminologySetDTO> {

    @Autowired
    private TerminologyQuery query;

    protected TerminologyExecutor(TerminologyCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<TerminologySetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<TerminologySetDTO>>() {};
    }

    @Override
    public IActionHandlerEDS<TerminologySetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, TerminologyDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<TerminologySetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }
}
