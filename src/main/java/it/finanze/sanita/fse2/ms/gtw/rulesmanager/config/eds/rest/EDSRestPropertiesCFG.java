/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.rest;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for EDS Client.
 * 
 * @author Simone Lungarella
 */
@Getter
@Component
public class EDSRestPropertiesCFG {

    /**
     * Key store path.
     */
    @Value("${eds.rest.kspath}")
    private String kspath;

    /**
     * Key store password.
     */
    @Value("${eds.rest.kspwd}")
    private String kspwd;

    /**
     * Connection timeout in millis.
     */
    @Value("${eds.rest.connection-timeout}")
    private Integer connectionTimeout;

    /**
     * Read timeout in millis.
     */
    @Value("${eds.rest.read-timeout}")
    private Integer readTimeout;

    /**
     * Certificate password.
     */
    @Value("${eds.rest.certificate-pwd}")
    private String certificatePwd;

    /**
     * Trust store path.
     */
    @Value("${eds.rest.truststore-path}")
    private String truststorePath;

    /**
     * Trust store password.
     */
    @Value("${eds.rest.truststore-pwd}")
    private String truststorePwd;

    /**
     * Enable/disable security configuration.
     */
    @Value("${eds.rest.secured}")
    private Boolean secured;
}
