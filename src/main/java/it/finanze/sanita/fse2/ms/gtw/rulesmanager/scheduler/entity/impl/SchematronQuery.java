package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.*;

@Component
public class SchematronQuery implements IQueryEDS<SchematronDTO> {
    /**
     * Used by the executor to upsert the dto instance
     *
     * @param dto The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    @Override
    public Document getUpsertQuery(SchematronDTO dto) {
        // Get data
        SchematronDTO.Schematron schematron = dto.getDocument();
        // Create
        return new org.bson.Document()
            .append(FIELD_ID, new ObjectId(schematron.getId()))
            .append(FIELD_FILENAME, schematron.getNameSchematron())
            .append(FIELD_CONTENT, new Binary(StringUtility.decodeBase64(schematron.getContentSchematron())))
            .append(FIELD_TEMPLATE_ID_EXT, schematron.getTemplateIdExtension())
            .append(FIELD_ROOT, schematron.getTemplateIdRoot())
            .append(FIELD_LAST_UPDATE, schematron.getLastUpdateDate());
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
            .append(FIELD_FILENAME, doc.getString(FIELD_FILENAME))
            .append(FIELD_CONTENT, doc.get(FIELD_CONTENT, Binary.class))
            .append(FIELD_TEMPLATE_ID_EXT, doc.getString(FIELD_TEMPLATE_ID_EXT))
            .append(FIELD_ROOT, doc.getString(FIELD_ROOT))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
            .append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC));
    }
}