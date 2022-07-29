package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;

import java.util.ArrayList;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

public final class ActionBuilderEDS {
    private final List<IActionStepEDS> steps;

    private ActionBuilderEDS() {
        this.steps = new ArrayList<>();
    }

    public static ActionBuilderEDS builder() {
        return new ActionBuilderEDS();
    }

    public ActionBuilderEDS step(IActionStepEDS step) {
        // Add to list
        steps.add(step);
        // Return builder
        return this;
    }

    public ActionRes execute() {
        // Working var
        ActionRes res = OK;
        // Stop if any step is KO
        for (int i = 0; i < steps.size() && res == OK; i++) {
            // Get step
            IActionStepEDS step = steps.get(i);
            // Execute it
            res = step.execute();
        }
        // Return result
        return res;
    }

}
