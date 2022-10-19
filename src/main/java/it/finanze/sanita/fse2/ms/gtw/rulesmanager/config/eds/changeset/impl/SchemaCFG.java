/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SchemaCFG extends ChangesetCFG {
    private static final String SCHEMA = "schema";
    
    protected SchemaCFG(
        @Value("${eds.changeset.schema.status}")
        String status,
        @Value("${eds.changeset.schema.data}")
        String data
    ) {
        super(status, data, SCHEMA);
    }
}
