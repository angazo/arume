package com.angazo.arume.core.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class FiscalModuleRegistry {

    private final Map<String, FiscalModule> modulesByJurisdiction;

    public FiscalModuleRegistry(List<? extends FiscalModule> modules) {
        Objects.requireNonNull(modules, "modules");
        var registry = new HashMap<String, FiscalModule>();
        for (var module : modules) {
            var jurisdiction = module.descriptor().jurisdictionCode();
            if (registry.putIfAbsent(jurisdiction, module) != null) {
                throw new IllegalArgumentException("More than one fiscal module is registered for " + jurisdiction);
            }
        }
        modulesByJurisdiction = Map.copyOf(registry);
    }

    public <T extends FiscalCapability> Optional<T> resolve(
        String jurisdictionCode,
        String capabilityId,
        Class<T> capabilityType
    ) {
        Objects.requireNonNull(jurisdictionCode, "jurisdictionCode");
        Objects.requireNonNull(capabilityId, "capabilityId");
        Objects.requireNonNull(capabilityType, "capabilityType");
        var module = modulesByJurisdiction.get(jurisdictionCode);
        if (module == null) {
            return Optional.empty();
        }
        return module.capabilities().stream()
            .filter(capability -> capability.capabilityId().equals(capabilityId))
            .filter(capabilityType::isInstance)
            .map(capabilityType::cast)
            .findFirst();
    }
}
