package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base.StructureBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
public class MockStructures extends StructureBase<MockData> {

//    @Autowired
    private IStructureRepo repository;

    protected MockStructures(MockConfig config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    public IActionHandlerEDS<MockData> onInsertion() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public IActionHandlerEDS<MockData> onDeletions() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<MockData>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<MockData>>() {};
    }

    @Override
    protected IStructureRepo getParent() {
        return this.repository;
    }
}
