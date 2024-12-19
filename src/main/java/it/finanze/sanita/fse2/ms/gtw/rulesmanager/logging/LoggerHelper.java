
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.LogDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoggerHelper {
    
	Logger kafkaLog = LoggerFactory.getLogger("kafka-logger"); 
	
	@Value("${log.kafka-log.enable}")
	private boolean kafkaLogEnable;
	
	@Autowired
	private IConfigClient configClient;
	
	private String gatewayName;
	
	@Value("${spring.application.name}")
	private String msName;
	
	@Autowired
    private IConfigSRV configSRV;
	
	/* 
	 * Specify here the format for the dates 
	 */
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS"); 
	
	
	/* 
	 * Implements structured logs, at all logging levels
	 */
	public void trace(String logType, String message, String operation, 
			   ResultLogEnum result, Date startDateOperation) {
		
		if(configSRV.isControlLogPersistenceEnable()) {
			LogDTO logDTO = LogDTO.builder().
					message(message).
					operation(operation).
					op_result(result.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					log_type(logType).
					build();
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.trace(logMessage);

			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.trace(logMessage);
			}	
		}
		
	} 
	
	public void debug(String logType, String message,  String operation,  
			   ResultLogEnum result, Date startDateOperation) {
		
		if(configSRV.isControlLogPersistenceEnable()) {
			LogDTO logDTO = LogDTO.builder().
					message(message).
					operation(operation).
					op_result(result.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					log_type(logType).
					build();
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.debug(logMessage);

			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.debug(logMessage);
			}
		}
			
	}

	public void info(String logType, String message, String operation,  
			ResultLogEnum result, Date startDateOperation) {
		
		if(configSRV.isControlLogPersistenceEnable()) {
			LogDTO logDTO = LogDTO.builder().
					message(message).
					operation(operation).
					op_result(result.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					log_type(logType).
					build();
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.info(logMessage);
			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.info(logMessage);
			}
		}
			
	} 
	
	public void warn(String logType, String message, String operation,  
			   ResultLogEnum result, Date startDateOperation) {
		
		if(configSRV.isControlLogPersistenceEnable()) {
			LogDTO logDTO = LogDTO.builder().
					message(message).
					operation(operation).
					op_result(result.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					log_type(logType).
					build();
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.warn(logMessage);
	 
			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.warn(logMessage);
			}
		}
		
 
	} 
	
	public void error(String logType,String message, String operation,  
			   ResultLogEnum result, Date startDateOperation) {
		if(configSRV.isControlLogPersistenceEnable()) {
			LogDTO logDTO = LogDTO.builder().
					message(message).
					operation(operation).
					op_result(result.getCode()).
					op_timestamp_start(dateFormat.format(startDateOperation)).
					op_timestamp_end(dateFormat.format(new Date())).
					gateway_name(getGatewayName()).
					microservice_name(msName).
					log_type(logType).
					build();
			
			final String logMessage = StringUtility.toJSON(logDTO);
			log.error(logMessage);

			if (Boolean.TRUE.equals(kafkaLogEnable)) {
				kafkaLog.error(logMessage);
			}
		}

		
	}

	/**
	 * Returns the gateway name.
	 * 
	 * @return The GatewayName of the ecosystem.
	 */
	private String getGatewayName() {
		if (gatewayName == null) {
			gatewayName = configClient.getGatewayName();
		}
		return gatewayName;
	}
    
}
