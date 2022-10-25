package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionNaming {

    @Autowired
    private ProfileUtility profileUtility;

    @Bean("schemaBean")
    public String getSchemaCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.SCHEMA;
        }
        return Constants.Collections.SCHEMA;
    }

    @Bean("schematronBean")
    public String getSchematronCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.SCHEMATRON;
        }
        return Constants.Collections.SCHEMATRON;
    }

    @Bean("terminologyBean")
    public String getTerminologyCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.TERMINOLOGY;
        }
        return Constants.Collections.TERMINOLOGY;
    }

    @Bean("xslTransformBean")
    public String getXslTransformCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.XSL_TRANSFORM;
        }
        return Constants.Collections.XSL_TRANSFORM;
    }

    @Bean("structuresBean")
    public String getStructuresCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.FHIR_TRANSFORM;
        }
        return Constants.Collections.FHIR_TRANSFORM;
    }
    
    
}
