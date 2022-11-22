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

	public static final String WHITELIST_FLAG = "#WHITELIST#";

	private String system;
	private String version;
	private String code;
	private Date releaseDate;
	private Date creationDate;

	public boolean isWhiteList() {
		return code != null && code.equals(WHITELIST_FLAG);
	}

}