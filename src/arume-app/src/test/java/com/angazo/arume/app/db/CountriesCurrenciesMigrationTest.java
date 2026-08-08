package com.angazo.arume.app.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CountriesCurrenciesMigrationTest {

    private static final String URL =
        "jdbc:h2:mem:arume_migration_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

    @BeforeAll
    static void applyMigrations() {
        Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration/core")
            .table("_flyway_core_schema_history")
            .load()
            .migrate();
        Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration/es")
            .table("_flyway_es_schema_history")
            .baselineOnMigrate(true)
            .baselineVersion("0.0.0.0")
            .load()
            .migrate();
        Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration/uk")
            .table("_flyway_uk_schema_history")
            .baselineOnMigrate(true)
            .baselineVersion("0.0.0.0")
            .load()
            .migrate();
    }

    @Test
    void languageCatalogContainsSupportedLanguages() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals(2, countRows(stmt, "t0_i18n"));
            assertEquals("English", singleValue(stmt, "SELECT name FROM t0_i18n WHERE language_code = 'en'"));
            assertEquals("Spanish", singleValue(stmt, "SELECT name FROM t0_i18n WHERE language_code = 'es'"));
        }
    }

    @Test
    void countriesTableContainsExpectedSeed() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals(7, countRows(stmt, "t1_countries"));
            assertEquals(14, countRows(stmt, "t2_country_names"));
            assertEquals(8, countRows(stmt, "t3_currencies"));
            assertEquals(2, countRows(stmt, "t4_country_currency"));

            var spain = queryCountry(stmt, "ES");
            assertEquals("ESP", spain.alpha3);
            assertEquals(724, spain.numericCode);
        }
    }

    @Test
    void countriesTableHasNoNameColumn() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals(0, countRows(stmt, """
                information_schema.columns
                WHERE table_name = 't1_countries' AND column_name = 'name'
                """));
        }
    }

    @Test
    void everyCountryHasANameInEveryLanguage() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals(0, countRows(stmt, """
                (SELECT c.alpha2_code, l.language_code
                 FROM t1_countries c
                 CROSS JOIN t0_i18n l
                 WHERE NOT EXISTS (
                     SELECT 1 FROM t2_country_names n
                     WHERE n.country_alpha2_code = c.alpha2_code
                       AND n.language_code = l.language_code))
                """));
        }
    }

    @Test
    void countryNamesAreSeededInBothLanguages() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals("Spain", countryName(stmt, "ES", "en"));
            assertEquals("España", countryName(stmt, "ES", "es"));
            assertEquals("United Kingdom", countryName(stmt, "GB", "en"));
            assertEquals("Reino Unido", countryName(stmt, "GB", "es"));
        }
    }

    @Test
    void countryNameMustReferenceExistingCountryAndLanguage() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertThrows(SQLException.class, () -> stmt.executeUpdate(
                "INSERT INTO t2_country_names (country_alpha2_code, language_code, name) VALUES ('XX', 'en', 'Nowhere')"));
            assertThrows(SQLException.class, () -> stmt.executeUpdate(
                "INSERT INTO t2_country_names (country_alpha2_code, language_code, name) VALUES ('ES', 'fr', 'Espagne')"));
        }
    }

    @Test
    void currenciesTableContainsExpectedSeed() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            var euro = queryCurrency(stmt, 978);
            assertEquals("EUR", euro.alpha3);
            assertEquals("Euro", euro.name);
            assertEquals("€", euro.symbol);

            var clf = queryCurrency(stmt, 990);
            assertEquals("CLF", clf.alpha3);
            assertEquals("Unidad de Fomento", clf.name);
            assertEquals("UF", clf.symbol);
        }
    }

    @Test
    void eachNationalModuleSeedsItsOwnCurrencyAssociation() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals(List.of(978), currenciesOf(stmt, "ES"), "Spain should have EUR (978)");
            assertEquals(List.of(826), currenciesOf(stmt, "GB"), "The United Kingdom should have GBP (826)");
        }
    }

    @Test
    void countriesWithoutANationalModuleHaveNoCurrencyAssociation() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            for (var alpha2 : List.of("US", "CL", "SG", "AU", "ZA")) {
                assertEquals(List.of(), currenciesOf(stmt, alpha2), alpha2 + " has no national module yet");
            }
        }
    }

    private static List<Integer> currenciesOf(java.sql.Statement stmt, String alpha2) throws SQLException {
        try (var rs = stmt.executeQuery(
                 "SELECT currency_numeric_code FROM t4_country_currency "
                     + "WHERE country_alpha2_code = '" + alpha2 + "' ORDER BY currency_numeric_code")) {
            var codes = new java.util.ArrayList<Integer>();
            while (rs.next()) {
                codes.add(rs.getInt(1));
            }
            return List.copyOf(codes);
        }
    }

    @Test
    void duplicateAlpha3IsRejected() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertThrows(SQLException.class, () -> stmt.executeUpdate(
                "INSERT INTO t1_countries (alpha2_code, alpha3_code, numeric_code) VALUES ('XX', 'ESP', 999)"));
            assertFalse(exists(stmt, "SELECT 1 FROM t1_countries WHERE alpha2_code = 'XX'"));
        }
    }

    private static int countRows(java.sql.Statement stmt, String table) throws SQLException {
        try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static boolean exists(java.sql.Statement stmt, String sql) throws SQLException {
        try (var rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    private static String singleValue(java.sql.Statement stmt, String sql) throws SQLException {
        try (var rs = stmt.executeQuery(sql)) {
            assertTrue(rs.next());
            return rs.getString(1);
        }
    }

    private static String countryName(java.sql.Statement stmt, String alpha2, String language) throws SQLException {
        return singleValue(stmt, "SELECT name FROM t2_country_names WHERE country_alpha2_code = '"
            + alpha2 + "' AND language_code = '" + language + "'");
    }

    private static CountryRow queryCountry(java.sql.Statement stmt, String alpha2) throws SQLException {
        try (var rs = stmt.executeQuery(
            "SELECT alpha3_code, numeric_code FROM t1_countries WHERE alpha2_code = '" + alpha2 + "'")) {
            assertTrue(rs.next());
            return new CountryRow(rs.getString(1), rs.getInt(2));
        }
    }

    private static CurrencyRow queryCurrency(java.sql.Statement stmt, int numericCode) throws SQLException {
        try (var rs = stmt.executeQuery("SELECT alpha3_code, name, symbol FROM t3_currencies WHERE numeric_code = " + numericCode)) {
            assertTrue(rs.next());
            return new CurrencyRow(rs.getString(1), rs.getString(2), rs.getString(3));
        }
    }

    private record CountryRow(String alpha3, int numericCode) {
    }

    private record CurrencyRow(String alpha3, String name, String symbol) {
    }
}
