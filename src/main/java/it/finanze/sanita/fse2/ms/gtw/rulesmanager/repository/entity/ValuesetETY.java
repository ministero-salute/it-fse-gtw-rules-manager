package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Model to save valuset documents
 */
@Document(collection = "#{@valuesetBean}")
@Data
@NoArgsConstructor
public class ValuesetETY {
	
	@Id
	private String id;
	
	@Field(name = "content_valueset")
	private Binary contentValueset;

	@Field(name = "name_valueset")
	private String nameValuset;
	 
}
