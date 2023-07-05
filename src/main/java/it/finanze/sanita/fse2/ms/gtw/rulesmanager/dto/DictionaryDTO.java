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

import lombok.Data;

import java.util.Date;

@Data
public class DictionaryDTO {

	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_VERSION = "version";
	public static final String FIELD_RELEASE_DATE = "release_date";
	public static final String FIELD_WHITELIST = "whitelist";
	public static final String FIELD_DELETED = "deleted";
	public static final String FIELD_SOURCE = "source";

	private String system;
	private String version;
	private Date release_date;
	private boolean whitelist;
	private boolean deleted;
	private int source;

	public DictionaryDTO(
		String system,
		String version,
		Date release_date,
		Boolean whitelist,
		Boolean deleted,
		int source
	) {
		this.system = system;
		this.version = version;
		this.release_date = release_date;
		this.whitelist = whitelist != null && whitelist;
		this.deleted = deleted != null && deleted;
		this.source = source;
	}

	public Date getReleaseDate() {
		return release_date;
	}
}