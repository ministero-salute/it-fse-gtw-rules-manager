package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangeSetSpecCFG;
import org.springframework.stereotype.Component;

@Component
public class MockConfig extends ChangeSetSpecCFG {
    protected MockConfig() {
        super("https://127.0.0.1/status", "https://127.0.0.1/data/", "mock");
    }
}