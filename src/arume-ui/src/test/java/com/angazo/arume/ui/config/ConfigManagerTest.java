package com.angazo.arume.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigManagerTest {

    @TempDir
    Path tempDir;

    private ConfigManager configManager;

    @BeforeEach
    void setUp() {
        configManager = new ConfigManager(tempDir);
        System.clearProperty("spring.datasource.url");
        System.clearProperty("spring.datasource.driver-class-name");
        System.clearProperty("spring.datasource.username");
        System.clearProperty("spring.datasource.password");
    }

    @Test
    void shouldDetectConfigDoesNotExist() {
        assertFalse(configManager.exists());
    }

    @Test
    void shouldDetectConfigExists() throws Exception {
        Files.createFile(tempDir.resolve("arume.yml"));
        assertTrue(configManager.exists());
    }

    @Test
    void shouldSaveAndLoadConfig() {
        var config = new ArumeConfig(
            "en",
            "h2",
            false,
            "jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12",
            "org.h2.Driver",
            "light"
        );

        configManager.save(config);
        assertTrue(configManager.exists());

        var loaded = configManager.load();
        assertEquals("en", loaded.language());
        assertEquals("h2", loaded.dbType());
        assertFalse(loaded.encrypt());
        assertEquals("jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12", loaded.url());
        assertEquals("org.h2.Driver", loaded.driverClassName());
    }

    @Test
    void shouldApplyConfigToSystemProperties() {
        var config = new ArumeConfig(
            "en",
            "h2",
            false,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=user1;PASSWORD=filepass12 userpass12",
            "org.h2.Driver",
            "light"
        );

        configManager.applyToSystemProperties(config);

        assertEquals("jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=user1;PASSWORD=filepass12 userpass12", System.getProperty("spring.datasource.url"));
        assertEquals("org.h2.Driver", System.getProperty("spring.datasource.driver-class-name"));
        assertNull(System.getProperty("spring.datasource.username"), "username should not be set as system property");
        assertNull(System.getProperty("spring.datasource.password"), "password should not be set as system property");
    }

    @Test
    void shouldResolveJarDir() {
        var dir = ConfigManager.resolveJarDir();
        assertNotNull(dir);
        assertTrue(Files.exists(dir));
    }

    @Test
    void shouldBuildH2UrlAlwaysWithCipher() {
        var url = configManager.buildH2Url(Path.of("/tmp/data"), "admin", "userpass12", "filepass12", false);
        assertTrue(url.startsWith("jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"));
        assertTrue(url.contains(";CIPHER=AES"));
        assertTrue(url.contains(";USER=admin"));
        assertTrue(url.contains(";PASSWORD=filepass12 userpass12"));
    }

    @Test
    void shouldBuildH2UrlWithEncryptFlag() {
        var url = configManager.buildH2Url(Path.of("/tmp/data"), "admin", "userpass12", "filepass12", true);
        assertTrue(url.contains("CIPHER=AES"));
        assertTrue(url.contains(";USER=admin"));
        assertTrue(url.contains(";PASSWORD=filepass12 userpass12"));
    }

    @Test
    void shouldBuildH2UrlWithoutEncryptFlag() {
        var url = configManager.buildH2Url(Path.of("/tmp/data"), "admin", "userpass12", "filepass12", false);
        assertTrue(url.contains("CIPHER=AES"));
        assertTrue(url.contains(";USER=admin"));
        assertTrue(url.contains(";PASSWORD=filepass12 userpass12"));
    }

    @Test
    void shouldBuildH2UrlIncludeBothPasswords() {
        var url = configManager.buildH2Url(Path.of("/tmp/data"), "admin", "userpass12", "filepass12", false);
        assertTrue(url.contains(";USER=admin"));
        assertTrue(url.contains(";PASSWORD=filepass12 userpass12"));
    }

    @Test
    void shouldProvideDefaultDbDir() {
        var defaultDbDir = configManager.getDefaultDbDir();
        assertEquals(tempDir.resolve("data"), defaultDbDir);
    }

    @Test
    void shouldFailLoadingNonExistentConfig() {
        assertThrows(ConfigException.class, () -> configManager.load());
    }

    @Test
    void shouldHandleEncryptFlag() {
        var config = new ArumeConfig(
            "en",
            "h2",
            true,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12",
            "org.h2.Driver",
            "light"
        );

        configManager.save(config);
        var loaded = configManager.load();
        assertTrue(loaded.encrypt());
    }

    @Test
    void shouldSaveAndLoadLanguage() {
        var config = new ArumeConfig(
            "es",
            "h2",
            false,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12",
            "org.h2.Driver",
            "light"
        );

        configManager.save(config);
        var loaded = configManager.load();
        assertEquals("es", loaded.language());
    }

    @Test
    void shouldEncryptUrlWhenEncryptTrue() {
        var config = new ArumeConfig(
            "en",
            "h2",
            true,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12",
            "org.h2.Driver",
            "light"
        );

        configManager.save(config);

        var yaml = new org.yaml.snakeyaml.Yaml();
        try (var input = new java.io.FileInputStream(tempDir.resolve("arume.yml").toFile())) {
            @SuppressWarnings("unchecked")
            var data = (java.util.Map<String, Object>) yaml.load(input);
            @SuppressWarnings("unchecked")
            var spring = (java.util.Map<String, Object>) data.get("spring");
            @SuppressWarnings("unchecked")
            var datasource = (java.util.Map<String, Object>) spring.get("datasource");
            var url = (String) datasource.get("url");
            assertTrue(EncryptionService.isEncrypted(url), "URL should be encrypted in YAML");
            assertFalse(datasource.containsKey("username"), "YAML should not contain username");
            assertFalse(datasource.containsKey("password"), "YAML should not contain password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSavePlainUrlWhenEncryptFalse() {
        var plainUrl = "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12";
        var config = new ArumeConfig(
            "en",
            "h2",
            false,
            plainUrl,
            "org.h2.Driver",
            "light"
        );

        configManager.save(config);

        var yaml = new org.yaml.snakeyaml.Yaml();
        try (var input = new java.io.FileInputStream(tempDir.resolve("arume.yml").toFile())) {
            @SuppressWarnings("unchecked")
            var data = (java.util.Map<String, Object>) yaml.load(input);
            @SuppressWarnings("unchecked")
            var spring = (java.util.Map<String, Object>) data.get("spring");
            @SuppressWarnings("unchecked")
            var datasource = (java.util.Map<String, Object>) spring.get("datasource");
            var url = (String) datasource.get("url");
            assertFalse(EncryptionService.isEncrypted(url), "URL should be plain when encrypt=false");
            assertEquals(plainUrl, url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldLoadAndDecryptEncryptedUrl() {
        var plainUrl = "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12";
        var config = new ArumeConfig(
            "en",
            "h2",
            true,
            plainUrl,
            "org.h2.Driver",
            "light"
        );

        configManager.save(config);

        var loaded = configManager.load();
        assertTrue(loaded.encrypt());
        assertEquals(plainUrl, loaded.url());
    }

    @Test
    void shouldDefaultToEnglishWhenLanguageMissing() throws Exception {
        var yml = "arume:\n  db:\n    type: h2\n    encrypt: false\nspring:\n  datasource:\n    url: jdbc:h2:file:/tmp/db;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12\n    driver-class-name: org.h2.Driver\n";
        Files.writeString(tempDir.resolve("arume.yml"), yml);

        var loaded = configManager.load();
        assertEquals("en", loaded.language());
        assertEquals("jdbc:h2:file:/tmp/db;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12", loaded.url());
    }

    @Test
    void shouldIgnoreLegacyCountryKey() throws Exception {
        var yml = "arume:\n  country: chl\n  language: es\n  db:\n    type: h2\n    encrypt: false\nspring:\n  datasource:\n    url: jdbc:h2:file:/tmp/db;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12\n    driver-class-name: org.h2.Driver\n";
        Files.writeString(tempDir.resolve("arume.yml"), yml);

        var loaded = configManager.load();
        assertEquals("es", loaded.language());

        configManager.updateLanguage("en");
        var yaml = new org.yaml.snakeyaml.Yaml();
        try (var input = new java.io.FileInputStream(tempDir.resolve("arume.yml").toFile())) {
            @SuppressWarnings("unchecked")
            var data = (java.util.Map<String, Object>) yaml.load(input);
            @SuppressWarnings("unchecked")
            var arume = (java.util.Map<String, Object>) data.get("arume");
            assertFalse(arume.containsKey("country"), "Legacy country key should be dropped on save");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldUpdateLanguage() {
        var config = new ArumeConfig(
            "en",
            "h2",
            false,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12",
            "org.h2.Driver",
            "light"
        );
        configManager.save(config);

        configManager.updateLanguage("es");
        var loaded = configManager.load();
        assertEquals("es", loaded.language());
        assertEquals("h2", loaded.dbType());
    }

    @Test
    void shouldUpdateLanguagePreservingEncryptedUrl() {
        var plainUrl = "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12";
        var config = new ArumeConfig(
            "en",
            "h2",
            true,
            plainUrl,
            "org.h2.Driver",
            "light"
        );
        configManager.save(config);

        configManager.updateLanguage("es");
        var loaded = configManager.load();
        assertEquals("es", loaded.language());
        assertTrue(loaded.encrypt());
        assertEquals(plainUrl, loaded.url(), "URL should survive language update unchanged");
    }

    @Test
    void shouldUpdateThemePreservingEncryptedUrl() {
        var plainUrl = "jdbc:h2:file:/tmp/db;MODE=PostgreSQL;CIPHER=AES;USER=admin;PASSWORD=filepass12 userpass12";
        var config = new ArumeConfig(
            "en",
            "h2",
            true,
            plainUrl,
            "org.h2.Driver",
            "light"
        );
        configManager.save(config);

        configManager.updateTheme("dark");
        var loaded = configManager.load();
        assertEquals("dark", loaded.theme());
        assertTrue(loaded.encrypt());
        assertEquals(plainUrl, loaded.url(), "URL should survive theme update unchanged");
    }
}
