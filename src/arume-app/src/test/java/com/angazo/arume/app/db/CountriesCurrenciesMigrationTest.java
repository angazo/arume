package com.angazo.arume.app.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CountriesCurrenciesMigrationTest {

    private static final String URL =
        "jdbc:h2:mem:arume_migration_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

    @BeforeAll
    static void applyMigrations() {
        var flyway = Flyway.configure()
            .dataSource(URL, "sa", "")
            .locations("classpath:db/migration")
            .load();
        flyway.migrate();
    }

    @Test
    void countriesTableContainsExpectedSeed() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement()) {

            assertEquals(7, countRows(stmt, "t1_countries"));
            assertEquals(8, countRows(stmt, "t2_currencies"));
            assertEquals(8, countRows(stmt, "t3_country_currency"));

            var countries = queryCountry(stmt, 724);
            assertEquals("ESP", countries.alpha3);
            assertEquals("Spain", countries.name);
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
    void chileHasTwoCurrencies() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, "sa", "");
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(
                 "SELECT currency_numeric_code FROM t3_country_currency WHERE country_numeric_code = 152")) {

            var codes = new java.util.ArrayList<Integer>();
            while (rs.next()) {
                codes.add(rs.getInt(1));
            }
            assertEquals(2, codes.size());
            assertTrue(codes.contains(152), "Chile should have CLP (152)");
            assertTrue(codes.contains(990), "Chile should have CLF (990)");
        }
    }

    private static int countRows(java.sql.Statement stmt, String table) throws SQLException {
        try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static CountryRow queryCountry(java.sql.Statement stmt, int numericCode) throws SQLException {
        try (var rs = stmt.executeQuery("SELECT alpha3_code, name FROM t1_countries WHERE numeric_code = " + numericCode)) {
            assertTrue(rs.next());
            return new CountryRow(rs.getString(1), rs.getString(2));
        }
    }

    private static CurrencyRow queryCurrency(java.sql.Statement stmt, int numericCode) throws SQLException {
        try (var rs = stmt.executeQuery("SELECT alpha3_code, name, symbol FROM t2_currencies WHERE numeric_code = " + numericCode)) {
            assertTrue(rs.next());
            return new CurrencyRow(rs.getString(1), rs.getString(2), rs.getString(3));
        }
    }

    private record CountryRow(String alpha3, String name) {
    }

    private record CurrencyRow(String alpha3, String name, String symbol) {
    }
}
