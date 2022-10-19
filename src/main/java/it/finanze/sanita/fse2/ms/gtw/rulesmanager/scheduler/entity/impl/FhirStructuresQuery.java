/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_CONTENT;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_FILENAME;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_ID;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_LAST_SYNC;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_LAST_UPDATE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_ROOT;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY.FIELD_DELETED;


import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;

@Component
public class FhirStructuresQuery implements IQueryEDS<FhirStructuresDTO> {
	 /**
     * Used by the executor to upsert the dto instance
     *
     * @param dto The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    @Override
    public Document getUpsertQuery(FhirStructuresDTO dto) {
    	
    	List<Document> mapsToSave = buildMapDocuments(dto.getMaps());
    	List<Document> definitionsToSave = buildDefinitionDocuments(dto.getDefinitions());
    	List<Document> valuesetsToSave =buildValuesetDocuments(dto.getValuesets());
    	Document doc = new Document();
    	doc.put("maps", mapsToSave);
    	doc.put("definitions", definitionsToSave);
    	doc.put("valuesets", valuesetsToSave);
    	doc.put("_id", new ObjectId(dto.getId()));
    	doc.put("template_id_root", dto.getTemplateIdRoot());
    	doc.put("last_update_date", dto.getLastUpdateDate());
    	doc.put("version", dto.getVersion());
    	doc.put("root_map", dto.getRootMap());
    	doc.put("deleted", dto.getDeleted()); 
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
            .append(FIELD_FILENAME, doc.getString(FIELD_FILENAME))
            .append(FIELD_CONTENT, doc.get(FIELD_CONTENT, Binary.class))
            .append("version", doc.getString("version"))
            .append(FIELD_ROOT, doc.getString(FIELD_ROOT))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
            .append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC))
            .append(FIELD_DELETED, doc.getBoolean(FIELD_DELETED)); 
    }
    
    private List<Document> buildMapDocuments(List<FhirStructuresDTO.FhirStructures> maps) {
    	List<Document> mapsToSave = new ArrayList<>();
    	if(maps!=null) {
    		for(FhirStructuresDTO.FhirStructures map : maps) {
    			Document doc = new Document();
    			doc.put("name_map", map.getNameMap());
    			doc.put("filename_map", map.getFilenameMap());
    			doc.put("content_map",new Binary(StringUtility.decodeBase64(map.getContentMap())));
    			mapsToSave.add(doc);
    		}
    	}
    	return mapsToSave;
    }
    
    private List<Document> buildDefinitionDocuments(List<FhirStructuresDTO.Definition> structureDefinitions) {
    	List<Document> definitionsToSave = new ArrayList<>();
    	if(structureDefinitions!=null) {
    		for(FhirStructuresDTO.Definition definition : structureDefinitions) {
    			Document doc = new Document();
    			doc.put("name_definition", definition.getNameDefinition());
    			doc.put("filename_definition", definition.getFilenameDefinition());
    			doc.put("content_definition",new Binary(StringUtility.decodeBase64(definition.getContentDefinition())));
    			definitionsToSave.add(doc);
    		}
    	}
    	return definitionsToSave;
    }
    
    
    private List<Document> buildValuesetDocuments(List<FhirStructuresDTO.Valueset> valuesets) {
    	List<Document> valuesetsToSave = new ArrayList<>();
    	if(valuesets!=null) {
    		for(FhirStructuresDTO.Valueset valueset : valuesets) {
    			Document doc = new Document();
    			doc.put("name_valueset", valueset.getNameValueset());
    			doc.put("filename_valueset", valueset.getFilenameValueset());
    			doc.put("content_valueset", new Binary(StringUtility.decodeBase64(valueset.getContentValueset())));
    			valuesetsToSave.add(doc);
    		}
    	}
    	return valuesetsToSave;
    }
}
