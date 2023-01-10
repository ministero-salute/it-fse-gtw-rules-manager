package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.listeners;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;

@FunctionalInterface
public interface OnPlanListener {
    void onPlanCompleted(ActionRes status);
}

