
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.EngineCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.EnginesExecutor;

@Component
@Primary
public class MockEnginesExecutor extends EnginesExecutor {

	protected MockEnginesExecutor(EngineCFG cfg, BridgeEDS bridge) {
		super(cfg, bridge);
	}

	@Override
	public ActionRes onClean() {
		return super.onClean();
	}

	@Override
	public ActionRes onCleanBackup() {
		return super.onCleanBackup();
	}

	@Override
	public ActionRes onStaging() {
		return super.onStaging();
	}

	@Override
	public ActionRes onProcessing() {
		return super.onProcessing();
	}

	@Override
	public CallbackRes onRecovery() {
		return super.onRecovery();
	}

	@Override
	public EngineCFG getConfig() {
		return super.getConfig();
	}

	@Override
	public String[] getSteps() {
		return super.getSteps();
	}

	@Override
	public ActionRes createBackup() {
		return super.createBackup();
	}
	
}
