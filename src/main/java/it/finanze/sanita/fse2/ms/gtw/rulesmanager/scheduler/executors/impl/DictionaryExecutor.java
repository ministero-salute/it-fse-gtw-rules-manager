/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.DictionaryCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.impl.DictionaryActionEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.chunk.base.EmptySetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICodeSystemVersionSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes.CB_OK;
import static java.lang.String.format;

@Slf4j
@Component
public class DictionaryExecutor extends ExecutorEDS<EmptySetDTO> {

    @Autowired
    private ICodeSystemVersionSRV csv;

    protected DictionaryExecutor(DictionaryCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    public ActionRes onClean() {
        // Delete staging if exists, then move on back-up.
        // If staging is erroneous it exits, otherwise returns the back-up returned value.
        return super.onClean() == OK ? onCleanBackup() : KO;
    }

    public ActionRes onCleanBackup() {
        // Working var
        ActionRes res = KO;
        // Drop previous staging collection if exists
        log.debug("[{}] Dropping previous branch for backup if exists", getConfig().getTitle());
        try {
            // Drop it
            getBridge().getRepository().drop(getConfig().getBackup());
            // Set flag
            res = OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to drop previous backup collection", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    @Override
    protected ActionRes onStaging() {
        // Working var
        ActionRes res;
        // Create empty staging
        res = createStaging();
        // Go on only if staging was created
        if(res == OK) {
            // Create backup from production
            res = createBackup();
        }
        return res;
    }

    @Override
    protected ActionRes onProcessing() {
        // Working var
        ActionRes res = KO;
        // Log
        log.debug("[{}] Start processing data on staging", getConfig().getTitle());
        try {
            // Sync
            csv.syncCodeSystemVersions(
                getConfig().getParent().getStaging(),
                getCollection()
            );
            // Set flag
            res = OK;
            log.debug("[{}] Operations have been applied on staging", getConfig().getTitle());
        }catch (Exception ex) {
            log.error(
                format("[%s] Unable to process collection due to unknown error", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }

    public CallbackRes onRecovery() {
        // Working var
        CallbackRes res = CB_KO;
        // Try to restore back-up on error
        try {
            // Log me
            log.debug("[{}] Setting up the back-up branch", getConfig().getTitle());
            // Swap
            getBridge().getRepository().rename(getConfig().getBackup(), getConfig().getProduction());
            // Set flag
            res = CB_OK;
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to restore back-up collection", getConfig().getTitle()),
                ex
            );
        }

        return res;
    }

    @Override
    public DictionaryCFG getConfig() {
        return (DictionaryCFG) super.getConfig();
    }

    @Override
    protected String[] getSteps() {
        return DictionaryActionEDS.defaults();
    }

    protected ActionRes createBackup() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Creating backup branch", getConfig().getTitle());
        // Let's clone
        try {
            // Verify production exists
            if(getBridge().getRepository().exists(getConfig().getProduction())) {
                // Create back-up
                getBridge().getRepository().clone(getConfig().getProduction(), getConfig().getBackup());
            } else {
                log.debug("[{}] Database seems empty, creating an empty backup branch", getConfig().getTitle());
                // Create empty back-up
                getBridge().getRepository().create(getConfig().getBackup());
            }
            // Set flag
            res = OK;
            log.debug("[{}] Creating backup branch", getConfig().getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to duplicate collection", getConfig().getTitle()),
                ex
            );
        }
        return res;
    }
    protected ActionRes createStaging() {
        // Working var
        ActionRes res = KO;
        log.debug("[{}] Creating an empty new staging collection", getConfig().getTitle());
        try {
            // Assign the collection
            setCollection(getBridge().getRepository().create(getConfig().getStaging()));
            // Set the flag
            res = OK;
            log.debug("[{}] Staging branch ready", getConfig().getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[%s] Unable to create staging branch", getConfig().getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }
}