/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import java.util.Date;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model to save schema documents
 */
@Document(collection = "#{@schemaBean}")
@Data
@NoArgsConstructor
public class SchemaETY {

    public static final String FIELD_ID = "_id";
    public static final String FIELD_FILENAME = "name_schema";
    public static final String FIELD_CONTENT = "content_schema";
    public static final String FIELD_TYPE_ID_EXT = "type_id_extension";
    public static final String FIELD_ROOT_SCHEMA = "root_schema";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_LAST_SYNC = "last_sync";
    public static final String FIELD_DELETED= "deleted";


    @Id
    private String id;
    @Field(name = FIELD_FILENAME)
    private String nameSchema;
    @Field(name = FIELD_CONTENT)
    private Binary contentSchema;
    @Field(name = FIELD_TYPE_ID_EXT)
    private String typeIdExtension;
    @Field(name = FIELD_ROOT_SCHEMA)
    private Boolean rootSchema;
    @Field(name = FIELD_LAST_UPDATE)
    private Date lastUpdateDate;
    @Field(name = FIELD_LAST_SYNC)
    private Date lastSync;
	@Field(name = FIELD_DELETED)
    private Boolean deleted;

    public static SchemaETY fromDocument(org.bson.Document doc) {
    	SchemaETY entity = new SchemaETY();
    	entity.setId(doc.getObjectId(FIELD_ID).toHexString());
        entity.setNameSchema(doc.getString(FIELD_FILENAME));
        entity.setContentSchema(doc.get(FIELD_CONTENT, Binary.class));
        entity.setTypeIdExtension(doc.getString(FIELD_TYPE_ID_EXT));
        entity.setRootSchema(doc.getBoolean(FIELD_ROOT_SCHEMA));
        entity.setLastUpdateDate(doc.getDate(FIELD_LAST_UPDATE));
        entity.setLastSync(doc.getDate(FIELD_LAST_SYNC));
        entity.setDeleted(doc.getBoolean(FIELD_DELETED));
		return entity;
    }

    public static org.bson.Document toDocument(SchemaETY entity) {
        return new org.bson.Document()
            .append(FIELD_ID, new ObjectId(entity.id))
            .append(FIELD_FILENAME, entity.nameSchema)
            .append(FIELD_CONTENT, entity.contentSchema)
            .append(FIELD_TYPE_ID_EXT, entity.typeIdExtension)
            .append(FIELD_ROOT_SCHEMA, entity.rootSchema)
            .append(FIELD_LAST_UPDATE, entity.lastUpdateDate)
        	.append(FIELD_DELETED, entity.deleted); 
    }

}
