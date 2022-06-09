package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import java.util.Date;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Model to save schema.
 */
@Document(collection = "schema")
@Data
@NoArgsConstructor
public class SchemaETY {

	@Id
	private String id;
	
	@Field(name = "name_schema")
	private String nameSchema;
	
	@Field(name = "content_schema")
	private Binary contentSchema;
	
	@Field(name = "type_id_extension")
	private String typeIdExtension;
	
	@Field(name = "root_schema")
	private Boolean rootSchema;
	
	@Field(name = "last_update_date")
	private Date lastUpdateDate;
	 
}