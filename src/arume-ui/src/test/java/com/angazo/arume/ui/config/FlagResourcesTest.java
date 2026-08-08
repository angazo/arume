package com.angazo.arume.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class FlagResourcesTest {

    private static final List<String> FLAG_CODES = List.of("es", "gb", "us", "cl", "sg", "au", "za");

    @Test
    void flagPngExistsForEverySupportedCode() {
        for (var code : FLAG_CODES) {
            var path = "/icons/flags/" + code + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                assertNotNull(in, "Missing flag PNG: " + path);
            } catch (Exception e) {
                fail("Cannot read flag PNG " + path + ": " + e.getMessage());
            }
        }
    }

    @Test
    void flagPngIsHighResolution() {
        for (var code : FLAG_CODES) {
            var path = "/icons/flags/" + code + ".png";
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
    void noFlagPngIsKeyedByAlpha3() {
        for (var code : List.of("esp", "gbr", "usa", "chl", "sgp", "aus", "zaf")) {
            var path = "/icons/flags/" + code + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                assertNull(in, "Flag PNG should be keyed by alpha-2 but found: " + path);
            } catch (Exception e) {
                fail("Cannot check flag PNG " + path + ": " + e.getMessage());
            }
        }
    }

    @Test
    void flagPngAspectRatioIsLandscape4To3() {
        for (var code : FLAG_CODES) {
            var path = "/icons/flags/" + code + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                var image = ImageIO.read(in);
                assertNotNull(image, "Cannot decode flag PNG: " + path);
                assertEquals(image.getHeight() * 4L, image.getWidth() * 3L,
                        "Flag PNG aspect ratio must be 4:3 (kept for future company-active flag): " + path);
            } catch (Exception e) {
                fail("Cannot read flag PNG " + path + ": " + e.getMessage());
            }
        }
    }
}
