/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;

public enum ActionRes {
    OK,
    KO,
    EMPTY;
    public enum CallbackRes {
        CB_OK,
        CB_KO
    }

    /**
     * @return Convert the current action as a callback treating the EMPTY flag as positive
     */
    public CallbackRes toCallback() {
        return this == OK || this == EMPTY ? CB_OK : CB_KO;
    }
}
