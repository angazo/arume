package com.angazo.arume.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.angazo.arume.core.application.catalog.CountryCatalogService;
import com.angazo.arume.core.domain.catalog.CountryCatalogEntry;

@SpringBootTest(
    classes = ArumeApp.class,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:country_catalog_persistence;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CountryCatalogPersistenceIntegrationTest {

    @Autowired
    private CountryCatalogService countryCatalogService;

    @Test
    void countriesAreListedInSpanish() {
        var countries = countryCatalogService.list("es");

        assertEquals(7, countries.size());
        assertEquals("España", nameOf(countries, "ES"));
        assertEquals("Reino Unido", nameOf(countries, "GB"));
        assertEquals("Sudáfrica", nameOf(countries, "ZA"));
    }

    @Test
    void countriesAreListedInEnglish() {
        var countries = countryCatalogService.list("en");

        assertEquals(7, countries.size());
        assertEquals("Spain", nameOf(countries, "ES"));
        assertEquals("United Kingdom", nameOf(countries, "GB"));
        assertEquals("South Africa", nameOf(countries, "ZA"));
    }

    @Test
    void countriesAreSortedByLocalizedName() {
        var countries = countryCatalogService.list("es");

        assertEquals(
            countries.stream().sorted(Comparator.comparing(CountryCatalogEntry::name)).toList(),
            countries
        );
    }

    @Test
    void unknownLanguageFallsBackToEnglish() {
        var countries = countryCatalogService.list("fr");

        assertEquals(7, countries.size());
        assertEquals("Spain", nameOf(countries, "ES"));
    }

    @Test
    void blankLanguageFallsBackToEnglish() {
        var countries = countryCatalogService.list(null);

        assertTrue(countries.stream().anyMatch(entry -> entry.name().equals("United States")));
    }

    private static String nameOf(List<CountryCatalogEntry> countries, String alpha2) {
        return countries.stream()
            .filter(entry -> entry.code().value().equals(alpha2))
            .map(CountryCatalogEntry::name)
            .findFirst()
            .orElseThrow();
    }
}
