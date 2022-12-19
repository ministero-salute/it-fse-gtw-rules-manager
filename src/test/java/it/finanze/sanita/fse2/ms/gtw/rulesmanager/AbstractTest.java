/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;


public abstract class AbstractTest {

    @MockBean
    RestTemplate restTemplate;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    void assertSchemaAreEquals(List<SchemaETY> actual, List<SchemaETY> expected) {

        for (int i = 0; i < actual.size(); i++) {
            assertTrue(actual.get(i).getNameSchema().equals(expected.get(i).getNameSchema()), String.format( "Actual Schema name is %s and expected is %s", actual.get(i).getNameSchema(), expected.get(i).getNameSchema()));
            assertTrue(actual.get(i).getTypeIdExtension().equals(expected.get(i).getTypeIdExtension()), String.format( "Actual schema version is %s and expected is %s", actual.get(i).getTypeIdExtension(), expected.get(i).getTypeIdExtension()));
            assertTrue((actual.get(i).getContentSchema() == null && expected.get(i).getContentSchema() == null)
                    || (actual.get(i).getContentSchema().equals(expected.get(i).getContentSchema())), "Content schema is different");
        }
    }

    void assertSchematronAreEquals(List<SchematronETY> actual, List<SchematronETY> expected) {

        for (int i = 0; i < actual.size(); i++) { 
            assertTrue(actual.get(i).getNameSchematron().equals(expected.get(i).getNameSchematron()), String.format( "Actual Schematron name is %s and expected is %s", actual.get(i).getNameSchematron(), expected.get(i).getNameSchematron()));
            assertTrue((actual.get(i).getContentSchematron() == null && expected.get(i).getContentSchematron() == null)
                    || (actual.get(i).getContentSchematron().equals(expected.get(i).getContentSchematron())), "Content schematron is different");
        }
    }

    void assertVocabulariesAreEquals(List<TerminologyETY> actual, List<TerminologyETY> expected) {
       
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(actual.get(i).getCode().equals(expected.get(i).getCode()), String.format("Actual Code is %s and expected is %s", actual.get(i).getCode(), expected.get(i).getCode()));
            assertTrue(actual.get(i).getDescription().equals(expected.get(i).getDescription()), String.format( "Actual Description is %s and expected is %s", actual.get(i).getDescription(), expected.get(i).getDescription()));
            assertTrue(actual.get(i).getSystem().equals(expected.get(i).getSystem()), "System is different");
        }
    }
    
    protected void dropVocabularyCollection() {
    	mongoTemplate.dropCollection(TerminologyETY.class);
    }
    
    protected void dropSchematronCollection() {
    	mongoTemplate.dropCollection(SchematronETY.class);
    }
    
    protected void dropSchemaCollection() {
    	mongoTemplate.dropCollection(SchemaETY.class);
    }
    
}
