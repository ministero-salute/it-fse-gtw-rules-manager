package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Model to save dictionary.
 */
@Document(collection = "dictionary")
@Data
@NoArgsConstructor
public class DictionaryETY {

	@Id
	private String id;
	
	@Field(name = "filename")
	private String fileName;
	
	@Field(name = "content_file")
	private Binary contentFile;
	
	@Field(name = "version")
	private String version;
	 
}