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
            assertTrue(tableExists(connection, "t5_legal_forms"));
            assertTrue(tableExists(connection, "t6_companies"));
            assertTrue(tableExists(connection, "t9_fiscal_years"));
            assertTrue(tableExists(connection, "es1_invoice_series"));
            assertTrue(tableExists(connection, "es2_invoice_series_fiscal_year"));
        }
    }

    @Test
    void companyJurisdictionsReferenceCountriesCatalog() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement();
             var result = statement.executeQuery(
                 "SELECT COUNT(*) FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS "
                     + "WHERE LOWER(CONSTRAINT_NAME) IN ('fk_t6_t1', 'fk_t6_t1_2', 'fk_t7_t1', 'fk_t8_t1')")) {
            result.next();
            assertEquals(4, result.getInt(1));
        }
    }

    @Test
    void companyCreatedAtIsTimestampWithTimeZone() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement();
             var result = statement.executeQuery(
                 "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS "
                     + "WHERE LOWER(TABLE_NAME) = 't6_companies' AND LOWER(COLUMN_NAME) = 'created_at'")) {
            assertTrue(result.next());
            assertTrue(
                result.getString(1).toUpperCase().contains("TIMESTAMP"),
                "Expected TIMESTAMP-based type but was " + result.getString(1)
            );
            assertTrue(
                result.getString(1).toUpperCase().contains("TIME ZONE"),
                "Expected a time zone aware type but was " + result.getString(1)
            );
        }
    }

    @Test
    void SpainTablesReferenceCoreTables() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement();
             var result = statement.executeQuery(
                 "SELECT COUNT(*) FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS "
                     + "WHERE LOWER(CONSTRAINT_NAME) IN ('fk_es1_t6', 'fk_es2_t9', 'fk_es2_es1')")) {
            result.next();
            assertEquals(3, result.getInt(1));
        }
    }

    @Test
    void jurisdictionColumnsAreAlpha2() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement();
             var result = statement.executeQuery(
                 "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                     + "WHERE CHARACTER_MAXIMUM_LENGTH = 2 AND ("
                     + "(LOWER(TABLE_NAME) = 't6_companies' AND LOWER(COLUMN_NAME) IN ('primary_fiscal_jurisdiction', 'legal_form_jurisdiction')) "
                     + "OR (LOWER(TABLE_NAME) = 't7_company_profiles' AND LOWER(COLUMN_NAME) = 'fiscal_residence') "
                     + "OR (LOWER(TABLE_NAME) = 't8_company_tax_registrations' AND LOWER(COLUMN_NAME) = 'jurisdiction'))")) {
            result.next();
            assertEquals(4, result.getInt(1));
        }
    }

    @Test
    void companyLegalFormIsConstrainedByTheLegalFormCatalog() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement()) {

            assertThrows(java.sql.SQLException.class, () -> statement.executeUpdate("""
                INSERT INTO t6_companies (is_legal_person, primary_fiscal_jurisdiction, primary_fiscal_id,
                                          legal_form_jurisdiction, legal_form_code)
                VALUES (TRUE, 'ES', 'B00000000', 'ES', 'UNKNOWN')
                """));
            assertThrows(java.sql.SQLException.class, () -> statement.executeUpdate("""
                INSERT INTO t6_companies (is_legal_person, primary_fiscal_jurisdiction, primary_fiscal_id,
                                          legal_form_jurisdiction, legal_form_code)
                VALUES (FALSE, 'ES', 'B00000001', 'ES', 'SL')
                """));
        }
    }

    @Test
    void spainSeedsTheCoreLegalFormCatalog() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "");
             var statement = connection.createStatement();
             var result = statement.executeQuery(
                 "SELECT COUNT(*) FROM t5_legal_forms WHERE country_alpha2_code = 'ES'")) {
            result.next();
            assertEquals(17, result.getInt(1));
        }
    }

    @Test
    void spainHasNoLegalFormTableOfItsOwn() throws Exception {
        try (var connection = DriverManager.getConnection(URL, "sa", "")) {
            org.junit.jupiter.api.Assertions.assertFalse(tableExists(connection, "es3_legal_forms"));
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
