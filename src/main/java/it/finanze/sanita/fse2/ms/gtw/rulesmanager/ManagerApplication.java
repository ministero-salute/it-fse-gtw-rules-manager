package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.properties.EDSClientCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ManagerApplication {

	@Autowired
	private EDSClientCFG edsClientCFG;

	public static void main(String[] args) {
		SpringApplication.run(ManagerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplateEds() {

		RestTemplate restTemplate = null;
		if (Boolean.FALSE.equals(edsClientCFG.isDevMode())) {
			
			try (FileInputStream fis = new FileInputStream(edsClientCFG.getKspath())) {
				
				KeyStore clientStore = KeyStore.getInstance("JKS");
				clientStore.load(fis, edsClientCFG.getKspwd().toCharArray());

				SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
				// CA Actalis and certificate
				sslContextBuilder.loadKeyMaterial(clientStore, edsClientCFG.getCertificatePwd().toCharArray());

				// Gateway certificate
				sslContextBuilder.loadTrustMaterial(new File(edsClientCFG.getTruststorePath()), edsClientCFG.getTruststorePwd().toCharArray());
				SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());

				HttpClientBuilder clientBuilder = HttpClientBuilder.create();
				clientBuilder.disableCookieManagement();

				CloseableHttpClient httpClient = clientBuilder.setSSLSocketFactory(sslConnectionSocketFactory).build();

				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
				requestFactory.setConnectTimeout(edsClientCFG.getConnectionTimeout());
				requestFactory.setReadTimeout(edsClientCFG.getReadTimeout());

				restTemplate = new RestTemplate(requestFactory);
			} catch (Exception e) {
				log.error("Exception while creating rest template to access EDS Client", e);
				throw new BusinessException(e);
			}
		} else {
			restTemplate = new RestTemplate();
		}

		return restTemplate;
	}
}
