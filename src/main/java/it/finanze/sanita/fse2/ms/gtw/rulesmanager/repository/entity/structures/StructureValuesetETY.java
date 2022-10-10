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
// * Model to save valuset documents
// */
//@Document(collection = "#{@valuesetBean}")
//@Data
//@NoArgsConstructor
//public class StructureValuesetETY {
//
//	public static final String FIELD_ID = "_id";
//	public static final String FIELD_NAME = "name_valueset";
//	public static final String FIELD_FILENAME = "filename_valueset";
//	public static final String FIELD_CONTENT = "content_valueset";
//	public static final String FIELD_LAST_UPDATE = "last_update_date";
//	public static final String FIELD_LAST_SYNC = "last_sync";
//
//	@Id
//	private String id;
//
//	@Field(name = FIELD_FILENAME)
//	private String filenameValueset;
//
//	@Field(name = FIELD_NAME)
//	private String nameValueset;
//
//	@Field(name = FIELD_CONTENT)
//	private Binary contentValueset;
//
//	@Field(name = FIELD_LAST_UPDATE)
//	private Date lastUpdateDate;
//
//	@Field(name = FIELD_LAST_SYNC)
//	private Date lastSync;
//	 
//}
