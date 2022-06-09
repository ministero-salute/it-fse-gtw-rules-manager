package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchemaEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ISchemaRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ISchematronRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IXslTransformRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = { Constants.ComponentScan.BASE })
@ActiveProfiles(Constants.Profile.TEST)
class FirstUserStory extends AbstractTest {

    @Autowired
    private InvokeEDSClientScheduler invokeEDSClientScheduler;

    @Autowired
    private ISchemaRepo schemaRepo;

    @Autowired
    private ISchematronRepo schematronRepo;

    @Autowired
    private IXslTransformRepo xslTransformRepo;

    @Autowired
    private ITerminologyRepo vocabularyRepo;
    
    @Autowired
    private MongoTemplate mongoTemplate;
     
    @BeforeEach
    void setup() {
        // This make the service calls the real eds client
        given(edsClientCFG.isDevMode()).willReturn(false);
        given(edsClientCFG.getUrlConfigItems()).willReturn("does-not-matter");

        log.info("Dropping tables...");
        dropSchemaCollection();
        dropSchematronCollection();
        dropXslTransformCollection();
        dropVocabularyCollection();
    }
 

    private void assertItemsArePersisted(ConfigItemDTO configurationItems) {
        List<SchemaETY> schemas = schemaRepo.findAll().stream().filter(e-> !Boolean.TRUE.equals(e.getRootSchema())).collect(Collectors.toList());
        List<TerminologyETY> vocabularies = vocabularyRepo.findAll();

        assertTrue(configurationItems.getSchema().getSchemaChildEntryDTO().size() == schemas.size());
        //+8 sono item in più aggiungi per il cda da validare
        assertTrue(configurationItems.getVocabulary().get(0).getEntryDTO().size()+8  == vocabularies.size());

        List<SchemaETY> toCompare = new ArrayList<>();
        for(SchemaEntryDTO entryDTO : configurationItems.getSchema().getSchemaChildEntryDTO()) {
        	SchemaETY schema = new SchemaETY();
        	schema.setContentSchema(entryDTO.getContentSchema());
        	schema.setNameSchema(entryDTO.getNameSchema());
        	schema.setTypeIdExtension(configurationItems.getSchema().getTypeIdExtension());
        	toCompare.add(schema);
        }
        assertSchemaAreEquals(toCompare , schemas); 
    }
    
    @Test
    void userStoryTest() {
        log.info("Testing User Story 1...");
        ConfigItemDTO configurationItems = getMockedItems();
        mockEDSClient(HttpStatus.OK, configurationItems, null);
        invokeEDSClientScheduler.run();
        assertItemsArePersisted(configurationItems);
        
        Integer schemaCounter = counterCollection("schema");
        Integer schematronCounter = counterCollection("schematron");
        Integer vocabularyCounter = counterCollection("vocabulary");
        Integer xslTransformCounter = counterCollection("xsl_transform");
         
        invokeEDSClientScheduler.run();
        
        assertEquals(schemaCounter, counterCollection("schema"));
        assertEquals(schematronCounter, counterCollection("schematron"));
        assertEquals(vocabularyCounter, counterCollection("vocabulary"));
        assertEquals(xslTransformCounter, counterCollection("xsl_transform"));
        
    }

    private Integer counterCollection(String collection) {
    	Integer output = 0;
    	output = (int) mongoTemplate.count(new Query(), collection);
    	return output;
    }
}
