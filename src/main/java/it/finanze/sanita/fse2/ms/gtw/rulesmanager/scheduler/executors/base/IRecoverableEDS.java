/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;

public interface IRecoverableEDS {
    ActionRes recovery();
}
