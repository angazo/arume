package com.angazo.arume.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import com.angazo.arume.ui.i18n.I18nManager;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CountryResourcesTest {

    @BeforeAll
    static void initI18n() {
        I18nManager.init("en");
    }

    @Test
    void flagPngExistsForEveryCountry() {
        for (var c : Country.values()) {
            var path = "/icons/flags/" + c.code() + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                assertNotNull(in, "Missing flag PNG: " + path);
            } catch (Exception e) {
                fail("Cannot read flag PNG " + path + ": " + e.getMessage());
            }
        }
    }

    @Test
    void flagPngIsHighResolution() {
        for (var c : Country.values()) {
            var path = "/icons/flags/" + c.code() + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                var image = ImageIO.read(in);
                assertNotNull(image, "Cannot decode flag PNG: " + path);
                assertEquals(96, image.getWidth(), "Unexpected width: " + path);
                assertEquals(72, image.getHeight(), "Unexpected height: " + path);
            } catch (Exception e) {
                fail("Cannot read flag PNG " + path + ": " + e.getMessage());
            }
        }
    }

    @Test
    void flagPngAspectRatioMatchesImageView() {
        for (var c : Country.values()) {
            var path = "/icons/flags/" + c.code() + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                var image = ImageIO.read(in);
                assertNotNull(image, "Cannot decode flag PNG: " + path);
                assertEquals(image.getHeight() * 4L, image.getWidth() * 3L,
                        "Flag PNG aspect ratio must be 4:3 (same as the 32x24 ImageView): " + path);
            } catch (Exception e) {
                fail("Cannot read flag PNG " + path + ": " + e.getMessage());
            }
        }
    }

    @Test
    void countryLabelKeysExistInEnglishBundle() {
        for (var c : Country.values()) {
            var value = I18nManager.getString(c.getLabelKey());
            assertFalse(value.startsWith("!"), "Missing English key: " + c.getLabelKey());
        }
    }

    @Test
    void countryLabelKeysExistInSpanishBundle() {
        I18nManager.setLanguage("es");
        try {
            for (var c : Country.values()) {
                var value = I18nManager.getString(c.getLabelKey());
                assertFalse(value.startsWith("!"), "Missing Spanish key: " + c.getLabelKey());
            }
        } finally {
            I18nManager.setLanguage("en");
        }
    }

    @Test
    void mainLanguageKeysExistInBundles() {
        I18nManager.setLanguage("en");
        assertFalse(I18nManager.getString("main.language.en").startsWith("!"));
        assertFalse(I18nManager.getString("main.language.es").startsWith("!"));
        I18nManager.setLanguage("es");
        try {
            assertFalse(I18nManager.getString("main.language.en").startsWith("!"));
            assertFalse(I18nManager.getString("main.language.es").startsWith("!"));
        } finally {
            I18nManager.setLanguage("en");
        }
    }

    @Test
    void countryTooltipKeyExistsInBundles() {
        I18nManager.setLanguage("en");
        assertFalse(I18nManager.getString("main.country.tooltip").startsWith("!"));
        I18nManager.setLanguage("es");
        try {
            assertFalse(I18nManager.getString("main.country.tooltip").startsWith("!"));
        } finally {
            I18nManager.setLanguage("en");
        }
    }
}