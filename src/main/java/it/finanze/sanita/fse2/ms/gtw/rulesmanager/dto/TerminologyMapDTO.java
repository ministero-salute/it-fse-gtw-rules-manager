/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
	private boolean deleted;

	public boolean isWhiteList() {
		return code != null && code.equals(WHITELIST_FLAG);
	}

}