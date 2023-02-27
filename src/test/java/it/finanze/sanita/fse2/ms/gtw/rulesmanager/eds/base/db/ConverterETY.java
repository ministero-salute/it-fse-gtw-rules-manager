package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

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
}
