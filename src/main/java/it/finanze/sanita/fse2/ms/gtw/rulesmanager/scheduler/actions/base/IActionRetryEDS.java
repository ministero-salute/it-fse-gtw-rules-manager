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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.IExecutableEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.IRecoverableEDS;
import org.slf4j.Logger;

import java.util.Optional;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG.RETRY_VALUE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static java.lang.String.format;

public interface IActionRetryEDS {
	
    static <T> Optional<T> retryOnException(IActionFnEDS<T> fn, ChangesetCFG config, Logger log, int times) {
        // Working var
        Optional<T> obj = Optional.empty();
        // Iterate until object is present or by max retryOnException value
        for (int i = 0; i < times && !obj.isPresent(); ++i) {
            try {
                // Retrieve object
                obj = Optional.ofNullable(fn.get());
            } catch (Exception e) {
                // Log me
                log.error(
                    format("[%s][#%s] Retrying operation due to exception", config.getTitle(), i + 1),
                    e
                );
            }
        }
        return obj;
    }

    static <T> Optional<T> retryOnException(IActionFnEDS<T> fn, ChangesetCFG config, Logger log) {
        return retryOnException(fn, config, log, RETRY_VALUE);
    }

    static ActionRes retryExecutorOnException(IExecutableEDS executor, Logger log, int times) {
        // Working var
        ActionRes res = KO;
        // Execute
        for (int i = 0; i <= times && res == KO; i++) {
            // Invoke
            res = executor.execute();
            // Log me
            if(res == KO && i != times) {
                log.error("[Executor][#{}] Restarting executor due to exception", i + 1);
            }
        }
        // Bye
        return res;
    }

    static ActionRes retryRecoveryOnException(IRecoverableEDS executor, Logger log, int times) {
        // Working var
        ActionRes res = KO;
        // Execute
        for (int i = 0; i <= times && res == KO; i++) {
            // Invoke
            res = executor.recovery();
            // Log me
            if(res == KO && i != times) {
                log.error("[Executor][#{}] Restarting recovery due to exception", i + 1);
            }
        }
        // Bye
        return res;
    }

    static ActionRes retryExecutorOnException(IExecutableEDS executor, Logger log) {
        return retryExecutorOnException(executor, log, RETRY_VALUE);
    }

    static ActionRes retryRecoveryOnException(IRecoverableEDS executor, Logger log) {
        return retryRecoveryOnException(executor, log, RETRY_VALUE);
    }
}
