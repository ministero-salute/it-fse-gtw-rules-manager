package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Model to save xsl transform.
 */
@Document(collection = "xsl_transform")
@Data
@NoArgsConstructor
public class XslTransformETY {

	@Id
	private String id;
	
	@Field(name = "cda_type")
	private String cdaType;
	
	@Field(name = "name_xsl_transform")
	private String nameXslTransform;
	
	@Field(name = "content_xsl_transform")
	private Binary contentXslTransform;
	
	@Field(name = "version")
	private String version;
	 
}