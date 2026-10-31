package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.PROPS_NAME_CONTROL_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ConfigItemTypeEnum.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.env.MockEnvironment;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl.ConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.ProfileUtility;

@Tag("ISOLATION")
class ConfigServiceIsolationTest {
    private final IConfigClient client = mock(IConfigClient.class);
    private final ProfileUtility profiles = new ProfileUtility();
    private final MockEnvironment environment = new MockEnvironment();
    private ConfigSRV service;

    @BeforeEach
    void setUp() {
        service = new ConfigSRV();
        ReflectionTestUtils.setField(profiles, "environment", environment);
        ReflectionTestUtils.setField(service, "client", client);
        ReflectionTestUtils.setField(service, "profiles", profiles);
    }

    @Test
    void specificConfigurationOverridesGenericAndIsCached() {
        when(client.getConfigurationItems(GENERIC)).thenReturn(items("false"));
        when(client.getConfigurationItems(RULES_MANAGER)).thenReturn(items("true"));
        service.postConstruct();
        assertTrue(service.isControlLogPersistenceEnable());
        assertTrue(service.isControlLogPersistenceEnable());
        assertEquals(300_000, service.getRefreshRate());
        var order = inOrder(client);
        order.verify(client).getConfigurationItems(GENERIC);
        order.verify(client).getConfigurationItems(RULES_MANAGER);
        verifyNoMoreInteractions(client);
    }

    @Test
    void refreshesExpiredValueUsingPreviousValueAndServiceType() {
        // Override only the clock policy, leaving initialization and refresh behavior real.
        service = new ConfigSRV() { @Override public long getRefreshRate() { return 0; } };
        ReflectionTestUtils.setField(service, "client", client);
        ReflectionTestUtils.setField(service, "profiles", profiles);
        when(client.getConfigurationItems(GENERIC)).thenReturn(items("true"));
        when(client.getConfigurationItems(RULES_MANAGER)).thenReturn(emptyItems());
        when(client.getProps(PROPS_NAME_CONTROL_LOG_ENABLED, "true", RULES_MANAGER)).thenReturn("false");
        service.postConstruct();
        assertFalse(service.isControlLogPersistenceEnable());
        verify(client).getProps(PROPS_NAME_CONTROL_LOG_ENABLED, "true", RULES_MANAGER);
    }

    @Test
    void rejectsMissingRequiredConfiguration() {
        when(client.getConfigurationItems(any())).thenReturn(emptyItems());
        assertEquals("Missing props " + PROPS_NAME_CONTROL_LOG_ENABLED + " from rules-manager",
                assertThrows(IllegalStateException.class, service::postConstruct).getMessage());
    }

    @Test
    void skipsRemoteInitializationOnlyForLegacyTestProfile() {
        environment.setActiveProfiles("TEST");
        service.postConstruct();
        verifyNoInteractions(client);
        assertTrue(profiles.isTestProfile());
        assertFalse(profiles.isDevOrDockerProfile());
    }

    @Test
    void isolationProfileDoesNotAccidentallyEnableLegacyTestFallback() {
        environment.setActiveProfiles("ISOLATION");
        assertFalse(profiles.isTestProfile());
        assertFalse(profiles.isDevOrDockerProfile());
        environment.setActiveProfiles("DOCKER");
        assertTrue(profiles.isDevOrDockerProfile());
        environment.setActiveProfiles("dev");
        assertTrue(profiles.isDevOrDockerProfile());
    }

    private ConfigItemDTO items(String value) {
        ConfigItemDTO.ConfigDataItemDTO entry = new ConfigItemDTO.ConfigDataItemDTO();
        entry.setItems(Map.of(PROPS_NAME_CONTROL_LOG_ENABLED, value));
        ConfigItemDTO result = new ConfigItemDTO();
        result.setConfigurationItems(List.of(entry));
        return result;
    }

    private ConfigItemDTO emptyItems() {
        ConfigItemDTO result = new ConfigItemDTO();
        result.setConfigurationItems(List.of());
        return result;
    }
}
