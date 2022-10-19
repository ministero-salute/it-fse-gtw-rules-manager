/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

@FunctionalInterface
public interface IActionFnEDS<T> {
    T get() throws Exception;
}
