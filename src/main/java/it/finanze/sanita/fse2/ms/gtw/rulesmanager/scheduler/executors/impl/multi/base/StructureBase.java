package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.base;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IStructureRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.StructureAction.CHANGESET_PARENT;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi.StructureAction.STAGING_PARENT;
import static java.lang.String.format;

@Slf4j
public abstract class StructureBase<T> extends ExecutorEDS<T> {

    protected StructureBase(ChangesetCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    protected ActionRes onChangesetParent() {
        return onChangeset(this.onLastUpdateParent());
    }

    private IActionFnEDS<Date> onLastUpdateParent() {
        return () -> getParent().getLastSyncFromParent(
            getConfig().getParent(),
            getConfig().getProduction()
        );
    }

    protected abstract IStructureRepo getParent();

    protected ActionRes onStagingParent() {
        // Working vars
        BridgeEDS bridge = getBridge();
        ChangesetCFG config = getConfig();
        ActionRes res = KO;
        // No need to check for field existence because we would have quit it early
        // due to EMPTY flag if no documents where available even if there was
        // only an empty document
        try {
            if(bridge.getRepository().exists(config.getParent())) {
                res = cloneStagingParent();
            } else {
                res = emptyStaging();
            }
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to verify if production branch exists", config.getTitle()),
                ex
            );
        }
        return res;
    }

    private ActionRes cloneStagingParent() {
        // Working var
        ActionRes res = KO;
        // Log me
        log.debug("[EDS][{}] Cloning current branch for staging", getConfig().getTitle());
        // Let's clone
        try {
            // Assign the collection
            setCollection(getParent().cloneFromParent(
                getConfig().getParent(),
                getConfig().getStaging(),
                getConfig().getProduction()
            ));
            // Set flag
            res = OK;
            // Log me
            log.debug("[EDS][{}] Staging branch ready", getConfig().getTitle());
        } catch (EdsDbException ex) {
            log.error(
                format("[EDS][%s] Unable to duplicate collection", getConfig().getTitle()),
                ex
            );
        }
        // Bye
        return res;
    }

    @Override
    protected String[] getSteps() {
        return new String[] {
            RESET,
            CLEAN,
            CHANGESET_PARENT,
            CHANGESET_EMPTY,
            STAGING_PARENT,
            PROCESSING,
            VERIFY,
            SYNC,
            CHANGESET_STAGING,
            CHANGESET_ALIGNMENT
        };
    }

    @Override
    protected List<Map.Entry<String, IActionStepEDS>> getCustomSteps() {
        // Create steps list
        List<Map.Entry<String, IActionStepEDS>> custom = new ArrayList<>();
        // Add new steps
        custom.add(new SimpleImmutableEntry<>(CHANGESET_PARENT, this::onChangesetParent));
        custom.add(new SimpleImmutableEntry<>(STAGING_PARENT, this::onStagingParent));
        // Return
        return custom;
    }

    public ActionRes clean() {
        return this.onClean();
    }
}
