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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Model to save schematron.
 */
@Document(collection = "#{@schematronBean}")
@Data
@NoArgsConstructor
public class SchematronETY {

	public static final String FIELD_ID = "_id";
    public static final String FIELD_FILENAME = "name_schematron";
    public static final String FIELD_CONTENT = "content_schematron";
    public static final String FIELD_VERSION = "version";
	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_ROOT = "template_id_root";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_LAST_SYNC = "last_sync";
    public static final String FIELD_DELETED = "deleted";

	@Id
	private String id;
	@Field(name = FIELD_CONTENT)
	private Binary contentSchematron;
	@Field(name = FIELD_FILENAME)
	private String nameSchematron;
	@Field(name = FIELD_ROOT)
	private String templateIdRoot;
	@Field(name = FIELD_VERSION)
	private String version;
	@Field(name = FIELD_SYSTEM)
	private String system;
	@Field(name = FIELD_LAST_UPDATE)
	private Date lastUpdateDate;
	@Field(name = FIELD_LAST_SYNC)
    private Date lastSync;
	@Field(name = FIELD_DELETED)
    private Boolean deleted;
}