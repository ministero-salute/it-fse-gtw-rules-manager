package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util.ProcessResult;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.IExecutableEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.IRecoverableEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.plan.PlanEDS;

@Tag("ISOLATION")
class SchedulerIsolationTest {
    private final Logger log = mock(Logger.class);
    private final ChangesetCFG config = new ChangesetCFG("http://eds/status", "http://eds/data/", "schema") {};

    @ParameterizedTest
    @EnumSource(ActionRes.class)
    void preservesStepOrderNotifiesListenersAndStopsAtNonOk(ActionRes result) {
        PlanEDS plan = new PlanEDS();
        List<String> events = new ArrayList<>();
        plan.addOnStepListener((name, value) -> events.add(name + ":" + value));
        plan.addOnPlanListener(value -> events.add("plan:" + value));
        plan.addStep("first", () -> { events.add("first"); return OK; });
        plan.addStep("second", () -> { events.add("second"); return result; });
        plan.addStep("last", () -> { events.add("last"); return OK; });

        assertEquals(result == OK ? OK : result, plan.execute());
        List<String> expected = new ArrayList<>(List.of("first", "first:OK", "second", "second:" + result));
        if (result == OK) expected.addAll(List.of("last", "last:OK"));
        expected.add("plan:" + result);
        assertEquals(expected, events);
    }

    @Test
    void emptyPlanSucceedsAndNullListenersAreRejected() {
        PlanEDS plan = new PlanEDS();
        AtomicInteger completed = new AtomicInteger();
        plan.addOnPlanListener(value -> { assertEquals(OK, value); completed.incrementAndGet(); });
        assertEquals(OK, plan.execute());
        assertEquals(1, completed.get());
        assertThrows(NullPointerException.class, () -> plan.addOnPlanListener(null));
        assertThrows(NullPointerException.class, () -> plan.addOnStepListener(null));
    }

    @Test
    void retriesExceptionsAndNullResultsUntilAValueArrives() {
        AtomicInteger attempts = new AtomicInteger();
        Optional<String> result = IActionRetryEDS.retryOnException(() -> {
            int attempt = attempts.incrementAndGet();
            if (attempt == 1) throw new IllegalStateException("transient failure");
            return attempt == 2 ? null : "ready";
        }, config, log);
        assertEquals(Optional.of("ready"), result);
        assertEquals(3, attempts.get());
    }

    @Test
    void exhaustsRetryBudgetAndDoesNotRunWithZeroAttempts() {
        AtomicInteger attempts = new AtomicInteger();
        assertTrue(IActionRetryEDS.retryOnException(() -> {
            attempts.incrementAndGet();
            throw new IllegalStateException("persistent failure");
        }, config, log, 2).isEmpty());
        assertEquals(2, attempts.get());
        assertTrue(IActionRetryEDS.retryOnException(() -> {
            fail("zero budget must not invoke callback");
            return "unexpected";
        }, config, log, 0).isEmpty());
    }

    @Test
    void executorRetriesOnlyKoAndIncludesInitialAttempt() {
        IExecutableEDS executor = mock(IExecutableEDS.class);
        when(executor.execute()).thenReturn(KO, KO, EXIT);
        assertEquals(EXIT, IActionRetryEDS.retryExecutorOnException(executor, log, 3));
        verify(executor, times(3)).execute();
        reset(executor);
        when(executor.execute()).thenReturn(KO);
        assertEquals(KO, IActionRetryEDS.retryExecutorOnException(executor, log));
        verify(executor, times(ChangesetCFG.RETRY_VALUE + 1)).execute();
    }

    @Test
    void recoveryStopsOnSuccessAndHonorsRetryBudget() {
        IRecoverableEDS executor = mock(IRecoverableEDS.class);
        when(executor.recovery()).thenReturn(KO, OK);
        assertEquals(OK, IActionRetryEDS.retryRecoveryOnException(executor, log));
        verify(executor, times(2)).recovery();
        reset(executor);
        when(executor.recovery()).thenReturn(KO);
        assertEquals(KO, IActionRetryEDS.retryRecoveryOnException(executor, log, 0));
        verify(executor).recovery();
    }

    @Test
    void validatesProcessedOperationTotalsAndFormatsDiagnostics() {
        ProcessResult valid = new ProcessResult(3, 2, 3, 2);
        assertTrue(valid.isValid());
        assertEquals(5, valid.getOperations());
        assertEquals(5, valid.getExpectedOperations());
        assertEquals("[ Insertions: 3 | Deletions: 2 ]", valid.getInfo());
        assertEquals(valid.getInfo(), valid.getExpectedInfo());
        assertFalse(new ProcessResult(2, 2, 3, 2).isValid());
    }
}
