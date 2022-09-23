package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


/**
 * @author vincenzoingenito
 * Model to save map.
 */
@Document(collection = "#{@structureMapBean}")
@Data
@NoArgsConstructor
public class StructureMapETY {

	public static final String FIELD_ID = "_id";
	public static final String FIELD_NAME = "name_map";
	public static final String FIELD_FILENAME = "filename_map";
	public static final String FIELD_CONTENT = "content_map";
	public static final String FIELD_ID_ROOT = "template_id_root";
	public static final String FIELD_ID_EXTS = "template_id_extension";
	public static final String FIELD_LAST_UPDATE = "last_update_date";
	public static final String FIELD_LAST_SYNC = "last_sync";

	@Id
	private String id;

	@Field(name = FIELD_NAME)
	private String nameMap;

	@Field(name = FIELD_FILENAME)
	private String filenameMap;

	@Field(name = FIELD_CONTENT)
	private Binary contentMap;

	@Field(name = FIELD_ID_ROOT)
	private String templateIdRoot;

	@Field(name = FIELD_ID_EXTS)
	private String templateIdExtension;

	@Field(name = FIELD_LAST_SYNC)
	private Date lastSync;

	@Field(name = FIELD_LAST_UPDATE)
	private Date lastUpdateDate;

	 
}