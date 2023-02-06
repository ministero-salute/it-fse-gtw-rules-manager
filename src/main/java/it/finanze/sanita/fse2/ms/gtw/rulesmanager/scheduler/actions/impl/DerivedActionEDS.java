/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.*;

public final class DerivedActionEDS {

    private DerivedActionEDS() {}

    public static String[] defaults() {
        return new String[] {
            RESET,
            CLEAN,
            STAGING,
            PROCESSING,
            SWAP,
        };
    }

}
