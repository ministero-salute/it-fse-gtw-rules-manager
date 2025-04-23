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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.rest;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;

@Configuration
public class EDSRestTemplateCFG {

	@Autowired
	private GovwayCfg govwayCfg;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		if(!StringUtility.isNullOrEmpty(govwayCfg.getGovwayUser()) && !StringUtility.isNullOrEmpty(govwayCfg.getGovwayPass())) {
			ClientHttpRequestInterceptor interceptor = new BasicAuthenticationInterceptor(govwayCfg.getGovwayUser(), govwayCfg.getGovwayPass());
			restTemplate.setInterceptors(Arrays.asList(interceptor));			
		}

		return restTemplate;
	}

}
