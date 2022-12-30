/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.impl;


import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.FhirStructuresDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.base.DefinitionETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.base.MapETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.base.ValuesetETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.TransformETY.*;

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
    	List<Document> mapsToSave = buildMapDocuments(dto.getMaps());
    	List<Document> definitionsToSave = buildDefinitionDocuments(dto.getDefinitions());
    	List<Document> valuesetsToSave = buildValuesetDocuments(dto.getValuesets());
    	Document doc = new Document();
		doc.put(FIELD_ID, new ObjectId(dto.getId()));
    	doc.put(FIELD_MAPS, mapsToSave);
    	doc.put(FIELD_DEFINITIONS, definitionsToSave);
    	doc.put(FIELD_VALUESETS, valuesetsToSave);
    	doc.put(FIELD_TEMPLATE_ID_ROOT, dto.getTemplateIdRoot());
    	doc.put(FIELD_LAST_UPDATE, dto.getLastUpdateDate());
    	doc.put(FIELD_VERSION, dto.getVersion());
    	doc.put(FIELD_ROOT_MAP, dto.getRootMap());
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
            .append(FIELD_MAPS, doc.getList(FIELD_MAPS, Document.class))
			.append(FIELD_DEFINITIONS, doc.getList(FIELD_DEFINITIONS, Document.class))
			.append(FIELD_VALUESETS, doc.getList(FIELD_VALUESETS, Document.class))
            .append(FIELD_TEMPLATE_ID_ROOT, doc.getString(FIELD_TEMPLATE_ID_ROOT))
            .append(FIELD_LAST_UPDATE, doc.getDate(FIELD_LAST_UPDATE))
			.append(FIELD_VERSION, doc.getString(FIELD_VERSION))
			.append(FIELD_ROOT_MAP, doc.getString(FIELD_ROOT_MAP))
			.append(FIELD_LAST_SYNC, doc.getDate(FIELD_LAST_SYNC))
            .append(FIELD_DELETED, doc.getBoolean(FIELD_DELETED)); 
    }
    
    public List<Document> buildMapDocuments(List<FhirStructuresDTO.FhirStructures> maps) {
    	List<Document> mapsToSave = new ArrayList<>();
    	if(maps!=null) {
    		for(FhirStructuresDTO.FhirStructures map : maps) {
    			Document doc = new Document();
    			doc.put(MapETY.FIELD_NAME, map.getNameMap());
    			doc.put(MapETY.FIELD_FILENAME, map.getFilenameMap());
    			doc.put(MapETY.FIELD_CONTENT, new Binary(StringUtility.decodeBase64(map.getContentMap())));
    			mapsToSave.add(doc);
    		}
    	}
    	return mapsToSave;
    }
    
    public List<Document> buildDefinitionDocuments(List<FhirStructuresDTO.Definition> structureDefinitions) {
    	List<Document> definitionsToSave = new ArrayList<>();
    	if(structureDefinitions!=null) {
    		for(FhirStructuresDTO.Definition definition : structureDefinitions) {
    			Document doc = new Document();
    			doc.put(DefinitionETY.FIELD_NAME, definition.getNameDefinition());
    			doc.put(DefinitionETY.FIELD_FILENAME, definition.getFilenameDefinition());
    			doc.put(DefinitionETY.FIELD_CONTENT,new Binary(StringUtility.decodeBase64(definition.getContentDefinition())));
    			definitionsToSave.add(doc);
    		}
    	}
    	return definitionsToSave;
    }
    
    
    public List<Document> buildValuesetDocuments(List<FhirStructuresDTO.Valueset> valuesets) {
    	List<Document> valuesetsToSave = new ArrayList<>();
    	if(valuesets!=null) {
    		for(FhirStructuresDTO.Valueset valueset : valuesets) {
    			Document doc = new Document();
    			doc.put(ValuesetETY.FIELD_NAME, valueset.getNameValueset());
    			doc.put(ValuesetETY.FIELD_FILENAME, valueset.getFilenameValueset());
    			doc.put(ValuesetETY.FIELD_CONTENT, new Binary(StringUtility.decodeBase64(valueset.getContentValueset())));
    			valuesetsToSave.add(doc);
    		}
    	}
    	return valuesetsToSave;
    }
}
