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

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY.Reference;

@Data
public class DictionaryDTO {

	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_VERSION = "version";
	public static final String FIELD_RELEASE_DATE = "releaseDate";
	public static final String FIELD_WHITELIST = "whitelist";
	public static final String FIELD_DELETED = "deleted";

	private String system;
	private String version;
	private Date releaseDate;
	private boolean whitelist;
	private boolean deleted;
	private Reference ref;

	public DictionaryDTO(
		String system,
		String version,
		Date releaseDate,
		Boolean whitelist,
		Boolean deleted,
		Reference ref
	) {
		this.system = system;
		this.version = version;
		this.releaseDate = releaseDate;
		this.whitelist = whitelist != null && whitelist;
		this.deleted = deleted != null && deleted;
		this.ref = ref;
	}
}