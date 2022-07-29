package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

@FunctionalInterface
public interface IActionFnEDS<T> {
    T get() throws Exception;
}
