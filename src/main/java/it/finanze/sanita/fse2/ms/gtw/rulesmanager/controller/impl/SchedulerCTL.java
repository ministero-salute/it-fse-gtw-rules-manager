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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.ISchedulerCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.tools.RunSchedulerDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsSchedulerRunningException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Logs.DTO_RUN_TASK_QUEUED;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Logs.ERR_SCH_RUNNING;

@RestController
public class SchedulerCTL extends AbstractCTL implements ISchedulerCTL {

	@Autowired
	private InvokeEDSClientScheduler scheduler;

	@Override
	public RunSchedulerDTO run() {
		// Throw exception if trying to run and already executing
		if(scheduler.isRunning()) throw new EdsSchedulerRunningException(ERR_SCH_RUNNING);
		// Put in queue, as soon as the executor is free, task will start
		scheduler.asyncAction();
		// Meanwhile return response
		return new RunSchedulerDTO(getLogTraceInfo(), DTO_RUN_TASK_QUEUED);
	}

	
}
