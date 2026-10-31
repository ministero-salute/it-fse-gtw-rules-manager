package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.core.*;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.read.ListAppender;
import com.google.gson.JsonParser;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ResultLogEnum;

@Tag("ISOLATION")
@SpringJUnitConfig(LoggerHelper.class)
@TestPropertySource(properties={"log.kafka-log.enable=true","spring.application.name=rules-isolation"})
class LoggingIsolationTest {
    @Autowired LoggerHelper helper;
    @MockitoBean IConfigClient configClient;
    @MockitoBean IConfigSRV config;

    @Test void emitsStructuredMessagesAtEveryLevelAndHonorsPersistenceSwitch() {
        when(config.isControlLogPersistenceEnable()).thenReturn(true);
        when(configClient.getGatewayName()).thenReturn("gateway");
        Logger kafka = (Logger) org.slf4j.LoggerFactory.getLogger("kafka-logger");
        Level previous = kafka.getLevel(); kafka.setLevel(Level.TRACE);
        ListAppender<ILoggingEvent> output = new ListAppender<>(); output.start(); kafka.addAppender(output);
        try {
            Date date = new Date(1000);
            helper.trace("CONTROL", "message", "operation", ResultLogEnum.OK, date);
            helper.debug("CONTROL", "message", "operation", ResultLogEnum.OK, date);
            helper.info("CONTROL", "message", "operation", ResultLogEnum.OK, date);
            helper.warn("CONTROL", "message", "operation", ResultLogEnum.KO, date);
            helper.error("CONTROL", "message", "operation", ResultLogEnum.KO, date);
            assertEquals(List.of(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR),
                    output.list.stream().map(ILoggingEvent::getLevel).toList());
            for (var event : output.list) {
                var json = JsonParser.parseString(event.getFormattedMessage()).getAsJsonObject();
                assertEquals("message", json.get("message").getAsString());
                assertEquals("gateway", json.get("gateway_name").getAsString());
                assertEquals("rules-isolation", json.get("microservice_name").getAsString());
                assertEquals("operation", json.get("operation").getAsString());
                assertTrue(json.has("op_timestamp_start"));
            }
            verify(configClient).getGatewayName();
            when(config.isControlLogPersistenceEnable()).thenReturn(false);
            helper.trace(null,null,null,null,null); helper.debug(null,null,null,null,null);
            helper.info(null,null,null,null,null); helper.warn(null,null,null,null,null); helper.error(null,null,null,null,null);
            assertEquals(5, output.list.size());
        } finally { kafka.detachAppender(output); kafka.setLevel(previous); output.stop(); }
    }

    @Test void asynchronousDeliveryReportsCallbackAndImmediateTimeoutFailures() {
        Producer<String,String> producer = mock(Producer.class);
        ProducerRecord<String,String> record = new ProducerRecord<>("topic", "payload");
        FailedDeliveryCallback<String> failed = mock(FailedDeliveryCallback.class);
        AsynchronousDeliveryStrategy strategy = new AsynchronousDeliveryStrategy();
        assertTrue(strategy.send(producer, record, "event", failed));
        ArgumentCaptor<Callback> callback = ArgumentCaptor.forClass(Callback.class);
        verify(producer).send(eq(record), callback.capture());
        callback.getValue().onCompletion(null, null); verifyNoInteractions(failed);
        RuntimeException cause = new RuntimeException("failure");
        callback.getValue().onCompletion(null, cause); verify(failed).onFailedDelivery("event", cause);
        org.apache.kafka.common.errors.TimeoutException timeout = new org.apache.kafka.common.errors.TimeoutException("timeout");
        when(producer.send(eq(record), any(Callback.class))).thenThrow(timeout);
        assertFalse(strategy.send(producer, record, "event", failed));
        verify(failed).onFailedDelivery("event", timeout);
    }

