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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.ConfigClientRoutes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.WhoIsResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of gtw-config Client.
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
	private ConfigClientRoutes routes;

    @Autowired
    private RestTemplate restTemplate;

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

    @Override
	public Object getProps(ConfigItemTypeEnum type, String props, Object previous) {
	    Object out = previous;

	    String endpoint = routes.getConfigItem(type, props);

	    if (isReachable()) {
	        Object response = restTemplate.getForObject(endpoint, Object.class);
	        out = convertResponse(response, previous);
	    }

	    return out;
	}

	@SuppressWarnings("unchecked")
	private <T> T convertResponse(Object response, Object previous) {
	    try {
	        Class<T> targetType = (Class<T>) previous.getClass();

	        if (targetType == Integer.class) {
	            return (T) Integer.valueOf(response.toString());
	        } else if (targetType == Boolean.class) {
	            return (T) Boolean.valueOf(response.toString());
	        } else if (targetType == String.class) {
	            return (T) response.toString();
	        } else {
	            return (T) response;
	        }
	    } catch (Exception e) {
	        return null;
	    }
	}

	@Override
	public ConfigItemDTO getConfigurationItems(ConfigItemTypeEnum type) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(routes.base() + "/config-items").queryParam("type", type); 
		return restTemplate.getForObject(builder.toUriString(), ConfigItemDTO.class);
	}
}
