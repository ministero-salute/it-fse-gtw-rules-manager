/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Collections.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST_PREFIX;

@Configuration
public class CollectionNaming {

    @Autowired
    private ProfileUtility profiles;

    @Bean("schemaBean")
    public String getSchemaCollection() {
        if (profiles.isTestProfile()) {
            return TEST_PREFIX + SCHEMA;
        }
        return SCHEMA;
    }

    @Bean("schematronBean")
    public String getSchematronCollection() {
        if (profiles.isTestProfile()) {
            return TEST_PREFIX + SCHEMATRON;
        }
        return SCHEMATRON;
    }

    @Bean("terminologyBean")
    public String getTerminologyCollection() {
        if (profiles.isTestProfile()) {
            return TEST_PREFIX + TERMINOLOGY;
        }
        return TERMINOLOGY;
    }

    @Bean("structuresBean")
    public String getStructuresCollection() {
        if (profiles.isTestProfile()) {
            return TEST_PREFIX + FHIR_TRANSFORM;
        }
        return FHIR_TRANSFORM;
    }

    @Bean("dictionaryBean")
    public String getDictionaryCollection() {
        if (profiles.isTestProfile()) {
            return TEST_PREFIX + DICTIONARY;
        }
        return DICTIONARY;
    }

    @Bean("engineBean")
    public String getEngineCollection() {
        if (profiles.isTestProfile()) {
            return TEST_PREFIX + ENGINES;
        }
        return ENGINES;
    }

}
