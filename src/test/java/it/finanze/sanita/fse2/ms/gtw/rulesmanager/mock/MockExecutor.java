/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base.BaseSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionStepEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.IChunkHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.chunk.ISnapshotHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.BridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import org.bson.Document;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

@Component
public class MockExecutor extends ExecutorEDS<MockData> implements ISnapshotHandlerEDS {

    public static final String EMPTY_STEP = "EMPTY_STEP";

    private boolean verify;

    protected MockExecutor(MockConfig cfg, BridgeEDS bridge) {
        super(cfg, bridge);
    }

    @Override
    protected ParameterizedTypeReference<ChangeSetDTO<MockData>> getType() {
        return new ParameterizedTypeReference<ChangeSetDTO<MockData>>() {};
    }

    public ActionRes onChangeset(IActionFnEDS<Date> hnd) {
        return super.onChangeset(hnd);
    }

    public IActionFnEDS<Date> onLastUpdateProd() {
        return super.onLastUpdateProd();
    }
    public IActionFnEDS<Date> onLastUpdateStaging() {
        return super.onLastUpdateStaging();
    }

    public ActionRes onChangesetEmpty() {
        return super.onChangesetEmpty();
    }

    public ActionRes onClean() {
        return super.onClean();
    }

    public ActionRes onProcessing(boolean verify) {
        this.verify = verify;
        return super.onProcessing();
    }

    public ActionRes onReset() {
        return super.onReset();
    }

    public ActionRes onStaging() {
        return super.onStaging();
    }

    public ActionRes onSwap(MongoCollection<Document> staging) {
        // Overwrite value only if test is launch alone
        if(this.getCollection() == null) this.setCollection(staging);
        return super.onSwap();
    }

    public ActionRes onChangesetAlignment() {
        return super.onChangesetAlignment();
    }

    public ActionRes onSync() {
        return super.onSync();
    }

    public ActionRes onVerify() {
        return super.onVerify();
    }

    @Override
    public IActionHandlerEDS<MockData> onInsertion() {
        return (staging, info) -> verify ? OK : KO;
    }

    @Override
    public IActionHandlerEDS<MockData> onDeletions() {
        return (staging, info) -> verify ? OK : KO;
    }

    @Override
    public ActionRes onVerifyProductionSize() {
        return super.onVerifyProductionSize();
    }

    @Override
    public IChunkHandlerEDS onChunkInsertion() {
        return (staging, snapshot, chunk, max) -> new SimpleImmutableEntry<>(verify ? OK : KO, 0);
    }

    @Override
    public IChunkHandlerEDS onChunkDeletions() {
        return (staging, snapshot, chunk, max) -> new SimpleImmutableEntry<>(verify ? OK : KO, 0);
    }

    public static ChangeSetDTO<MockData> createChangeset(int insert, int delete, long size) {

        List<BaseSetDTO<MockData>> insertions = new ArrayList<>();
        List<BaseSetDTO<MockData>> deletions = new ArrayList<>();

        for(int i = 0; i < insert; ++i) {
            insertions.add(new BaseSetDTO<>("testId", new MockData("insert - " + i)));
        }
        for(int i = 0; i < delete; ++i) {
            deletions.add(new BaseSetDTO<>("testId", new MockData("delete - " + i)));
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

    public static ChangeSetDTO<MockData> emptyChangeset() {
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
    protected List<Map.Entry<String, IActionStepEDS>> getCustomSteps() {
        List<Map.Entry<String, IActionStepEDS>> steps = new ArrayList<>();
        steps.add(new SimpleImmutableEntry<>(EMPTY_STEP, this::emptyStep));
        return steps;
    }
}
