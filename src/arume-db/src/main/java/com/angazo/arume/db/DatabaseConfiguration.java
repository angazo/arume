package com.angazo.arume.db;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.angazo.arume.core.module.MigrationModuleDescriptor;
import com.angazo.arume.core.module.SchemaVersion;
import com.angazo.arume.db.migration.MigrationOrchestrator;

@Configuration
@MapperScan("com.angazo.arume.db.persistence.mapper")
@Slf4j
public class DatabaseConfiguration {

    private static final MigrationModuleDescriptor CORE_MIGRATIONS = new MigrationModuleDescriptor(
        "arume-core",
        SchemaVersion.parse("0.1.0.2"),
        SchemaVersion.parse("0.0.0.0"),
        "classpath:db/migration/core",
        "flyway_core_schema_history"
    );

    @Bean
    public MigrationOrchestrator migrationOrchestrator() {
        return new MigrationOrchestrator();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(
        DataSource dataSource,
        ApplicationContext applicationContext
    ) throws Exception {
        var factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(applicationContext.getResources("classpath*:mappers/*.xml"));
        var configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultFetchSize(100);
        configuration.setDefaultStatementTimeout(5);
        factory.setConfiguration(configuration);
        return factory.getObject();
    }

    @Bean
    public Flyway flyway(DataSource dataSource, MigrationOrchestrator migrationOrchestrator) {
        log.info("Running Flyway migrations...");
        var flyway = migrationOrchestrator.migrateCore(dataSource, CORE_MIGRATIONS);
        var result = flyway.info().current();
        log.info("Core Flyway schema ready at: {}", result == null ? "none" : result.getVersion());
        return flyway;
    }
}
