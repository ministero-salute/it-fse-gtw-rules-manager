/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions;

public final class ActionEDS {

    private ActionEDS() {}

    /**
     * Reset client references
     */
    public static final String RESET = "RESET";
    /**
     * Remove staging repositories (if any)
     */
    public static final String CLEAN = "CLEAN";
    /**
     * Retrieve changeset using production last-sync value
     */
    public static final String CHANGESET_PROD = "CHANGESET_PROD";
    /**
     * Retrieve changeset using staging last-sync value
     */
    public static final String CHANGESET_STAGING = "CHANGESET_STAGING";
    /**
     * Verify if changeset is empty then it quit
     */
    public static final String CHANGESET_EMPTY = "CHANGESET_EMPTY";
    /**
     * Clone the production repository into a staging-one
     */
    public static final String STAGING = "STAGING";
    /**
     * Execute all pending operations from changeset into staging
     */
    public static final String PROCESSING = "PROCESSING";
    /**
     * Applies indexes on staging if necessary
     */
    public static final String INDEXING = "INDEXING";
    /**
     * Verify the pending operations have been fully processed
     */
    public static final String VERIFY = "VERIFY";
    /**
     * Call the changeset to check if anything has been updated recently (after processing usually)
     */
    public static final String CHANGESET_ALIGNMENT = "CHANGESET_ALIGNMENT";
    /**
     * Update the last_sync value
     */
    public static final String SYNC = "SYNC";
    /**
     * Rename the staging instance as the production instance, dropping the target (production)
     */
    public static final String SWAP = "SWAP";

    public static String[] defaults() {
        return new String[]{
            RESET,
            CLEAN,
            CHANGESET_PROD,
            CHANGESET_EMPTY,
            STAGING,
            PROCESSING,
            INDEXING,
            VERIFY,
            SYNC,
            CHANGESET_STAGING,
            CHANGESET_ALIGNMENT,
            SWAP
        };
    }

    /**
     * Retrieve changeset using null value (reset)
     */
    public static final String CHANGESET_RECOVERY = "CHANGESET_RECOVERY";
    /**
     * Creates an empty staging collection
     */
    public static final String STAGING_RECOVERY = "STAGING_RECOVERY";
    // We are skipping the empty check because if the remote instance
    // is empty we want the production collection to be like it.
    // We are also skipping the staging cloning process because
    // we want to start from a brand-new collection.
    public static String[] recovery() {
        return new String[]{
            RESET,
            CLEAN,
            CHANGESET_RECOVERY,
            STAGING_RECOVERY,
            PROCESSING,
            INDEXING,
            VERIFY,
            SYNC,
            CHANGESET_STAGING,
            CHANGESET_ALIGNMENT,
            SWAP
        };
    }

}
