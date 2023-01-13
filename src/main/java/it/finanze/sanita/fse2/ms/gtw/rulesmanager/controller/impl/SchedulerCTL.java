/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.ISchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.tools.RunSchedulerDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Logs.DTO_RUN_TASK_QUEUED;

@RestController
public class SchedulerCTL extends AbstractCTL implements ISchedulerCTL {

	@Autowired
	private InvokeEDSClientScheduler scheduler;

	@Override
	public RunSchedulerDTO runScheduler(HttpServletRequest request) {
		// Put in queue, as soon as the executor is free, task will start
		scheduler.asyncAction();
		// Meanwhile return response
		return new RunSchedulerDTO(getLogTraceInfo(), DTO_RUN_TASK_QUEUED);
	}

	
}
