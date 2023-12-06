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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.API_CONFIG_ITEMS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.API_PROPS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.API_STATUS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.API_WHOIS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.IDENTIFIER_MS;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.IDENTIFIER;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.API_VERSION;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.QP_TYPE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.QP_PROPS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.MicroservicesURLCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ConfigItemTypeEnum;


@Component
public final class ConfigClientRoutes {

    @Autowired
    private MicroservicesURLCFG msUrlCFG;

    public UriComponentsBuilder base() {
        return UriComponentsBuilder.fromHttpUrl(msUrlCFG.getConfigHost());
    }

    public String identifier() {
        return IDENTIFIER;
    }

    public String microservice() {
        return IDENTIFIER_MS;
    }

    public String status() {
        return base()
            .pathSegment(API_STATUS)
            .build()
            .toUriString();
    }

    public String whois() {
        return base()
            .pathSegment(API_WHOIS)
            .build()
            .toUriString();
    }

    public String getConfigItem(ConfigItemTypeEnum type, String props) {
        return base()
            .pathSegment(API_VERSION, API_CONFIG_ITEMS, API_PROPS)
            .queryParam(QP_TYPE, type.name())
            .queryParam(QP_PROPS, props)
            .build()
            .toUriString();
    }

}
