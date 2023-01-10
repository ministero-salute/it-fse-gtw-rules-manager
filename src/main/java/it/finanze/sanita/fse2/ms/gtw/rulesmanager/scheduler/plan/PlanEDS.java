/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
