
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

import lombok.SneakyThrows;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class EDSRestTemplateCFG {

    @Autowired
    private EDSRestPropertiesCFG config;

    @ConditionalOnProperty(name="eds.rest.secured", havingValue="false")
    @Bean
    public RestTemplate createRestTemplateForDev() {
        return new RestTemplate();
    }

    @SneakyThrows
    @Bean
    @ConditionalOnProperty(name="eds.rest.secured", havingValue="true")
    public RestTemplate createRestTemplateForProd() {

        RestTemplate restTemplate;

        try (FileInputStream fis = new FileInputStream(config.getKspath())) {

            KeyStore clientStore = KeyStore.getInstance("JKS");
            clientStore.load(fis, config.getKspwd().toCharArray());

            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            // CA and certificate
            sslContextBuilder.loadKeyMaterial(clientStore, config.getCertificatePwd().toCharArray());
            // Gateway certificate
            sslContextBuilder.loadTrustMaterial(new File(
                config.getTruststorePath()), config.getTruststorePwd().toCharArray()
            );
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContextBuilder.build()
            );

            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
            clientBuilder.disableCookieManagement();

            CloseableHttpClient httpClient = clientBuilder.setSSLSocketFactory(sslConnectionSocketFactory).build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            requestFactory.setConnectTimeout(config.getConnectionTimeout());
            requestFactory.setReadTimeout(config.getReadTimeout());

            restTemplate = new RestTemplate(requestFactory);
        }

        return restTemplate;
    }
}
