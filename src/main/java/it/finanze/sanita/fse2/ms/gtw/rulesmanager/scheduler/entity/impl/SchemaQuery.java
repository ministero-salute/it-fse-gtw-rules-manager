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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY.*;

@Component
public class SchemaQuery implements IQueryEDS<SchemaDTO> {

    /**
     * Used by the executor to upsert the dto instance
     *
     * @param dto The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    @Override
    public Document getUpsertQuery(SchemaDTO dto) {
        // Get data
        SchemaDTO.Schema schema = dto.getDocument();
        // Create
        return new org.bson.Document()
            .append(FIELD_ID, new ObjectId(schema.getId()))
            .append(FIELD_FILENAME, schema.getNameSchema())
            .append(FIELD_CONTENT, new Binary(StringUtility.decodeBase64(schema.getContentSchema())))
            .append(FIELD_TYPE_ID_EXT, schema.getTypeIdExtension())
            .append(FIELD_ROOT_SCHEMA, schema.getRootSchema())
            .append(FIELD_LAST_UPDATE, schema.getLastUpdateDate())
            .append(FIELD_DELETED, false);
    }

    /**
     * Used by the executor to find a given dto
     *
     * @param id The retrieved id from {@code client.getDocument()}
     * @return The query to find a given dto
     */
    @Override
    public Document getFilterQuery(String id) {
        return new org.bson.Document().append(FIELD_ID, new ObjectId(id));
    }

    /**
     * Used by the executor to find and delete a given dto
     *
     * @param id The retrieved id from {@code client.getDocument()}
     * @return The query to find and delete a given dto
     */
    @Override
    public Document getDeleteQuery(String id) {
        return getFilterQuery(id);
    }

    /**
     * Used mainly for testing purpose to deep compare documents among cloned collections
     * @param doc The document retrieved from the collection
     * @return The query to deeply compare a given document into another collection
     */
    @Override
    public Document getComparatorQuery(Document doc) {
        return new org.bson.Document()
            .append(FIELD_ID, doc.getObjectId(FIELD_ID))
            .append(FIELD_FILENAME, doc.getString(FIELD_FILENAME))
            .append(FIELD_CONTENT, doc.get(FIELD_CONTENT, Binary.class))
            .append(FIELD_TYPE_ID_EXT, doc.getString(FIELD_TYPE_ID_EXT))
            .append(FIELD_ROOT_SCHEMA, doc.getBoolean(FIELD_ROOT_SCHEMA))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
            .append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC))
            .append(FIELD_DELETED, doc.getBoolean(FIELD_DELETED)); 
    }
}
