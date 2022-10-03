package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents.MapCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.MapSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.MapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl.MapQuery;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.HandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.StructureBase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MapExecutor extends StructureBase<MapSetDTO> {

    @Autowired
    private IStructureRepo parent;

    @Autowired
    private MapQuery query;

    protected MapExecutor(MapCFG config, BridgeEDS bridge, LoggerHelper loggerHelper) {
        super(config, bridge, loggerHelper);
    }

    @Override
    public IActionHandlerEDS<MapSetDTO> onInsertion() {
        return HandlerEDS.onInsertion(log, MapDTO.class, getBridge().getClient(), query, getConfig());
    }

    @Override
    public IActionHandlerEDS<MapSetDTO> onDeletions() {
        return HandlerEDS.onDeletions(log, query, getConfig());
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<MapSetDTO>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<MapSetDTO>>() {};
    }

    @Override
    protected IStructureRepo getParent() {
        return parent;
    }
}
