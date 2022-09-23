package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.DefinitionDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.StructureDefinitionETY.*;


@Component
public class DefinitionQuery implements IQueryEDS<DefinitionDTO> {

    /**
     * Used by the executor to upsert the dto instance
     *
     * @param dto The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    @Override
    public Document getUpsertQuery(DefinitionDTO dto) {
        // Get data
        DefinitionDTO.Definition definition = dto.getDocument();
        // Create
        return new Document()
            .append(FIELD_ID, new ObjectId(definition.getId()))
            .append(FIELD_NAME, definition.getNameDefinition())
            .append(FIELD_FILENAME, definition.getFilenameDefinition())
            .append(FIELD_CONTENT, new Binary(StringUtility.decodeBase64(definition.getContentDefinition())))
            .append(FIELD_VERSION, definition.getVersionDefinition())
            .append(FIELD_LAST_UPDATE, definition.getLastUpdateDate());
    }

    /**
     * Used by the executor to find a given dto
     *
     * @param id The retrieved id from {@code client.getDocument()}
     * @return The query to find a given dto
     */
    @Override
    public Document getFilterQuery(String id) {
        return new Document().append(FIELD_ID, new ObjectId(id));
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
        return new Document()
            .append(FIELD_ID, doc.getObjectId(FIELD_ID))
            .append(FIELD_NAME, doc.getString(FIELD_NAME))
            .append(FIELD_FILENAME, doc.getString(FIELD_FILENAME))
            .append(FIELD_CONTENT, doc.get(FIELD_CONTENT, Binary.class))
            .append(FIELD_VERSION, doc.getString(FIELD_VERSION))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
            .append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC));
    }
}
