package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


/**
 * Model to save xsl transform.
 */
@Document(collection = "#{@xslTransformBean}")
@Data
@NoArgsConstructor
public class XslTransformETY {

	public static final String FIELD_ID = "_id";
	public static final String FIELD_FILENAME = "name_xsl_transform";
	public static final String FIELD_CONTENT = "content_xsl_transform";
	public static final String FIELD_VERSION = "template_id_extension";
	public static final String FIELD_ROOT = "template_id_root";
	public static final String FIELD_LAST_UPDATE = "last_update_date";
	public static final String FIELD_LAST_SYNC = "last_sync";

	@Id
	private String id;
	
	@Field(name = FIELD_ROOT)
	private String templateIdRoot;
	
	@Field(name = FIELD_FILENAME)
	private String nameXslTransform;
	
	@Field(name = FIELD_CONTENT)
	private Binary contentXslTransform;
	
	@Field(name = FIELD_VERSION)
	private String templateIdExtension;
	
	@Field(name = FIELD_LAST_UPDATE)
    private Date lastUpdateDate;
	
	 
}