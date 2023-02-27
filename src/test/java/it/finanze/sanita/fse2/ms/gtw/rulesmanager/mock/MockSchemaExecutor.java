/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchemaCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.SchemaSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base.BaseSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchemaExecutor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.ActionEDS.RESET;

@Component
public class MockSchemaExecutor extends SchemaExecutor {

    public static final String[] FAKE_STEPS = new String[]{
        RESET
    };

    public static final String[] ERR_FAKE_STEPS = new String[]{
        RESET,
        "NOT_EXISTS_STEP"
    };

    public static final String EMPTY_STEP = "EMPTY_STEP";

    private Boolean verify;

    protected MockSchemaExecutor(SchemaCFG config, BridgeEDS bridge) {
        super(config, bridge);
    }

    @Override
    public String[] getSteps() {
        return super.getSteps();
    }

    @Override
    public String[] getRecoverySteps() {
        return super.getRecoverySteps();
    }

    @Override
    public ActionRes onChangeset(IActionFnEDS<Date> hnd) {
        return super.onChangeset(hnd);
    }

    @Override
    public ActionRes execute() {
        return super.execute();
    }

    @Override
    public ActionRes recovery() {
        return super.recovery();
    }

    @Override
    public ActionRes startup(String[] steps) {
        return super.startup(steps);
    }

    @Override
    public IActionFnEDS<Date> onLastUpdateProd() {
        return super.onLastUpdateProd();
    }

    @Override
    public IActionFnEDS<Date> onLastUpdateStaging() {
        return super.onLastUpdateStaging();
    }

    @Override
    public ActionRes onChangesetEmpty() {
        return super.onChangesetEmpty();
    }

    @Override
    public ActionRes onClean() {
        return super.onClean();
    }

    public ActionRes onProcessing(Boolean verify) {
        this.verify = verify;
        return super.onProcessing();
    }

    @Override
    public ActionRes onReset() {
        return super.onReset();
    }

    @Override
    public ActionRes onStaging() {
        return super.onStaging();
    }

    public ActionRes onSwap(MongoCollection<Document> staging) {
        // Overwrite value only if test is launch alone
        if(this.getCollection() == null) this.setCollection(staging);
        return super.onSwap();
    }

    @Override
    public ActionRes onChangesetAlignment() {
        return super.onChangesetAlignment();
    }

    @Override
    public ActionRes onSync() {
        return super.onSync();
    }

    @Override
    public ActionRes onVerify() {
        return super.onVerify();
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onInsertion() {
        return verify == null ? super.onInsertion() : (staging, info) -> verify ? OK : KO;
    }

    @Override
    public IActionHandlerEDS<SchemaSetDTO> onDeletions() {
        return verify == null ? super.onDeletions() : (staging, info) -> verify ? OK : KO;
    }

    @Override
    public ActionRes onVerifyProductionSize() {
        return super.onVerifyProductionSize();
    }

    public static ChangeSetDTO<SchemaSetDTO> createChangeset(int insert, int delete, long size) {

        List<BaseSetDTO<SchemaSetDTO>> insertions = new ArrayList<>();
        List<BaseSetDTO<SchemaSetDTO>> deletions = new ArrayList<>();

        for(int i = 0; i < insert; ++i) {
            insertions.add(new BaseSetDTO<>(new ObjectId().toHexString(), new SchemaSetDTO()));
        }
        for(int i = 0; i < delete; ++i) {
            deletions.add(new BaseSetDTO<>(new ObjectId().toHexString(), new SchemaSetDTO()));
        }

        return new ChangeSetDTO<>(
            "randomTraceId",
            "randomSpanId",
            new Date(),
            new Date(),
            insertions,
            deletions,
            insert + delete,
            size
        );
    }

    public static ChangeSetDTO<SchemaSetDTO> emptyChangeset() {
        return new ChangeSetDTO<>(
            "",
            "",
            new Date(),
            new Date(),
            new ArrayList<>(),
            new ArrayList<>(),
            0,
            0
        );
    }

    private ActionRes emptyStep() {
        return ActionRes.OK;
    }

    @Override
    public void registerAdditionalHandlers() {
        super.registerAdditionalHandlers();
    }

    @Override
    public List<Map.Entry<String, IActionStepEDS>> getCustomSteps() {
        List<Map.Entry<String, IActionStepEDS>> steps = new ArrayList<>();
        steps.add(new SimpleImmutableEntry<>(EMPTY_STEP, this::emptyStep));
        return steps;
    }
}
