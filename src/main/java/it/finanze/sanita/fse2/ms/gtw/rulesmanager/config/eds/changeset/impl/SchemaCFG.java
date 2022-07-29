package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangeSetSpecCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SchemaCFG extends ChangeSetSpecCFG {
    protected SchemaCFG(
        @Value("${eds.changeset.schema.status}")
        String status,
        @Value("${eds.changeset.schema.data}")
        String data,
        @Value("${eds.changeset.schema.name}")
        String schema
    ) {
        super(status, data, schema);
    }
}
