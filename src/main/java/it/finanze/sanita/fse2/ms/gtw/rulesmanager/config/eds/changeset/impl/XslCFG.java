package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class XslCFG extends ChangesetCFG {
    private static final String SCHEMA = "xsl_transform";
    protected XslCFG(
        @Value("${eds.changeset.xsl.status}")
        String status,
        @Value("${eds.changeset.xsl.data}")
        String data
    ) {
        super(status, data, SCHEMA);
    }
}
