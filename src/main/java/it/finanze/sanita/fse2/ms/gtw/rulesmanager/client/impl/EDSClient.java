package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.properties.EDSClientCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.EDSClientException;
import lombok.extern.slf4j.Slf4j;

/**
 * Production implemention of EDS Client.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@Component
@Profile("!" + Constants.Profile.DEV)
public class EDSClient extends AbstractClient implements IEDSClient {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -1470125906483650945L;

	@Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EDSClientCFG edsClientCFG;

    @Override
    @CircuitBreaker(name = "configurationItems")
    public ConfigItemDTO getConfigurationItems() {
        log.info("Calling EDS Client to retrieve configuration items...");

        ConfigItemDTO configurationItems = null;
        ResponseEntity<ConfigItemDTO> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(headers);
            response = restTemplate.exchange(edsClientCFG.getUrlConfigItems(), HttpMethod.GET, entity, ConfigItemDTO.class);
            
            configurationItems = response.getBody();

            if (configurationItems == null) {
            	throw new EDSClientException(String.format("Error while calling EDS Client to retrieve configuration items: %s", response.getStatusCode()));
            }

            log.info("{} status returned from EDS Client", response.getStatusCode());
            
        } catch (HttpClientErrorException ex) {
            log.error(String.format("%s status returned from EDS Client", ex.getStatusCode()), ex);
            throw ex;
        } catch(EDSClientException edsE) {
            log.error("Error while calling EDS Client to retrieve configuration items, body is null");
            throw edsE;
        } catch (Exception ex) {
            log.error(String.format("Error encountered while calling EDS Client at url: %s to retrieve configuration items", edsClientCFG.getUrlConfigItems()), ex);
            throw new BusinessException(ex);
        }

        return configurationItems;
    }

}
