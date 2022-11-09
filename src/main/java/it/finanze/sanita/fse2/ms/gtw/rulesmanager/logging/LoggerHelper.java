/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.LogDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * 
 */ 
@Service
@Slf4j
public class LoggerHelper {
	
	/* 
	 * Specify here the format for the dates 
	 */
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS"); 
	
	
	/* 
	 * Implements structured logs, at all logging levels
	 */
	public void trace(String message, String operation, 
			   ResultLogEnum result, Date startDateOperation) {

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				build();

		final String logMessage = StringUtility.toJSON(logDTO);
		log.trace(logMessage);
	} 
	
	public void debug(String message,  String operation,  
			   ResultLogEnum result, Date startDateOperation) {

			LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.debug(logMessage);
	}

	public void info(String message, String operation,  
			ResultLogEnum result, Date startDateOperation) {

			LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.info(logMessage);
	} 
	
	public void warn(String message, String operation,  
			   ResultLogEnum result, Date startDateOperation) {

		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.warn(logMessage);
	} 
	
	public void error(String message, String operation,  
			   ResultLogEnum result, Date startDateOperation) {


		LogDTO logDTO = LogDTO.builder().
				message(message).
				operation(operation).
				op_result(result.getCode()).
				op_timestamp_start(dateFormat.format(startDateOperation)).
				op_timestamp_end(dateFormat.format(new Date())).
				build();
		
		final String logMessage = StringUtility.toJSON(logDTO);
		log.error(logMessage);
	}
}
