package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.FIELD_CODE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.FIELD_DESCRIPTION;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.FIELD_ID;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.FIELD_LAST_SYNC;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.FIELD_LAST_UPDATE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.FIELD_SYSTEM;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;

@Component
public class TerminologyQuery implements IQueryEDS<TerminologyDTO> {
    /**
     * Used by the executor to upsert the dto instance
     *
     * @param dto The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    @Override
    public Document getUpsertQuery(TerminologyDTO dto) {
        // Get data
        TerminologyDTO.Terminology terminology = dto.getDocument();
        // Create
        return new org.bson.Document()
            .append(FIELD_ID, new ObjectId(terminology.getId()))
            .append(FIELD_SYSTEM, terminology.getSystem())
            .append(FIELD_CODE, terminology.getCode())
            .append(FIELD_DESCRIPTION, terminology.getDescription())
            .append(FIELD_LAST_UPDATE, terminology.getLastUpdateDate());
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
            .append(FIELD_SYSTEM, doc.getString(FIELD_SYSTEM))
            .append(FIELD_CODE, doc.getString(FIELD_CODE))
            .append(FIELD_DESCRIPTION, doc.getString(FIELD_DESCRIPTION))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
            .append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC));
    }
}
