package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.OpenTelemetry;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.handler.ExceptionCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;

@Tag("ISOLATION")
@SpringJUnitConfig({SchedulerCTL.class, ExceptionCTL.class, LivenessCheckCTL.class})
class ControllersIsolationTest {
    @Autowired SchedulerCTL controller;
    @Autowired ExceptionCTL advice;
    @Autowired LivenessCheckCTL health;
    @MockitoBean InvokeEDSClientScheduler scheduler;
    @MockitoBean Tracer tracer;
    MockMvc mvc;
    @BeforeEach void setup() { mvc = MockMvcBuilders.standaloneSetup(controller, health).setControllerAdvice(advice).build(); }

    @Test void queuesUpdateAndReturnsTraceFields() throws Exception {
        when(tracer.spanBuilder(anyString())).thenAnswer(c -> OpenTelemetry.noop().getTracer("test").spanBuilder("request"));
        mvc.perform(post("/v1/run-scheduler")).andExpect(status().isOk())
                .andExpect(jsonPath("traceID").isString()).andExpect(jsonPath("spanID").isString());
        verify(scheduler).asyncAction();
    }
    @Test void rejectsRunningSchedulerWithProblemResponse() throws Exception {
        when(scheduler.isRunning()).thenReturn(true);
        when(tracer.spanBuilder(anyString())).thenAnswer(c -> OpenTelemetry.noop().getTracer("test").spanBuilder("error"));
        mvc.perform(post("/v1/run-scheduler")).andExpect(status().isLocked())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("status").value(423)).andExpect(jsonPath("type").value("/msg/io"))
                .andExpect(jsonPath("instance").value("/queue")).andExpect(jsonPath("detail").isString());
        verify(scheduler, never()).asyncAction();
    }
    @Test void absentSpanBuilderIsHandledForSuccessAndFailure() throws Exception {
        mvc.perform(post("/v1/run-scheduler")).andExpect(status().isOk());
        when(scheduler.isRunning()).thenReturn(true);
        mvc.perform(post("/v1/run-scheduler")).andExpect(status().isLocked());
    }
    @Test void livenessReportsUp() throws Exception {
        mvc.perform(get("/status")).andExpect(status().isOk()).andExpect(jsonPath("status").value("UP"));
    }
}
