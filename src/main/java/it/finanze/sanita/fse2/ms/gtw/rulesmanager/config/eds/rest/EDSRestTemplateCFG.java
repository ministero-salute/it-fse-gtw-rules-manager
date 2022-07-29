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
