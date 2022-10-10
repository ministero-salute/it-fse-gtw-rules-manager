//package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.bson.types.Binary;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import java.util.Date;
// 
//
///**
// * Model to save structure definition.
// */
//@Document(collection = "#{@structureDefinitionBean}")
//@Data
//@NoArgsConstructor
//public class StructureDefinitionETY {
//
//	public static final String FIELD_ID = "_id";
//	public static final String FIELD_NAME = "name_definition";
//	public static final String FIELD_FILENAME = "filename_definition";
//	public static final String FIELD_CONTENT = "content_definition";
//	public static final String FIELD_VERSION = "version_definition";
//	public static final String FIELD_LAST_UPDATE = "last_update_date";
//	public static final String FIELD_LAST_SYNC = "last_sync";
//
//	@Id
//	private String id;
//
//	@Field(name = FIELD_NAME)
//	private String nameDefinition;
//
//	@Field(name = FIELD_FILENAME)
//	private String filenameDefinition;
//
//	@Field(name = FIELD_CONTENT)
//	private Binary contentDefinition;
//
//	@Field(name = FIELD_VERSION)
//	private String versionDefinition;
//
//	@Field(name = FIELD_LAST_UPDATE)
//	private Date lastUpdateDate;
//
//	@Field(name = FIELD_LAST_SYNC)
//	private Date lastSync;
//
//}