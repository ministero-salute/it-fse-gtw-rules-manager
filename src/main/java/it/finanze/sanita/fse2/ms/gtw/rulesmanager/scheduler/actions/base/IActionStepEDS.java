package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;

@FunctionalInterface
public interface IActionStepEDS {
    ActionRes execute();
}