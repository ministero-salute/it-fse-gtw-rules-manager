package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.mock;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionFnEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.ExecutorBridgeEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.ExecutorEDS;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;

@Slf4j
@Component
public class MockExecutor extends ExecutorEDS<MockData> {

    private boolean verify;

    protected MockExecutor(MockConfig cfg, ExecutorBridgeEDS bridge) {
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
    public IActionHandlerEDS<MockData> onModification() {
        return (staging, info) -> verify ? OK : KO;
    }

    @Override
    public IActionHandlerEDS<MockData> onDeletions() {
        return (staging, info) -> verify ? OK : KO;
    }

    public static ChangeSetDTO<MockData> emptyChangeset() {
        return new ChangeSetDTO<>(
            "",
            "",
            new Date(),
            new Date(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            0
        );
    }

    public static ChangeSetDTO<MockData> createChangeset(int insert, int update, int delete) {

        List<MockData> insertions = new ArrayList<>();
        List<MockData> modifications = new ArrayList<>();
        List<MockData> deletions = new ArrayList<>();

        for(int i = 0; i < insert; ++i) {
            insertions.add(new MockData("insert - " + i));
        }
        for(int i = 0; i < update; ++i) {
            modifications.add(new MockData("update - " + i));
        }
        for(int i = 0; i < delete; ++i) {
            deletions.add(new MockData("delete - " + i));
        }

        return new ChangeSetDTO<>(
            "randomTraceId",
            "randomSpanId",
            new Date(),
            new Date(),
            insertions,
            deletions,
            modifications,
            insert + update + delete
        );
    }
}
