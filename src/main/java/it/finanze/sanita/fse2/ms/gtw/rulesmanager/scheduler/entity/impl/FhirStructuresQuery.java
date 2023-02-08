/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;


import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TransformETY.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility.decodeBase64;

@Component
public class FhirStructuresQuery implements IQueryEDS<FhirStructuresDTO> {
	 /**
     * Used by the executor to upsert the dto instance
     *
     * @param str The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    @Override
    public Document getUpsertQuery(FhirStructuresDTO str) {
    	FhirStructuresDTO.Transform dto = str.getDocument();
    	Document doc = new Document();
		doc.put(FIELD_ID, new ObjectId(dto.getId()));
    	doc.put(FIELD_URI, dto.getUri());
        doc.put(FIELD_VERSION, dto.getVersion());
        if (dto.getTemplateIdRoot() != null) {
            doc.put(FIELD_TEMPLATE_ID_ROOT, dto.getTemplateIdRoot());
        }
        doc.put(FIELD_CONTENT, new Binary(decodeBase64(dto.getContent())));
        doc.put(FIELD_FILENAME, dto.getFilename());
    	doc.put(FIELD_TYPE, dto.getType());
    	doc.put(FIELD_LAST_UPDATE, dto.getLastUpdateDate());
    	doc.put(FIELD_DELETED, dto.getDeleted());
    	// Create
    	return doc;
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
     *
     * @param doc The document retrieved from the collection
     * @return The query to deeply compare a given document into another collection
     */
    @Override
    public Document getComparatorQuery(Document doc) {
        return new org.bson.Document()
            .append(FIELD_ID, doc.getObjectId(FIELD_ID))
            .append(FIELD_URI, doc.getString(FIELD_URI))
            .append(FIELD_VERSION, doc.getString(FIELD_VERSION))
            .append(FIELD_TEMPLATE_ID_ROOT, doc.getString(FIELD_TEMPLATE_ID_ROOT))
            .append(FIELD_CONTENT, doc.get(FIELD_CONTENT, Binary.class))
			.append(FIELD_FILENAME, doc.getString(FIELD_FILENAME))
            .append(FIELD_TYPE, doc.getString(FIELD_TYPE))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
			.append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC))
            .append(FIELD_DELETED, doc.getBoolean(FIELD_DELETED)); 
    }
}
