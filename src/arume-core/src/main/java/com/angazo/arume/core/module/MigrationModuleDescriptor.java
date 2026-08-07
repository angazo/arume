package com.angazo.arume.core.module;

import java.util.Objects;

public record MigrationModuleDescriptor(
    String moduleId,
    SchemaVersion schemaVersion,
    SchemaVersion minimumCoreSchemaVersion,
    String migrationLocation,
    String historyTable
) {

    public MigrationModuleDescriptor {
        moduleId = requireText(moduleId, "moduleId");
        Objects.requireNonNull(schemaVersion, "schemaVersion");
        Objects.requireNonNull(minimumCoreSchemaVersion, "minimumCoreSchemaVersion");
        migrationLocation = requireText(migrationLocation, "migrationLocation");
        historyTable = requireText(historyTable, "historyTable");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
