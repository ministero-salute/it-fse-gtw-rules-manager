package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ConfigItemTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl.ConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.ConfigClientRoutes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;

@Tag("ISOLATION")
class ConfigClientIsolationTest {
    private final ConfigClient client = new ConfigClient();
    private final ConfigClientRoutes routes = new ConfigClientRoutes();
    private final MockEnvironment environment = new MockEnvironment();
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestTemplate rest = new RestTemplate();
        ProfileUtility profiles = new ProfileUtility();
        ReflectionTestUtils.setField(profiles, "environment", environment);
        ReflectionTestUtils.setField(routes, "configHost", "http://config");
        ReflectionTestUtils.setField(client, "routes", routes);
        ReflectionTestUtils.setField(client, "profile", profiles);
        ReflectionTestUtils.setField(client, "client", rest);
        server = MockRestServiceServer.bindTo(rest).build();
    }

    @Test
    void retrievesGatewayNameWithoutDevelopmentReachabilityProbe() {
        server.expect(requestTo("http://config/v1/whois"))
                .andRespond(withSuccess("{\"gatewayName\":\"gateway\"}", MediaType.APPLICATION_JSON));
        assertEquals("gateway", client.getGatewayName());
        server.verify();
    }

    @Test
    void returnsDevelopmentFallbackOnlyWhenUnreachable() {
        environment.setActiveProfiles("dev");
        server.expect(requestTo("http://config/status"))
                .andRespond(withException(new IOException("offline")));
        assertEquals(Constants.AppConstants.MOCKED_GATEWAY_NAME, client.getGatewayName());
        server.verify();
    }

    @Test
    void reachableDevelopmentEnvironmentUsesRealGatewayResponse() {
        environment.setActiveProfiles("test");
        server.expect(requestTo(routes.status())).andRespond(withSuccess());
        server.expect(requestTo(routes.whois())).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        assertNull(client.getGatewayName());
        server.verify();
    }

    @Test
    void wrapsMissingGatewayBodyAsBusinessFailure() {
        server.expect(requestTo(routes.whois())).andRespond(withNoContent());
        BusinessException failure = assertThrows(BusinessException.class, client::getGatewayName);
        assertEquals("Error encountered while retrieving gateway name", failure.getMessage());
        assertInstanceOf(BusinessException.class, failure.getCause());
        server.verify();
    }

    @Test
    void readsTypedConfigurationItems() {
        server.expect(requestTo("http://config/v1/config-items?type=RULES_MANAGER"))
                .andRespond(withSuccess("{\"configurationItems\":[{\"key\":\"RULES_MANAGER\",\"items\":{\"flag\":\"true\"}}]}",
                        MediaType.APPLICATION_JSON));
        assertEquals("true", client.getConfigurationItems(RULES_MANAGER)
                .getConfigurationItems().getFirst().getItems().get("flag"));
        server.verify();
    }

    @Test
    void fallsBackToGenericPropertyWhenSpecificOneIsAbsent() {
        server.expect(requestTo(routes.status())).andRespond(withSuccess());
        server.expect(requestTo(routes.getConfigItem(RULES_MANAGER, "flag"))).andRespond(withNoContent());
        server.expect(requestTo(routes.getConfigItem(GENERIC, "flag")))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));
        assertEquals("true", client.getProps("flag", "false", RULES_MANAGER));
        server.verify();
    }

    @Test
    void prefersSpecificPropertyWithoutGenericRequest() {
        server.expect(requestTo(routes.status())).andRespond(withSuccess());
        server.expect(requestTo(routes.getConfigItem(RULES_MANAGER, "flag")))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));
        assertEquals("true", client.getProps("flag", "true", RULES_MANAGER));
        server.verify();
    }

    @Test
    void keepsPreviousValueWhenConfigurationServiceIsOffline() {
        server.expect(requestTo(routes.status())).andRespond(withException(new IOException("offline")));
        assertEquals("previous", client.getProps("flag", "previous", RULES_MANAGER));
        server.verify();
    }
}
