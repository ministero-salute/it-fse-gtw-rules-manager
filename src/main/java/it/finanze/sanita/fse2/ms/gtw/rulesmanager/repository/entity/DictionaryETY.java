/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

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
	public static final String FIELD_CREATION_DATE = "creation_date";
    public static final String FIELD_RELEASE_DATE = "release_date";
    public static final String FIELD_WHITELIST = "whitelist";
    public static final String FIELD_DELETED = "deleted";


	@Id
	private String id;
	@Field(name = FIELD_SYSTEM)
	private String system;
	@Field(name = FIELD_VERSION)
	private String version;
	@Field(name = FIELD_CREATION_DATE)
    private Date creationDate;
    @Field(name = FIELD_RELEASE_DATE)
    private Date releaseDate;
    @Field(name = FIELD_WHITELIST)
    private boolean whiteList;
    @Field(name = FIELD_DELETED)
    private boolean deleted;

    public static org.bson.Document fromMap(TerminologyMapDTO map) {
        org.bson.Document entity = new org.bson.Document();
        entity.put(FIELD_SYSTEM, map.getSystem());
        entity.put(FIELD_VERSION, map.getVersion());
        entity.put(FIELD_CREATION_DATE, map.getCreationDate());
        entity.put(FIELD_RELEASE_DATE, map.getReleaseDate());
        entity.put(FIELD_WHITELIST, map.isWhiteList());
        entity.put(FIELD_DELETED, map.isDeleted());
        return entity;
    }

}
