package com.angazo.arume.uk;

import com.angazo.arume.core.module.FiscalModuleDescriptor;
import com.angazo.arume.core.module.MigrationModuleDescriptor;
import com.angazo.arume.core.module.SchemaVersion;

public final class UkModuleDescriptor {

    private UkModuleDescriptor() {
    }

    public static FiscalModuleDescriptor descriptor() {
        return new FiscalModuleDescriptor(
            "arume-uk",
            "GB",
            "0.1.0",
            "0.1.0.0"
        );
    }

    public static MigrationModuleDescriptor migrationDescriptor() {
        return new MigrationModuleDescriptor(
            "arume-uk",
            SchemaVersion.parse("0.1.0.0"),
            SchemaVersion.parse("0.1.0.0"),
            "classpath:db/migration/uk",
            "_flyway_uk_schema_history"
        );
    }
}
