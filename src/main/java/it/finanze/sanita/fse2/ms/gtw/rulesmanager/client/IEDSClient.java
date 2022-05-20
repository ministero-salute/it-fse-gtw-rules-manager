package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client;

import org.springframework.web.client.HttpClientErrorException;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.EDSClientException;

/**
 * Interface of EDS Client.
 * 
 * @author Simone Lungarella
 */
public interface IEDSClient {

    /**
     * Returns configuration items to use as validation data.
     * 
     * @return Configuration items.
     * @throws EDSClientException If the response from EDS Client has {@code null} body.
     * @throws HttpClientErrorException If an error occurred while calling EDS Client.
     */
    ConfigItemDTO getConfigurationItems();
}
