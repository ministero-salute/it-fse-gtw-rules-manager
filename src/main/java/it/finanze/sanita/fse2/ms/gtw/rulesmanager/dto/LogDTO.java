/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogDTO {

	private String log_type;
	
	private String message;
	
	private String operation;
	
	private String op_result;
	
	private String op_timestamp_start;
	
	private String op_timestamp_end;
	
	private String gateway_name;
	
	private String microservice_name;
}
