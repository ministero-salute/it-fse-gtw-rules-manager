package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SchematronCFG extends ChangesetCFG {
    private static final String SCHEMA = "schematron";
    protected SchematronCFG(
        @Value("${eds.changeset.schematron.status}")
        String status,
        @Value("${eds.changeset.schematron.data}")
        String data
    ) {
        super(status, data, SCHEMA);
    }
}
