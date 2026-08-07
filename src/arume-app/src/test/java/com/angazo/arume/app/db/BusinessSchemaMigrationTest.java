package com.angazo.arume.app.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DriverManager;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.angazo.arume.core.module.MigrationModuleDescriptor;
import com.angazo.arume.core.module.SchemaVersion;
import com.angazo.arume.db.migration.MigrationOrchestrator;

class BusinessSchemaMigrationTest {

    private static final String URL =
        "jdbc:h2:mem:business_schema_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

    private static Flyway coreFlyway;

    @BeforeAll
    static void applyMigrations() {
        coreFlyway = Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration/core")
            .table("flyway_core_schema_history")
            .load();
        coreFlyway.migrate();

        Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration/es")
            .table("flyway_es_schema_history")
            .baselineOnMigrate(true)
            .baselineVersion("0.0.0.0")
            .load()
            .migrate();
    }

    @Test
    void coreAndSpainUseIndependentHistories() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "")) {
            assertTrue(tableExists(connection, "flyway_core_schema_history"));
            assertTrue(tableExists(connection, "flyway_es_schema_history"));
            assertTrue(tableExists(connection, "t4_companies"));
            assertTrue(tableExists(connection, "t7_fiscal_years"));
            assertTrue(tableExists(connection, "es1_invoice_series"));
            assertTrue(tableExists(connection, "es2_invoice_series_fiscal_year"));
        }
    }

    @Test
    void SpainTablesReferenceCoreTables() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement();
             var result = statement.executeQuery(
                 "SELECT COUNT(*) FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS "
                     + "WHERE LOWER(CONSTRAINT_NAME) IN ('fk_es1_t4', 'fk_es2_t7', 'fk_es2_es1')")) {
            result.next();
            assertEquals(3, result.getInt(1));
        }
    }

    @Test
    void nationalModuleCannotRunWithInsufficientCoreVersion() {
        var descriptor = new MigrationModuleDescriptor(
            "arume-es",
            SchemaVersion.parse("0.1.0.0"),
            SchemaVersion.parse("9.9.9.9"),
            "classpath:db/migration/es",
            "flyway_es_incompatible_history"
        );

        assertThrows(IllegalStateException.class, () -> new MigrationOrchestrator()
            .migrateNational(
                dataSource(),
                descriptor,
                coreFlyway
            ));
    }

    @Test
    void failedNationalMigrationStopsWithoutEnablingTheModule() {
        var flyway = Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration/es-failure")
            .table("flyway_es_failure_history")
            .baselineOnMigrate(true)
            .baselineVersion("0.0.0.0")
            .load();

        assertThrows(FlywayException.class, flyway::migrate);
    }

    private static javax.sql.DataSource dataSource() {
        var dataSource = new org.h2.jdbcx.JdbcDataSource();
        dataSource.setURL(URL);
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    private static boolean tableExists(java.sql.Connection connection, String tableName) throws Exception {
        try (var statement = connection.prepareStatement(
                 "SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = LOWER(?)")) {
            statement.setString(1, tableName);
            try (var result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