    @Test void blockingDeliveryWaitsUsingConfiguredTimeoutAndHandlesFailedFuture() throws Exception {
        Producer<String,String> producer = mock(Producer.class);
        Future<RecordMetadata> future = mock(Future.class);
        ProducerRecord<String,String> record = new ProducerRecord<>("topic", "payload");
        FailedDeliveryCallback<String> failed = mock(FailedDeliveryCallback.class);
        when(producer.send(record)).thenReturn(future);
        BlockingDeliveryStrategy strategy = new BlockingDeliveryStrategy();
        assertTrue(strategy.send(producer, record, "event", failed)); verify(future).get();
        strategy.setTimeout(50); assertEquals(50, strategy.getTimeout());
        assertTrue(strategy.send(producer, record, "event", failed)); verify(future).get(50, TimeUnit.MILLISECONDS);
        when(future.get(50, TimeUnit.MILLISECONDS)).thenThrow(new TimeoutException("timeout"));
        assertFalse(strategy.send(producer, record, "event", failed)); verify(failed).onFailedDelivery(eq("event"), any(TimeoutException.class));
        doThrow(new InterruptedException()).when(future).get(50, TimeUnit.MILLISECONDS);
        try {
            assertThrows(it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException.class,
                    () -> strategy.send(producer, record, "event", failed));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally { Thread.interrupted(); }
    }

    @Test void hostKeyUsesContextHostnameAndMissingHostnameDoesNotInventAKey() {
        Context missing = mock(Context.class);
        HostNameKeyingStrategy strategy = new HostNameKeyingStrategy();
        strategy.setContext(missing); strategy.setContext(missing);
        assertNull(strategy.createKey("event")); strategy.start(); assertTrue(strategy.isStarted()); strategy.stop();
        ContextBase context = new ContextBase(); context.putProperty(CoreConstants.HOSTNAME_KEY, "host");
        HostNameKeyingStrategy named = new HostNameKeyingStrategy(); named.setContext(context);
        assertArrayEquals(ByteBuffer.allocate(4).putInt("host".hashCode()).array(), named.createKey(null));
        assertNull(new NoKeyKeyingStrategy().createKey(null));
    }

    @ParameterizedTest @ValueSource(booleans={true,false})
    void appenderEncodesRecordsDefersKafkaLogsAndClosesProducer(boolean timestamps) {
        Producer<byte[],byte[]> producer = mock(Producer.class);
        TestAppender appender = configured(producer);
        appender.setAppendTimestamp(timestamps); appender.setPartition(-1);
        DeliveryStrategy delivery = mock(DeliveryStrategy.class); appender.setDeliveryStrategy(delivery);
        appender.start(); assertTrue(appender.isStarted());
        LoggingEvent deferred = event("org.apache.kafka.clients.NetworkClient");
        appender.doAppend(deferred); verifyNoInteractions(producer, delivery);
        appender.doAppend(event("application"));
        ArgumentCaptor<ProducerRecord> record = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(delivery, times(2)).send(eq(producer), record.capture(), any(), any());
        assertEquals("logs", record.getValue().topic()); assertNull(record.getValue().partition());
        assertArrayEquals(new byte[]{1,2}, (byte[])record.getValue().value());
        assertEquals(timestamps ? 1234L : null, record.getValue().timestamp());
        appender.stop(); verify(producer).close(); assertFalse(appender.isStarted());
    }

    @Test void appenderFallsBackWhenProducerCreationFailsAndManagesAttachedAppenders() {
        TestAppender appender = configured(null);
        ListAppender<ILoggingEvent> fallback = new ListAppender<>(); fallback.setName("fallback"); fallback.start();
        appender.addAppender(fallback); assertTrue(appender.isAttached(fallback));
        assertSame(fallback, appender.getAppender("fallback")); assertTrue(appender.iteratorForAppenders().hasNext());
        appender.start(); appender.doAppend(event("application")); assertEquals(1, fallback.list.size());
        assertTrue(appender.detachAppender("fallback")); appender.addAppender(fallback);
        assertTrue(appender.detachAppender(fallback)); appender.addAppender(fallback);
        appender.detachAndStopAllAppenders(); assertFalse(fallback.isStarted()); appender.stop();
    }

    @Test void appenderRejectsMissingPrerequisitesAndSurvivesProducerCloseFailure() {
        TestAppender invalid = new TestAppender(null); invalid.setContext(new ContextBase()); invalid.start();
        assertFalse(invalid.isStarted());
        Producer<byte[],byte[]> producer = mock(Producer.class);
        TestAppender appender = configured(producer); appender.start(); appender.doAppend(event("application"));
        doThrow(new KafkaException("close failure")).when(producer).close();
        assertDoesNotThrow(appender::stop);
        assertEquals("broker:9092", appender.getProducerConfig().get("bootstrap.servers"));
    }

    static LoggingEvent event(String logger) {
        LoggingEvent event = new LoggingEvent(); event.setLoggerName(logger); event.setTimeStamp(1234); return event;
    }
    static TestAppender configured(Producer<byte[],byte[]> producer) {
        TestAppender appender = new TestAppender(producer); appender.setContext(new ContextBase());
        appender.addProducerConfig("ignored"); appender.addProducerConfig("bootstrap.servers=broker:9092");
        appender.setTopic("logs"); Encoder<ILoggingEvent> encoder = mock(Encoder.class);
        when(encoder.encode(any())).thenReturn(new byte[]{1,2}); appender.setEncoder(encoder); return appender;
    }
    static class TestAppender extends KafkaAppender<ILoggingEvent> {
        final Producer<byte[],byte[]> producer;
        TestAppender(Producer<byte[],byte[]> producer) { this.producer = producer; }
        @Override protected Producer<byte[],byte[]> createProducer() {
            if (producer == null) throw new KafkaException("broker unavailable"); return producer;
        }
    }
}
