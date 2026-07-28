package com.angazo.arume.db;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.angazo.arume.db.repository")
@Slf4j
public class DatabaseConfiguration {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        var flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load();
        log.info("Running Flyway migrations...");
        var result = flyway.migrate();
        log.info("Flyway migrations applied: {}", result.migrationsExecuted);
        return flyway;
    }
}
