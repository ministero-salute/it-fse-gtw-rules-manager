package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.EDSClientCFG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IMockSRV;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTest {
 
    @MockBean
    EDSClientCFG edsClientCFG;

    @MockBean
    RestTemplate restTemplate;
    
    @Autowired
    IMockSRV mockSRV;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @SuppressWarnings("unchecked")
    void mockEDSClient(final HttpStatus status, final ConfigItemDTO bodyResponse, final HttpClientErrorException exception) {

        log.info("Executing mock to eds client to return a status {}", status);
        if (status.equals(HttpStatus.OK)) {
            ResponseEntity<ConfigItemDTO> response = new ResponseEntity<>(bodyResponse, status);
            given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).willReturn(response);

        } else {
            given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).willThrow(exception);
        }
    }

    ConfigItemDTO getMockedItems() {
    	return null;
//    	return mockSRV.mockConfigurationItem();
    }
    
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
//            assertTrue(actual.get(i).getXsdSchemaVersion().equals(expected.get(i).getXsdSchemaVersion()), String.format( "Actual schematron version is %s and expected is %s", actual.get(i).getXsdSchemaVersion(), expected.get(i).getXsdSchemaVersion()));
            assertTrue((actual.get(i).getContentSchematron() == null && expected.get(i).getContentSchematron() == null)
                    || (actual.get(i).getContentSchematron().equals(expected.get(i).getContentSchematron())), "Content schematron is different");
        }
    }

    void assertXsltAreEquals(List<XslTransformETY> actual, List<XslTransformETY> expected) {
        
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(actual.get(i).getNameXslTransform().equals(expected.get(i).getNameXslTransform()), String.format( "Actual Xsl name is %s and expected is %s", actual.get(i).getNameXslTransform(), expected.get(i).getNameXslTransform()));
            assertTrue(actual.get(i).getTemplateIdExtension().equals(expected.get(i).getTemplateIdExtension()), String.format( "Actual xsl transform version is %s and expected is %s", actual.get(i).getTemplateIdExtension(), expected.get(i).getTemplateIdExtension()));
            assertTrue((actual.get(i).getContentXslTransform() == null && expected.get(i).getContentXslTransform() == null)
                    || (actual.get(i).getContentXslTransform().equals(expected.get(i).getContentXslTransform())), "Content xsl transform is different");
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
    
    protected void dropXslTransformCollection() {
    	mongoTemplate.dropCollection(XslTransformETY.class);
    }
    
}
