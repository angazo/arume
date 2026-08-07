package com.angazo.arume.es;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.angazo.arume.db.migration.MigrationOrchestrator;

@Configuration
@MapperScan("com.angazo.arume.es.persistence")
public class SpainDatabaseConfiguration {

    @Bean(name = "spainFlyway")
    @DependsOn("flyway")
    public Flyway spainFlyway(
        DataSource dataSource,
        @Qualifier("flyway") Flyway coreFlyway,
        MigrationOrchestrator migrationOrchestrator
    ) {
        return migrationOrchestrator.migrateNational(
            dataSource,
            SpainModuleDescriptor.migrationDescriptor(),
            coreFlyway
        );
    }
}
