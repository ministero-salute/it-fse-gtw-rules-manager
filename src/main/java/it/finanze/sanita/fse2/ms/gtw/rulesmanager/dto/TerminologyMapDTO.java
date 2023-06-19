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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class TerminologyMapDTO {

	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_VERSION = "version";
	public static final String FIELD_CODE = "code";
	public static final String FIELD_RELEASE_DATE = "releaseDate";
	public static final String FIELD_CREATION_DATE = "creationDate";
	public static final String FIELD_DELETED = "deleted";

	public static final String WHITELIST_FLAG = "#WHITELIST#";

	private String system;
	private String version;
	private String code;
	private Date releaseDate;
	private Date creationDate;
	private Boolean deleted;

	public boolean isWhiteList() {
		return code != null && code.equals(WHITELIST_FLAG);
	}

}