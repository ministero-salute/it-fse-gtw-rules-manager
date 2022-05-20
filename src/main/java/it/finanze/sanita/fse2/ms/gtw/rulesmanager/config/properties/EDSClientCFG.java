package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Configuration properties for EDS Client.
 * 
 * @author Simone Lungarella
 */
@Getter
@Component
public class EDSClientCFG {

    /**
     * URL to call EDS Client.
     */
    @Value("${eds.url-config-items}")
    private String urlConfigItems;

    /**
     * Key store path.
     */
    @Value("${eds.kspath}")
    private String kspath;

    /**
     * Key store password.
     */
    @Value("${eds.kspwd}")
    private String kspwd;

    /**
     * Connection timeout in millis.
     */
    @Value("${eds.connection-timeout}")
    private Integer connectionTimeout;

    /**
     * Read timeout in millis.
     */
    @Value("${eds.read-timeout}")
    private Integer readTimeout;

    /**
     * Certificate password.
     */
    @Value("${eds.certificate-pwd}")
    private String certificatePwd;

    /**
     * Trust store path.
     */
    @Value("${eds.truststore-path}")
    private String truststorePath;

    /**
     * Trust store password.
     */
    @Value("${eds.truststore-pwd}")
    private String truststorePwd;

    @Value("${eds.development-mode}")
    private boolean devMode;
}
