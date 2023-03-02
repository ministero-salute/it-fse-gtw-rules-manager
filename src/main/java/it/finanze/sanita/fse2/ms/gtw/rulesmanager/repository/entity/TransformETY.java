/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "#{@structuresBean}")
@Data
@NoArgsConstructor
public class TransformETY {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_URI = "uri";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_TEMPLATE_ID_ROOT = "template_id_root";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_DELETED = "deleted";
    public static final String FIELD_LAST_SYNC = "last_sync";
    
    @Id
	private String id;
	@Field(name = FIELD_URI)
	private String uri;
	@Field(name = FIELD_VERSION)
	private String version;
	@Field(name = FIELD_FILENAME)
	private String filename;
	@Field(name = FIELD_CONTENT)
	private Binary content;
	@Field(name = FIELD_TYPE)
    private String type;
	@Field(name = FIELD_TEMPLATE_ID_ROOT)
    private List<String> templateIdRoot;
    @Field(name = FIELD_LAST_UPDATE)
    private Date lastUpdateDate;
    @Field(name = FIELD_DELETED)
    private Boolean deleted;
    @Field(name = FIELD_LAST_SYNC)
    private Date lastSync;

}
