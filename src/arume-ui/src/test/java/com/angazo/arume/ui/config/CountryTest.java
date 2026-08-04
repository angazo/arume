package com.angazo.arume.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void fromCodeValidLowercase() {
        assertEquals(Country.ESP, Country.fromCode("esp"));
        assertEquals(Country.GBR, Country.fromCode("gbr"));
        assertEquals(Country.CHL, Country.fromCode("chl"));
        assertEquals(Country.ZAF, Country.fromCode("zaf"));
    }

    @Test
    void fromCodeValidUppercaseIsNormalized() {
        assertEquals(Country.USA, Country.fromCode("USA"));
        assertEquals(Country.AUS, Country.fromCode("AUS"));
    }

    @Test
    void fromCodeUnknownFallsBackToSpain() {
        assertEquals(Country.ESP, Country.fromCode("xxx"));
        assertEquals(Country.ESP, Country.fromCode("fr"));
    }

    @Test
    void fromCodeLegacyIso2NoLongerResolves() {
        assertEquals(Country.ESP, Country.fromCode("gb"));
        assertEquals(Country.ESP, Country.fromCode("us"));
        assertEquals(Country.ESP, Country.fromCode("cl"));
    }

    @Test
    void fromCodeNullFallsBackToSpain() {
        assertEquals(Country.ESP, Country.fromCode(null));
    }

    @Test
    void detectDefaultReturnsSupportedCountry() {
        assertEquals(Country.USA, Country.detectDefault(Locale.US));
        assertEquals(Country.CHL, Country.detectDefault(new Locale("es", "CL")));
        assertEquals(Country.GBR, Country.detectDefault(new Locale("en", "GB")));
        assertEquals(Country.AUS, Country.detectDefault(new Locale("en", "AU")));
    }

    @Test
    void detectDefaultFallsBackToSpainForUnsupported() {
        assertEquals(Country.ESP, Country.detectDefault(Locale.FRANCE));
        assertEquals(Country.ESP, Country.detectDefault(Locale.GERMANY));
    }

    @Test
    void detectDefaultFallsBackToSpainForBlankCountry() {
        assertEquals(Country.ESP, Country.detectDefault(new Locale("es")));
        assertEquals(Country.ESP, Country.detectDefault(Locale.ENGLISH));
    }

    @Test
    void getLabelKeyUsesLowercaseAlpha3() {
        assertEquals("wizard.country.esp", Country.ESP.getLabelKey());
        assertEquals("wizard.country.zaf", Country.ZAF.getLabelKey());
        assertEquals("wizard.country.chl", Country.CHL.getLabelKey());
    }

    @Test
    void officialLanguageMatchesExpected() {
        assertEquals("es", Country.ESP.officialLanguage());
        assertEquals("es", Country.CHL.officialLanguage());
        assertEquals("en", Country.GBR.officialLanguage());
        assertEquals("en", Country.USA.officialLanguage());
        assertEquals("en", Country.SGP.officialLanguage());
        assertEquals("en", Country.AUS.officialLanguage());
        assertEquals("en", Country.ZAF.officialLanguage());
    }

    @Test
    void catalogContainsExactlySevenCountries() {
        assertEquals(7, Country.values().length);
    }

    @Test
    void codesAreLowercaseIso3() {
        for (var c : Country.values()) {
            assertEquals(3, c.code().length());
            assertEquals(c.code().toLowerCase(), c.code());
        }
    }
}
