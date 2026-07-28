package com.angazo.arume.ui.i18n;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class I18nManagerTest {

    @BeforeEach
    void setUp() {
        I18nManager.init("en");
    }

    @Test
    void detectDefaultLanguageShouldReturnSpanishForEsLocale() {
        assertEquals("es", I18nManager.detectDefaultLanguage(Locale.of("es", "ES")));
    }

    @Test
    void detectDefaultLanguageShouldReturnSpanishForSpanishLocalesWithoutRegion() {
        assertEquals("es", I18nManager.detectDefaultLanguage(Locale.of("es")));
    }

    @Test
    void detectDefaultLanguageShouldReturnSpanishForCoOfficialWithSpain() {
        assertEquals("es", I18nManager.detectDefaultLanguage(Locale.of("ca", "ES")));
        assertEquals("es", I18nManager.detectDefaultLanguage(Locale.of("gl", "ES")));
        assertEquals("es", I18nManager.detectDefaultLanguage(Locale.of("eu", "ES")));
    }

    @Test
    void detectDefaultLanguageShouldReturnEnglishForCoOfficialOutsideSpain() {
        assertEquals("en", I18nManager.detectDefaultLanguage(Locale.of("ca", "FR")));
    }

    @Test
    void detectDefaultLanguageShouldReturnEnglishForNonSpanishLocales() {
        assertEquals("en", I18nManager.detectDefaultLanguage(Locale.of("fr", "FR")));
        assertEquals("en", I18nManager.detectDefaultLanguage(Locale.of("de", "DE")));
        assertEquals("en", I18nManager.detectDefaultLanguage(Locale.of("ja", "JP")));
    }

    @Test
    void detectDefaultLanguageShouldReturnEnglishForEnglishLocale() {
        assertEquals("en", I18nManager.detectDefaultLanguage(Locale.of("en", "US")));
    }

    @Test
    void initShouldSetLanguageAndBundle() {
        I18nManager.init("es");
        assertEquals("es", I18nManager.getCurrentLanguage());
        assertEquals("Guardar", I18nManager.getString("wizard.save"));
    }

    @Test
    void setLanguageShouldChangeActiveLanguage() {
        I18nManager.init("en");
        assertEquals("Save", I18nManager.getString("wizard.save"));

        I18nManager.setLanguage("es");
        assertEquals("Guardar", I18nManager.getString("wizard.save"));
    }

    @Test
    void getStringShouldReturnKeyWithBangsForMissingKey() {
        assertEquals("!nonexistent.key!", I18nManager.getString("nonexistent.key"));
    }

    @Test
    void setLanguageShouldNotNotifyWhenSameLanguage() {
        I18nManager.init("en");
        var notified = new AtomicBoolean(false);
        I18nManager.onLanguageChange(() -> notified.set(true));

        I18nManager.setLanguage("en");
        assertFalse(notified.get());
    }

    @Test
    void setLanguageShouldNotifyListenersOnChange() {
        I18nManager.init("en");
        var notified = new AtomicBoolean(false);
        I18nManager.onLanguageChange(() -> notified.set(true));

        I18nManager.setLanguage("es");
        assertTrue(notified.get());
    }

    @Test
    void onLanguageChangeShouldFireForMultipleListeners() {
        I18nManager.init("en");
        var first = new AtomicBoolean(false);
        var second = new AtomicBoolean(false);
        I18nManager.onLanguageChange(() -> first.set(true));
        I18nManager.onLanguageChange(() -> second.set(true));

        I18nManager.setLanguage("es");
        assertTrue(first.get());
        assertTrue(second.get());
    }

    @Test
    void getStringShouldReturnEnglishByDefault() {
        I18nManager.init("en");
        assertEquals("Save", I18nManager.getString("wizard.save"));
        assertEquals("Cancel", I18nManager.getString("wizard.cancel"));
        assertEquals("Initial Setup \u2014 Arume", I18nManager.getString("wizard.title"));
        assertEquals("Language:", I18nManager.getString("wizard.language"));
    }

    @Test
    void getStringShouldReturnSpanishWhenSpanishActive() {
        I18nManager.init("es");
        assertEquals("Guardar", I18nManager.getString("wizard.save"));
        assertEquals("Cancelar", I18nManager.getString("wizard.cancel"));
        assertEquals("Configuraci\u00f3n inicial \u2014 Arume", I18nManager.getString("wizard.title"));
        assertEquals("Idioma:", I18nManager.getString("wizard.language"));
    }

    @Test
    void getCurrentLanguageShouldReflectInit() {
        I18nManager.init("es");
        assertEquals("es", I18nManager.getCurrentLanguage());

        I18nManager.init("en");
        assertEquals("en", I18nManager.getCurrentLanguage());
    }
}
