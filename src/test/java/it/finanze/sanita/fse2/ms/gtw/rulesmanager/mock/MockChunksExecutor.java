package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.cfg.MockChunksConfig;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.TermsExecutor;
import org.springframework.stereotype.Component;

@Component
public class MockChunksExecutor extends TermsExecutor {
    protected MockChunksExecutor(MockChunksConfig config, BridgeEDS bridge) {
        super(config, bridge);
    }

}
