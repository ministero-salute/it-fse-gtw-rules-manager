package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl.EDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.EDSClientException;
import lombok.extern.slf4j.Slf4j;

/**
 * Test class for EDS Client.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
class EDSClientTest extends AbstractTest {
    
	@Autowired
	private EDSClient edsClient;
	
    @BeforeEach
    void setup() {
        // This make the service calls the real eds client
        given(edsClientCFG.isDevMode()).willReturn(false);
        given(edsClientCFG.getUrlConfigItems()).willReturn("does-not-matter");
    }

    @Test
    void testGetConfigurationItems() {
        log.info("Executing test to get configuration items");
        ConfigItemDTO configurationItems = getMockedItems();
        mockEDSClient(HttpStatus.OK, configurationItems, null);

        ConfigItemDTO response = edsClient.getConfigurationItems();
        assertNotNull(response, "Response should not be null");
        
        assertFalse(CollectionUtils.isEmpty(response.getSchema().getSchemaChildEntryDTO()));
        assertFalse(CollectionUtils.isEmpty(response.getVocabulary().get(0).getEntryDTO()));

        assertTrue(configurationItems.getSchema().getSchemaChildEntryDTO().size() == response.getSchema().getSchemaChildEntryDTO().size());
        assertTrue(configurationItems.getVocabulary().get(0).getEntryDTO().size() == response.getVocabulary().get(0).getEntryDTO().size());
     }
    
    @Test
    void testBadRequest() {
        log.info("Executing test to get configuration items with bad request");
        ConfigItemDTO configurationItems = getMockedItems();
        HttpClientErrorException httpExc = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        mockEDSClient(HttpStatus.BAD_REQUEST, configurationItems, httpExc);
        assertThrows(HttpClientErrorException.class, () -> edsClient.getConfigurationItems());
    }

    @Test
    void testNullBody() {
        log.info("Executing test to get configuration items with null body");
        mockEDSClient(HttpStatus.OK, null, null);
        assertThrows(EDSClientException.class, () -> edsClient.getConfigurationItems());
    }
     
}
