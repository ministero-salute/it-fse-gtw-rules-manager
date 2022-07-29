package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;

@Configuration
public class CollectionNaming {

    @Autowired
    private ProfileUtility profileUtility;

    @Bean("schemaBean")
    public String getSchemaCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.SCHEMA;
        }
        return Constants.ComponentScan.Collections.SCHEMA;
    }

    @Bean("schematronBean")
    public String getSchematronCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.SCHEMATRON;
        }
        return Constants.ComponentScan.Collections.SCHEMATRON;
    }

    @Bean("terminologyBean")
    public String getTerminologyCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.TERMINOLOGY;
        }
        return Constants.ComponentScan.Collections.TERMINOLOGY;
    }

    @Bean("xslTransformBean")
    public String getXslTransformCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.XSL_TRANSFORM;
        }
        return Constants.ComponentScan.Collections.XSL_TRANSFORM;
    }
    
    @Bean("structureDefinitionBean")
    public String getStructureDefinitionCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.STRUCTURE_DEFINITION;
        }
        return Constants.ComponentScan.Collections.STRUCTURE_DEFINITION;
    }
    
    @Bean("structureMapBean")
    public String getStructureMapCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.STRUCTURE_MAP;
        }
        return Constants.ComponentScan.Collections.STRUCTURE_MAP;
    }
    
    @Bean("valuesetBean")
    public String getValusetCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.ComponentScan.Collections.VALUSET;
        }
        return Constants.ComponentScan.Collections.VALUSET;
    }
    
    
    
    
}
