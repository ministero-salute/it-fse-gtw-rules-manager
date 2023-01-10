/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.ISchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SchedulerCTL extends AbstractCTL implements ISchedulerCTL {

	@Autowired
	private InvokeEDSClientScheduler scheduler;

	@Override
	public ResponseDTO runScheduler(HttpServletRequest request) {
		// Put in queue, as soon as the executor is free, task will start
		scheduler.asyncAction();
		// Meanwhile return response
		return new ResponseDTO(getLogTraceInfo());
	}

	
}
