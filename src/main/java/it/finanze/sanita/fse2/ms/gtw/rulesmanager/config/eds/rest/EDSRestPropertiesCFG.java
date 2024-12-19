
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

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for EDS Client.
 * 
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
