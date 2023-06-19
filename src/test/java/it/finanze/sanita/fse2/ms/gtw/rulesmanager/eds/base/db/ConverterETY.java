/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TransformETY;

public final class ConverterETY {
    public static Document fromSchemaToDoc(SchemaETY entity) {
        return new Document()
            .append(SchemaETY.FIELD_ID, new ObjectId(entity.getId()))
            .append(SchemaETY.FIELD_FILENAME, entity.getNameSchema())
            .append(SchemaETY.FIELD_CONTENT, entity.getContentSchema())
            .append(SchemaETY.FIELD_TYPE_ID_EXT, entity.getTypeIdExtension())
            .append(SchemaETY.FIELD_ROOT_SCHEMA, entity.getRootSchema())
            .append(SchemaETY.FIELD_LAST_UPDATE, entity.getLastUpdateDate())
            .append(SchemaETY.FIELD_DELETED, entity.getDeleted() != null && entity.getDeleted());
    }

    public static SchemaETY docToSchema(Document doc) {
        SchemaETY entity = new SchemaETY();
        entity.setId(doc.getObjectId(SchemaETY.FIELD_ID).toHexString());
        entity.setNameSchema(doc.getString(SchemaETY.FIELD_FILENAME));
        entity.setContentSchema(doc.get(SchemaETY.FIELD_CONTENT, Binary.class));
        entity.setTypeIdExtension(doc.getString(SchemaETY.FIELD_TYPE_ID_EXT));
        entity.setRootSchema(doc.getBoolean(SchemaETY.FIELD_ROOT_SCHEMA));
        entity.setLastUpdateDate(doc.getDate(SchemaETY.FIELD_LAST_UPDATE));
        entity.setLastSync(doc.getDate(SchemaETY.FIELD_LAST_SYNC));
        entity.setDeleted(doc.getBoolean(SchemaETY.FIELD_DELETED));
        return entity;
    }

    public static Document fromTermsToDoc(TerminologyETY entity) {
        return new Document()
            .append(TerminologyETY.FIELD_ID, new ObjectId(entity.getId()))
            .append(TerminologyETY.FIELD_SYSTEM, entity.getSystem())
            .append(TerminologyETY.FIELD_VERSION, entity.getVersion())
            .append(TerminologyETY.FIELD_CODE, entity.getCode())
            .append(TerminologyETY.FIELD_DESCRIPTION, entity.getDescription())
            .append(TerminologyETY.FIELD_RELEASE_DATE, entity.getReleaseDate())
            .append(TerminologyETY.FIELD_LAST_UPDATE, entity.getLastUpdateDate())
            .append(TerminologyETY.FIELD_DELETED, entity.getDeleted() != null && entity.getDeleted());
    }

    public static TerminologyETY docToTerms(Document doc) {
        TerminologyETY entity = new TerminologyETY();
        entity.setId(doc.getObjectId(TerminologyETY.FIELD_ID).toHexString());
        entity.setSystem(doc.getString(TerminologyETY.FIELD_SYSTEM));
        entity.setVersion(doc.getString(TerminologyETY.FIELD_VERSION));
        entity.setCode(doc.getString(TerminologyETY.FIELD_CODE));
        entity.setDescription(doc.getString(TerminologyETY.FIELD_DESCRIPTION));
        entity.setLastUpdateDate(doc.getDate(TerminologyETY.FIELD_LAST_UPDATE));
        entity.setLastSync(doc.getDate(TerminologyETY.FIELD_LAST_SYNC));
        return entity;
    }
    
    public static Document fromTransformToDoc(TransformETY entity) {
        return new Document()
            .append(TransformETY.FIELD_ID, new ObjectId(entity.getId()))
            .append(TransformETY.FIELD_VERSION, entity.getVersion())
            .append(TransformETY.FIELD_FILENAME, entity.getFilename())
            .append(TransformETY.FIELD_CONTENT, entity.getContent())
            .append(TransformETY.FIELD_TYPE, entity.getType())
            .append(TransformETY.FIELD_TEMPLATE_ID_ROOT, entity.getTemplateIdRoot())
            .append(TransformETY.FIELD_LAST_UPDATE, entity.getLastUpdateDate())
            .append(TransformETY.FIELD_LAST_SYNC, entity.getLastSync())
            .append(TransformETY.FIELD_DELETED, entity.getDeleted() != null && entity.getDeleted());
    }

    public static TransformETY docToTransform(Document doc) {
    	TransformETY entity = new TransformETY();
        entity.setId(doc.getObjectId(TransformETY.FIELD_ID).toHexString());
        entity.setVersion(doc.getString(TransformETY.FIELD_VERSION));
        entity.setFilename(doc.getString(TransformETY.FIELD_FILENAME));
        entity.setContent(doc.get(TransformETY.FIELD_CONTENT, Binary.class));
        entity.setType(doc.getString(TransformETY.FIELD_TYPE));
        entity.setTemplateIdRoot(doc.getList(TransformETY.FIELD_TEMPLATE_ID_ROOT, String.class));
        entity.setLastUpdateDate(doc.getDate(TransformETY.FIELD_LAST_UPDATE));
        entity.setLastSync(doc.getDate(TransformETY.FIELD_LAST_SYNC));
        entity.setDeleted(doc.getBoolean(TransformETY.FIELD_DELETED));
        return entity;
    }
    
}
