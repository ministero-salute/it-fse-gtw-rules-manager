package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;


@Configuration
@Getter
public class FhirStructuresCFG extends ChangesetCFG {
    private static final String FHIR_STRUCTURES = "fhir_structures";
    
    protected FhirStructuresCFG (
        @Value("${eds.changeset.fhir.status}")
        String status,
        @Value("${eds.changeset.fhir.data}")
        String data
    ) {
        super(status, data, FHIR_STRUCTURES);
    }
}
