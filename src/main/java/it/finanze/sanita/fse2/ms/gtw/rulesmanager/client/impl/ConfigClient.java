package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.WhoIsResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of gtw-config Client.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@Component
public class ConfigClient implements IConfigClient {

    /**
     * Config host.
     */
    @Value("${ms.url.gtw-config}")
    private String configHost;

    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private ProfileUtility profileUtility;

    @Override
    public String getGatewayName() {
        String gatewayName;
        try {
            log.debug("Config Client - Calling Config Client to get Gateway Name");
            final String endpoint = configHost + "/v1/whois";

            final boolean isTestEnvironment = profileUtility.isDevOrDockerProfile() || profileUtility.isTestProfile();

            // Check if the endpoint is reachable
            if (isTestEnvironment && !isReachable()) {
                log.warn("Config Client - Config Client is not reachable, mocking for testing purpose");
                return Constants.AppConstants.MOCKED_GATEWAY_NAME;
            }

            final ResponseEntity<WhoIsResponseDTO> response = restTemplate.getForEntity(endpoint, WhoIsResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                WhoIsResponseDTO body = response.getBody();
                gatewayName = body != null && body.getGatewayName() != null ? body.getGatewayName() : null;
            } else {
                log.error("Config Client - Error calling Config Client to get Gateway Name");
                throw new BusinessException("The Config Client has returned an error");
            }
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("Error encountered while retrieving gateway name", e);
        }
        return gatewayName;
    }

    private boolean isReachable() {
        try {
            final String endpoint = configHost + "/status";
            restTemplate.getForEntity(endpoint, String.class);
            return true;
        } catch (ResourceAccessException clientException) {
            return false;
        }
    }

}
