package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.SchemaCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.TerminologyCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
@ActiveProfiles(Constants.Profile.TEST)
class EDSClientTest {

    @Autowired
    private SchemaCFG configuration;
    @Autowired
    private TerminologyCFG chunks;
    @Autowired
    private IEDSClient client;
    @MockBean
    private RestTemplate rest;

    @Test
    void status() {
        // Common var
        ParameterizedTypeReference<Object> type = new ParameterizedTypeReference<Object>() {};
        // Test OK
        assertDoesNotThrow(() -> {
            // Mock response
            when(rest.exchange(any(), any(), any(), eq(type))).thenReturn(
                new ResponseEntity<>(new Object(), OK)
            );
            // Execute
            Object res = client.getStatus(configuration, new Date(), type);
            // Verify response
            assertNotNull(res);
        });
        // Test KO
        // Mock error
        when(rest.exchange(any(), any(), any(), eq(type))).thenThrow(new RuntimeException("Test error"));
        // Check exception
        assertThrows(EdsClientException.class, () -> client.getStatus(configuration, new Date(), type));
    }

    @Test
    void document() {
        // Test OK
        assertDoesNotThrow(() -> {
            // Mock response
            when(rest.getForEntity(any(), eq(Object.class))).thenReturn(
                new ResponseEntity<>(new Object(), OK)
            );
            // Execute
            Object res = client.getDocument(configuration, "id", Object.class);
            // Verify response
            assertNotNull(res);
        });
        // Test KO
        // Mock error
        when(rest.getForEntity(any(), eq(Object.class))).thenThrow(new RuntimeException("Test error"));
        // Check exception
        assertThrows(EdsClientException.class, () -> client.getDocument(configuration, "id", Object.class));
    }

    @Test
    void snapshot() {
        // Test OK
        assertDoesNotThrow(() -> {
            // Mock response
            when(rest.getForEntity(any(), eq(Object.class))).thenReturn(
                new ResponseEntity<>(new Object(), OK)
            );
            // Execute
            Object res = client.getSnapshot(chunks, new Date(), Object.class);
            // Verify response
            assertNotNull(res);
        });
        // Test KO
        // Mock error
        when(rest.getForEntity(any(), eq(Object.class))).thenThrow(new RuntimeException("Test error"));
        // Check exception
        assertThrows(EdsClientException.class, () -> client.getSnapshot(chunks, new Date(), Object.class));
    }

    @Test
    void chunkInsert() {
        // Test OK
        assertDoesNotThrow(() -> {
            // Mock response
            when(rest.getForEntity(any(), eq(Object.class))).thenReturn(
                new ResponseEntity<>(new Object(), OK)
            );
            // Execute
            Object res = client.getChunkIns(chunks, "id", 0, Object.class);
            // Verify response
            assertNotNull(res);
        });
        // Test KO
        // Mock error
        when(rest.getForEntity(any(), eq(Object.class))).thenThrow(new RuntimeException("Test error"));
        // Check exception
        assertThrows(EdsClientException.class, () -> client.getChunkIns(chunks, "id", 0, Object.class));
    }

    @Test
    void chunkDelete() {
        // Test OK
        assertDoesNotThrow(() -> {
            // Mock response
            when(rest.getForEntity(any(), eq(Object.class))).thenReturn(
                new ResponseEntity<>(new Object(), OK)
            );
            // Execute
            Object res = client.getChunkDel(chunks, "id", 0, Object.class);
            // Verify response
            assertNotNull(res);
        });
        // Test KO
        // Mock error
        when(rest.getForEntity(any(), eq(Object.class))).thenThrow(new RuntimeException("Test error"));
        // Check exception
        assertThrows(EdsClientException.class, () -> client.getChunkDel(chunks, "id", 0, Object.class));
    }

}
