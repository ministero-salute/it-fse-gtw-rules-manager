package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model to save terminology.
 */
@Document(collection = "#{@dictionaryBean}")
@Data
@NoArgsConstructor
public class DictionaryETY {

	public static final String FIELD_ID = "_id";
    public static final String FIELD_SYSTEM = "system";
    public static final String FIELD_VERSION = "version";
	public static final String FIELD_CREATION_UPDATE = "creation_date";
    public static final String FIELD_RELEASE_DATE = "release_date";
    public static final String FIELD_WHITELIST = "whitelist";


	@Id
	private String id;
	@Field(name = FIELD_SYSTEM)
	private String system;
	@Field(name = FIELD_VERSION)
	private String version;
	@Field(name = FIELD_CREATION_UPDATE)
    private Date creationDate;
    @Field(name = FIELD_RELEASE_DATE)
    private Date releaseDate;
    @Field(name = FIELD_WHITELIST)
    private boolean whiteList;
    @Field(name = "deleted")
    private boolean deleted;
	
}
