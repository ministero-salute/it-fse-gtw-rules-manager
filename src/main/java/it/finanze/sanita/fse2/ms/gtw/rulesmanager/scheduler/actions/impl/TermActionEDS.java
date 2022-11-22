/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.*;

public final class TermActionEDS {

    private TermActionEDS() {}

    /**
     * Synchronise code-system among dictionary and terminology
     */
    public static final String ON_CS_SYNC = "ON_CS_SYNC";

    public static String[] defaults() {
        return new String[] {
            RESET,
            CLEAN,
            CHANGESET_PROD,
            CHANGESET_EMPTY,
            STAGING,
            PROCESSING,
            VERIFY,
            SYNC,
            CHANGESET_STAGING,
            CHANGESET_ALIGNMENT,
            SWAP,
            ON_CS_SYNC
        };
    }

}
