/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.ISchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchedulerResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICounterSRV;

@RestController
public class SchedulerCTL extends AbstractCTL implements ISchedulerCTL {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 3260806709541019186L;

	
	@Autowired
	private InvokeEDSClientScheduler edsClientScheduler;
	
	@Autowired
	private ICounterSRV counterSRV;

	@Override
	public SchedulerResponseDTO runScheduler(HttpServletRequest request) {
		edsClientScheduler.action();
		Map<String,Integer> counter = counterSRV.countCfgItems();
		return new SchedulerResponseDTO(getLogTraceInfo(), counter);
	}

	
}
