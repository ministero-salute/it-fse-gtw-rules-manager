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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
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
    private Boolean whiteList;
    @Field(name = FIELD_DELETED)
    private Boolean deleted;

    public static org.bson.Document fromMap(TerminologyMapDTO map) {
        org.bson.Document entity = new org.bson.Document();
        entity.put(FIELD_SYSTEM, map.getSystem());
        entity.put(FIELD_VERSION, map.getVersion());
        entity.put(FIELD_CREATION_DATE, map.getCreationDate());
        entity.put(FIELD_RELEASE_DATE, map.getReleaseDate());
        entity.put(FIELD_WHITELIST, map.isWhiteList());
        entity.put(FIELD_DELETED, map.getDeleted());
        return entity;
    }

}
