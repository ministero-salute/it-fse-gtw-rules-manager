package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionCallbackEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;
import org.mockito.ArgumentCaptor;

@Tag("ISOLATION")
@SpringJUnitConfig(InvokeEDSClientScheduler.class)
class OrchestrationIsolationTest {
    @Autowired InvokeEDSClientScheduler scheduler;
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
    @Test void initializationSkipsLegacyTestAndDevButRunsInProduction() {
        when(profiles.isTestProfile()).thenReturn(true); scheduler.initialize(); verify(schema, never()).execute();
        when(profiles.isTestProfile()).thenReturn(false); when(profiles.isDevOrDockerProfile()).thenReturn(true);
        scheduler.initialize(); verify(schema, never()).execute();
        when(profiles.isDevOrDockerProfile()).thenReturn(false); scheduler.initialize(); verify(schema).execute();
    }
}
