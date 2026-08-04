package com.angazo.arume.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void fromCodeValidLowercase() {
        assertEquals(Country.ES, Country.fromCode("es"));
        assertEquals(Country.GB, Country.fromCode("gb"));
        assertEquals(Country.CL, Country.fromCode("cl"));
        assertEquals(Country.ZA, Country.fromCode("za"));
    }

    @Test
    void fromCodeValidUppercaseIsNormalized() {
        assertEquals(Country.US, Country.fromCode("US"));
        assertEquals(Country.AU, Country.fromCode("AU"));
    }

    @Test
    void fromCodeUnknownFallsBackToSpain() {
        assertEquals(Country.ES, Country.fromCode("xx"));
        assertEquals(Country.ES, Country.fromCode("fr"));
    }

    @Test
    void fromCodeNullFallsBackToSpain() {
        assertEquals(Country.ES, Country.fromCode(null));
    }

    @Test
    void detectDefaultReturnsSupportedCountry() {
        assertEquals(Country.US, Country.detectDefault(Locale.US));
        assertEquals(Country.CL, Country.detectDefault(new Locale("es", "CL")));
        assertEquals(Country.GB, Country.detectDefault(new Locale("en", "GB")));
        assertEquals(Country.AU, Country.detectDefault(new Locale("en", "AU")));
    }

    @Test
    void detectDefaultFallsBackToSpainForUnsupported() {
        assertEquals(Country.ES, Country.detectDefault(Locale.FRANCE));
        assertEquals(Country.ES, Country.detectDefault(Locale.GERMANY));
    }

    @Test
    void detectDefaultFallsBackToSpainForBlankCountry() {
        assertEquals(Country.ES, Country.detectDefault(new Locale("es")));
        assertEquals(Country.ES, Country.detectDefault(Locale.ENGLISH));
    }

    @Test
    void getLabelKeyUsesLowercaseCode() {
        assertEquals("wizard.country.es", Country.ES.getLabelKey());
        assertEquals("wizard.country.za", Country.ZA.getLabelKey());
        assertEquals("wizard.country.cl", Country.CL.getLabelKey());
    }

    @Test
    void officialLanguageMatchesExpected() {
        assertEquals("es", Country.ES.officialLanguage());
        assertEquals("es", Country.CL.officialLanguage());
        assertEquals("en", Country.GB.officialLanguage());
        assertEquals("en", Country.US.officialLanguage());
        assertEquals("en", Country.SG.officialLanguage());
        assertEquals("en", Country.AU.officialLanguage());
        assertEquals("en", Country.ZA.officialLanguage());
    }

    @Test
    void catalogContainsExactlySevenCountries() {
        assertEquals(7, Country.values().length);
    }

    @Test
    void codesAreLowercaseIso2() {
        for (var c : Country.values()) {
            assertEquals(2, c.code().length());
            assertEquals(c.code().toLowerCase(), c.code());
        }
    }
}