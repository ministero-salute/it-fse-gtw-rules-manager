package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl.EDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.*;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;

@Tag("ISOLATION")
class EdsClientIsolationTest {
    private final ChangesetCFG spec = new ChangesetCFG("http://eds/status", "http://eds/data/", "schema") {};
    private final ChunkChangesetCFG chunks = new ChunkChangesetCFG("http://eds/snapshot",
            "http://eds/insert/", "http://eds/delete/", "terminology") {};
    private final EDSClient client = new EDSClient();
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate rest = new RestTemplate();
        server = MockRestServiceServer.bindTo(rest).build();
        ReflectionTestUtils.setField(client, "client", rest);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void retrievesAndDeserializesEveryEdsEndpoint(int endpoint) throws Exception {
        String url = endpointUrl(endpoint);
        server.expect(requestTo(url)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(endpoint == 4 ? "[\"one\",\"two\"]" : "payload", MediaType.APPLICATION_JSON));
        Object response = invoke(endpoint);
        assertEquals(endpoint == 4 ? List.of("one", "two") : "payload", response);
        server.verify();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void wrapsHttpFailureAtEveryEndpoint(int endpoint) throws Exception {
        server.expect(requestTo(endpointUrl(endpoint))).andRespond(withServerError());
        EdsClientException failure = assertThrows(EdsClientException.class, () -> invoke(endpoint));
        assertEquals("Error while executing the request", failure.getMessage());
        assertInstanceOf(HttpServerErrorException.class, failure.getCause());
        server.verify();
    }

    @Test
    void returnsNullForNoContent() throws Exception {
        server.expect(requestTo("http://eds/data/id")).andRespond(withNoContent());
        assertNull(client.getDocument(spec, "id", String.class));
        server.verify();
    }

    @Test
    void formatsUtcSyncQueryAndRejectsInvalidUrlsWithoutNetworkAccess() throws Exception {
        assertEquals("http://eds/status?lastUpdate=1970-01-01T00:00:01Z",
                spec.getStatusReq(new Date(1000)).toString());
        assertEquals("null", spec.getLastUpdateFormatted(null));
        assertEquals("schema-staging", spec.getStaging());
        assertEquals("schema-backup", spec.getBackup());
        assertThrows(NullPointerException.class, spec::getParent);
        ChangesetCFG invalid = new ChangesetCFG("not a url", "bad url", "schema") {};
        EdsClientException failure = assertThrows(EdsClientException.class,
                () -> client.getDocument(invalid, "id", String.class));
        assertNotNull(failure.getCause());
        server.verify();
    }

    private String endpointUrl(int endpoint) {
        return switch (endpoint) {
            case 0 -> "http://eds/data/id";
            case 1 -> "http://eds/snapshot";
            case 2 -> "http://eds/insert/id/2";
            case 3 -> "http://eds/delete/id/2";
            default -> "http://eds/status";
        };
    }

    private Object invoke(int endpoint) throws EdsClientException {
        return switch (endpoint) {
            case 0 -> client.getDocument(spec, "id", String.class);
            case 1 -> client.getSnapshot(chunks, null, String.class);
            case 2 -> client.getChunkIns(chunks, "id", 2, String.class);
            case 3 -> client.getChunkDel(chunks, "id", 2, String.class);
            default -> client.getStatus(spec, null, new ParameterizedTypeReference<List<String>>() {});
        };
    }
}
