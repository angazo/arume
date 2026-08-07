package com.angazo.arume.core.module;

import java.util.Objects;

/** Describes a national module and the core schema it requires. */
public record FiscalModuleDescriptor(
    String moduleId,
    String jurisdictionCode,
    String moduleVersion,
    String minimumCoreSchemaVersion
) {

    public FiscalModuleDescriptor {
        moduleId = requireText(moduleId, "moduleId");
        jurisdictionCode = requireText(jurisdictionCode, "jurisdictionCode");
        moduleVersion = requireText(moduleVersion, "moduleVersion");
        minimumCoreSchemaVersion = requireText(minimumCoreSchemaVersion, "minimumCoreSchemaVersion");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
