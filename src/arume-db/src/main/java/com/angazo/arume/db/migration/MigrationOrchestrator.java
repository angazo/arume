package com.angazo.arume.db.migration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import com.angazo.arume.core.module.MigrationModuleDescriptor;
import com.angazo.arume.core.module.SchemaVersion;

public class MigrationOrchestrator {

    public Flyway migrateCore(DataSource dataSource, MigrationModuleDescriptor descriptor) {
        return migrate(dataSource, descriptor, false);
    }

    public Flyway migrateNational(
        DataSource dataSource,
        MigrationModuleDescriptor descriptor,
        Flyway coreFlyway
    ) {
        var currentCoreVersion = coreFlyway.info().current();
        if (currentCoreVersion == null || currentCoreVersion.getVersion() == null) {
            throw new IllegalStateException("Core schema has no current migration");
        }

        var current = SchemaVersion.parse(currentCoreVersion.getVersion().getVersion());
        if (current.compareTo(descriptor.minimumCoreSchemaVersion()) < 0) {
            throw new IllegalStateException(
                "Module " + descriptor.moduleId() + " requires core schema "
                    + descriptor.minimumCoreSchemaVersion() + " but found " + current
            );
        }
        return migrate(dataSource, descriptor, true);
    }

    private Flyway migrate(
        DataSource dataSource,
        MigrationModuleDescriptor descriptor,
        boolean baselineOnMigrate
    ) {
        var configuration = Flyway.configure()
            .dataSource(dataSource)
            .locations(descriptor.migrationLocation())
            .table(descriptor.historyTable());
        if (baselineOnMigrate) {
            configuration.baselineOnMigrate(true).baselineVersion("0.0.0.0");
        }
        var flyway = configuration.load();
        flyway.migrate();
        return flyway;
    }
}
