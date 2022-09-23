package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TerminologyCFG extends ChangesetCFG {
    private static final String SCHEMA = "terminology";
    protected TerminologyCFG(
        @Value("${eds.changeset.terminology.status}")
        String status,
        @Value("${eds.changeset.terminology.data}")
        String data
    ) {
        super(status, data, SCHEMA);
    }
}
