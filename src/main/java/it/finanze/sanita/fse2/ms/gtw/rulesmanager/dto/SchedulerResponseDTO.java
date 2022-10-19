/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerResponseDTO extends ResponseDTO {

	
	private Map<String,Integer> counters;
	

	public SchedulerResponseDTO() {
		super();
	}

	public SchedulerResponseDTO(final LogTraceInfoDTO traceInfo, final Map<String,Integer> inCounters) {
		super(traceInfo);
		counters = inCounters;
	}
}
	