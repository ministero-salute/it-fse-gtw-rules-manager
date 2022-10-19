/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;

import java.util.LinkedHashMap;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public final class ActionBuilderEDS {

    private final LinkedHashMap<String, IActionStepEDS> steps;

    private ActionBuilderEDS() {
        steps = new LinkedHashMap<>();
    }

    public static ActionBuilderEDS builder() {
        return new ActionBuilderEDS();
    }

    public void addStep(String name, IActionStepEDS step) {
        // Add to list
        steps.put(name, step);
    }

    public ActionRes execute(OnStepCallback cb) {
        ActionRes res = OK;
        // Iterate entry-set of map
        for (Map.Entry<String, IActionStepEDS> entry : steps.entrySet()) {
            // Get step
            IActionStepEDS step = entry.getValue();
            // Execute it
            res = step.execute();
            // Logging step status
            cb.onStepComplete(entry.getKey(), res);
            // Exit when not OK
            if (res != OK) break;
        }
        return res;
    }

    @FunctionalInterface
    public interface OnStepCallback {
        void onStepComplete(String name, ActionRes status);
    }
}
