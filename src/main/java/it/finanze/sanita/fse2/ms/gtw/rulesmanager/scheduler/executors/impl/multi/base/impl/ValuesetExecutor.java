package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents.ValuesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.ValuesetSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.ValuesetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.ValuesetQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.StructureBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValuesetExecutor extends StructureBase<ValuesetSetDTO> {

    @Autowired
    private IStructureRepo parent;

    @Autowired
    private ValuesetQuery query;

    protected ValuesetExecutor(ValuesetCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    public IActionHandlerEDS<ValuesetSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, ValuesetDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<ValuesetSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<ValuesetSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<ValuesetSetDTO>>() {};
    }

    @Override
    protected IStructureRepo getParent() {
        return parent;
    }
}
