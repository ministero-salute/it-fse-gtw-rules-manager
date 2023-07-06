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

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetChunkDTO.HistoryDeleteDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.TerminologyDTO;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.TerminologyDTO.ResourceMetaDTO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.TerminologyDTO.TerminologyItemDTO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.*;
import static java.lang.Integer.parseInt;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class TerminologyQuery {

    private Document getUpsertWhitelistItem(String resource, String version, ResourceMetaDTO meta) {
        return new org.bson.Document()
            .append(FIELD_SYSTEM, meta.getOid())
            .append(FIELD_VERSION, meta.getVersion())
            .append(FIELD_RELEASE_DATE, meta.getReleased())
            .append(FIELD_REF,
                new Document()
                    .append(Reference.FIELD_ID, parseInt(resource))
                    .append(Reference.FIELD_VERSION, parseInt(version))
            )
            .append(FIELD_WHITELIST, true);
    }

    private Document getUpsertQueryItem(String resource, String version, ResourceMetaDTO meta, TerminologyItemDTO item) {
        return new org.bson.Document()
            .append(FIELD_SYSTEM, meta.getOid())
            .append(FIELD_VERSION, meta.getVersion())
            .append(FIELD_CODE, item.getCode())
            .append(FIELD_DESCRIPTION, item.getDisplay())
            .append(FIELD_RELEASE_DATE, meta.getReleased())
            .append(FIELD_REF,
                new Document()
                    .append(Reference.FIELD_ID, parseInt(resource))
                    .append(Reference.FIELD_VERSION, parseInt(version))
            )
            .append(FIELD_WHITELIST, false);
    }

    private List<Document> getUpsertQueryList(String resource, String version, TerminologyDTO dto) {
        List<Document> docs = new ArrayList<>();
        for (TerminologyItemDTO item : dto.getItems()) {
            docs.add(getUpsertQueryItem(resource, version, dto.getMeta(), item));
        }
        return docs;
    }

    public List<Document> getUpsertQuery(TerminologyDTO dto) {
        List<Document> docs = new ArrayList<>();
        ResourceMetaDTO meta = dto.getMeta();
        if(meta.isWhitelist()) {
            docs.add(getUpsertWhitelistItem(dto.getResourceId(), dto.getVersionId(), meta));
        } else {
            docs = getUpsertQueryList(dto.getResourceId(), dto.getVersionId(), dto);
        }
        return docs;
    }

    public Bson getDeleteQuery(HistoryDeleteDTO data) {
        Query query = new Query(where(FIELD_REF_ID).is(parseInt(data.getId())));
        if(data.getOmit() != null) {
            query.addCriteria(
                where(FIELD_REF_VERSION).lt(parseInt(data.getOmit()))
            );
        }
        return query.getQueryObject();
    }
}
