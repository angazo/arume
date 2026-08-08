package com.angazo.arume.core.module;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class FiscalModuleRegistryTest {

    @Test
    void resolvesCapabilityFromMatchingJurisdiction() {
        var capability = new TestCapability();
        var module = new TestModule("ES", capability);
        var registry = new FiscalModuleRegistry(List.of(module));

        assertTrue(registry.resolve("ES", "test", TestCapability.class).isPresent());
    }

    @Test
    void returnsEmptyForUnavailableJurisdiction() {
        var registry = new FiscalModuleRegistry(List.of(new TestModule("ES", new TestCapability())));

        assertTrue(registry.resolve("PT", "test", TestCapability.class).isEmpty());
    }

    private record TestCapability() implements FiscalCapability {
        @Override
        public String capabilityId() {
            return "test";
        }
    }

    private record TestModule(String jurisdiction, FiscalCapability capability) implements FiscalModule {
        @Override
        public FiscalModuleDescriptor descriptor() {
            return new FiscalModuleDescriptor("test-" + jurisdiction, jurisdiction, "0.1.0", "0.1.0");
        }

        @Override
        public List<FiscalCapability> capabilities() {
            return List.of(capability);
        }
    }
}
