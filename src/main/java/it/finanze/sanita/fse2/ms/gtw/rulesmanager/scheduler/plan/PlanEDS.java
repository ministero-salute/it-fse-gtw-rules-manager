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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.listeners.OnPlanListener;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.listeners.OnStepListener;

import java.util.*;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public final class PlanEDS {

    private final LinkedHashMap<String, IActionStepEDS> steps;
    private final List<OnStepListener> onStepListeners;
    private final List<OnPlanListener> onPlanListeners;

    public PlanEDS() {
        steps = new LinkedHashMap<>();
        onStepListeners = new ArrayList<>();
        onPlanListeners = new ArrayList<>();
    }

    public void addOnStepListener(OnStepListener l) {
        Objects.requireNonNull(l, "Listener cannot be null");
        onStepListeners.add(l);
    }

    public void addOnPlanListener(OnPlanListener l) {
        Objects.requireNonNull(l, "Listener cannot be null");
        onPlanListeners.add(l);
    }

    public void addStep(String name, IActionStepEDS step) {
        // Add to list
        steps.put(name, step);
    }

    public ActionRes execute() {
        ActionRes res = OK;
        // Iterate entry-set of map
        for (Map.Entry<String, IActionStepEDS> entry : steps.entrySet()) {
            // Get step
            IActionStepEDS step = entry.getValue();
            // Execute it
            res = step.execute();
            // Fire listeners
            fireOnStepListeners(entry.getKey(), res);
            // Exit when not OK
            if (res != OK) break;
        }
        // Fire listeners
        fireOnPlanListeners(res);
        return res;
    }

    private void fireOnStepListeners(String name, ActionRes res) {
        onStepListeners.forEach(l -> l.onStepCompleted(name, res));
    }

    private void fireOnPlanListeners(ActionRes res) {
        onPlanListeners.forEach(l -> l.onPlanCompleted(res));
    }

}
