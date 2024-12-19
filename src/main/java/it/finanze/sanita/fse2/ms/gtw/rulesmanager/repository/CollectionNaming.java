
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright (C) 2023 Ministero della Salute
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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
