package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangeSetSpecCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SchematronCFG extends ChangeSetSpecCFG {
    protected SchematronCFG(
        @Value("${eds.changeset.schematron.status}")
        String status,
        @Value("${eds.changeset.schematron.data}")
        String data,
        @Value("${eds.changeset.schematron.name}")
        String schema
    ) {
        super(status, data, schema);
    }
}
