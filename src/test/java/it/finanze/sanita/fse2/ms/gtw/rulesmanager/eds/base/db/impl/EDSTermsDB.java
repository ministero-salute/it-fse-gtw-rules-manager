
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.AbstractSchemaDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl.EDSTermsHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;

@Component
public class EDSTermsDB extends AbstractSchemaDB<TerminologyETY> {
    public EDSTermsDB(MongoTemplate mongo, EDSTermsHandler hnd, TerminologyCFG config) {
        super(mongo, hnd, config);
    }
}
