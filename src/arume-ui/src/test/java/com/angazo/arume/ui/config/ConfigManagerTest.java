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
            "jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
            "org.h2.Driver",
            "admin",
            "mypassword12"
        );

        configManager.save(config);
        assertTrue(configManager.exists());

        var loaded = configManager.load();
        assertEquals("en", loaded.language());
        assertEquals("h2", loaded.dbType());
        assertFalse(loaded.encrypt());
        assertEquals("jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", loaded.url());
        assertEquals("org.h2.Driver", loaded.driverClassName());
        assertEquals("admin", loaded.username());
        assertEquals("mypassword12", loaded.password());
    }

    @Test
    void shouldApplyConfigToSystemProperties() {
        var config = new ArumeConfig(
            "en",
            "h2",
            false,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL",
            "org.h2.Driver",
            "user1",
            "secret123456"
        );

        configManager.applyToSystemProperties(config);

        assertEquals("jdbc:h2:file:/tmp/db;MODE=PostgreSQL", System.getProperty("spring.datasource.url"));
        assertEquals("org.h2.Driver", System.getProperty("spring.datasource.driver-class-name"));
        assertEquals("user1", System.getProperty("spring.datasource.username"));
        assertEquals("secret123456", System.getProperty("spring.datasource.password"));
    }

    @Test
    void shouldResolveJarDir() {
        var dir = ConfigManager.resolveJarDir();
        assertNotNull(dir);
        assertTrue(Files.exists(dir));
    }

    @Test
    void shouldBuildH2Url() {
        var url = configManager.buildH2Url(Path.of("/tmp/data"));
        assertEquals("jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1", url);
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
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL",
            "org.h2.Driver",
            "admin",
            "securepass12"
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
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL",
            "org.h2.Driver",
            "admin",
            "pass12345678"
        );

        configManager.save(config);
        var loaded = configManager.load();
        assertEquals("es", loaded.language());
    }

    @Test
    void shouldDefaultToEnglishWhenLanguageMissing() throws Exception {
        var yml = "arume:\n  db:\n    type: h2\n    encrypt: false\nspring:\n  datasource:\n    url: jdbc:h2:file:/tmp/db\n    driver-class-name: org.h2.Driver\n    username: admin\n    password: pass12345678\n";
        Files.writeString(tempDir.resolve("arume.yml"), yml);

        var loaded = configManager.load();
        assertEquals("en", loaded.language());
    }

    @Test
    void shouldUpdateLanguage() {
        var config = new ArumeConfig(
            "en",
            "h2",
            false,
            "jdbc:h2:file:/tmp/db;MODE=PostgreSQL",
            "org.h2.Driver",
            "admin",
            "pass12345678"
        );
        configManager.save(config);

        configManager.updateLanguage("es");
        var loaded = configManager.load();
        assertEquals("es", loaded.language());
        assertEquals("h2", loaded.dbType());
        assertEquals("admin", loaded.username());
    }
}
