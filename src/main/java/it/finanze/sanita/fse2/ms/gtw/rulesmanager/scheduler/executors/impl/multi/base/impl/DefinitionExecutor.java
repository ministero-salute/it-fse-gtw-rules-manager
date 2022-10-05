package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents.DefinitionCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.DefinitionSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.DefinitionDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.DefinitionQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.StructureBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefinitionExecutor extends StructureBase<DefinitionSetDTO> {

    @Autowired
    private IStructureRepo parent;

    @Autowired
    private DefinitionQuery query;

    protected DefinitionExecutor(DefinitionCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    public IActionHandlerEDS<DefinitionSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, DefinitionDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<DefinitionSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<DefinitionSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<DefinitionSetDTO>>() {};
    }

    @Override
    protected IStructureRepo getParent() {
        return parent;
    }
}
