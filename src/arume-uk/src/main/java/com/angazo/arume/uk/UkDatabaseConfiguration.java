package com.angazo.arume.uk;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.angazo.arume.db.migration.MigrationOrchestrator;

@Configuration
public class UkDatabaseConfiguration {

    @Bean(name = "ukFlyway")
    @DependsOn("flyway")
    public Flyway ukFlyway(
        DataSource dataSource,
        @Qualifier("flyway") Flyway coreFlyway,
        MigrationOrchestrator migrationOrchestrator
    ) {
        return migrationOrchestrator.migrateNational(
            dataSource,
            UkModuleDescriptor.migrationDescriptor(),
            coreFlyway
        );
    }
}
