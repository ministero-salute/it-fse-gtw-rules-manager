/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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
