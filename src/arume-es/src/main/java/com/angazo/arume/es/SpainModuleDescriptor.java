package com.angazo.arume.es;

import com.angazo.arume.core.module.FiscalModuleDescriptor;
import com.angazo.arume.core.module.MigrationModuleDescriptor;
import com.angazo.arume.core.module.SchemaVersion;

public final class SpainModuleDescriptor {

    private SpainModuleDescriptor() {
    }

    public static FiscalModuleDescriptor descriptor() {
        return new FiscalModuleDescriptor(
            "arume-es",
            "ES",
            "0.1.0",
            "0.1.0.0"
        );
    }

    public static MigrationModuleDescriptor migrationDescriptor() {
        return new MigrationModuleDescriptor(
            "arume-es",
            SchemaVersion.parse("0.1.0.0"),
            SchemaVersion.parse("0.1.0.0"),
            "classpath:db/migration/es",
            "_flyway_es_schema_history"
        );
    }
}
