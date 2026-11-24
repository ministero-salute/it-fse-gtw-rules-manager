package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.CallbackRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionCallbackEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.DictionaryExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.EnginesExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.FhirStructuresExecutors;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchemaExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.SchematronExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.TermsExecutor;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;

@Tag("ISOLATION")
@SpringJUnitConfig(InvokeEDSClientScheduler.class)
class OrchestrationIsolationTest {
    @Autowired InvokeEDSClientScheduler scheduler;
    @Autowired ConfigurableApplicationContext context;
    @MockitoBean ProfileUtility profiles;
    @MockitoBean SchemaExecutor schema;
    @MockitoBean SchematronExecutor schematron;
    @MockitoBean TermsExecutor terms;
    @MockitoBean FhirStructuresExecutors fhir;
    @MockitoBean EnginesExecutor engines;
    @MockitoBean DictionaryExecutor dictionary;
    @BeforeEach void defaults() {
        ChangesetCFG cfg = new ChangesetCFG("http://eds/status", "http://eds/data/", "schema") {};
        for (var e : java.util.List.of(schema, schematron, terms, fhir)) {
            when(e.getConfig()).thenReturn(cfg); when(e.execute()).thenReturn(OK);
        }
        when(dictionary.execute()).thenReturn(OK); when(engines.execute()).thenReturn(OK);
        when(dictionary.onRecovery()).thenReturn(CallbackRes.CB_OK); when(engines.onRecovery()).thenReturn(CallbackRes.CB_OK);
        when(dictionary.onCleanBackup()).thenReturn(OK); when(engines.onCleanBackup()).thenReturn(OK);
    }
    @Test void runsInDependencyOrderAndWiresDerivedCollectionCallbacks() {
        assertFalse(scheduler.isRunning());
        scheduler.asyncAction();
        assertFalse(scheduler.isRunning());
        var order = inOrder(schema, schematron, terms, fhir);
        order.verify(schema).execute(); order.verify(schematron).execute(); order.verify(terms).execute(); order.verify(fhir).execute();
        ArgumentCaptor<IActionCallbackEDS> callback = ArgumentCaptor.forClass(IActionCallbackEDS.class);
        verify(terms).setOnBeforeSwap(callback.capture()); assertEquals(CallbackRes.CB_OK, callback.getValue().execute());
        verify(terms).setOnFailedSwap(callback.capture()); assertEquals(CallbackRes.CB_OK, callback.getValue().execute());
        verify(terms).setOnSuccessSwap(callback.capture()); assertEquals(CallbackRes.CB_OK, callback.getValue().execute());
        verify(fhir).setOnBeforeSwap(callback.capture()); assertEquals(CallbackRes.CB_OK, callback.getValue().execute());
        verify(fhir).setOnFailedSwap(callback.capture()); assertEquals(CallbackRes.CB_OK, callback.getValue().execute());
        verify(fhir).setOnSuccessSwap(callback.capture()); assertEquals(CallbackRes.CB_OK, callback.getValue().execute());
    }
    @ParameterizedTest @ValueSource(booleans={false,true})
    void exhaustsRetriesThenRecoversAndContinuesWithOtherCollections(boolean recovered) {
        when(schema.execute()).thenAnswer(c -> { assertTrue(scheduler.isRunning()); return KO; });
        when(schema.recovery()).thenReturn(recovered ? OK : KO);
        scheduler.action();
        verify(schema, times(4)).execute(); verify(schema, times(recovered ? 1 : 4)).recovery();
        verify(fhir).execute(); assertFalse(scheduler.isRunning());
    }
    @ParameterizedTest @ValueSource(strings={"test", "dev", "production"})
    void applicationStartupDoesNotRunScheduledUpdates(String profile) {
        when(profiles.isTestProfile()).thenReturn(profile.equals("test"));
        when(profiles.isDevOrDockerProfile()).thenReturn(profile.equals("dev"));

        context.publishEvent(new ApplicationStartedEvent(
                new SpringApplication(InvokeEDSClientScheduler.class), new String[0], context, java.time.Duration.ZERO));

        verify(schema, never()).execute();
        verify(schematron, never()).execute();
        verify(terms, never()).execute();
        verify(fhir, never()).execute();
        assertFalse(scheduler.isRunning());
    }
}
