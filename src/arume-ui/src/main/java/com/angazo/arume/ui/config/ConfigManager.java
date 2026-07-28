package com.angazo.arume.ui.config;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class ConfigManager {

    private static final String CONFIG_FILE = "arume.yml";
    private static final String DEFAULT_DATASOURCE_URL = "jdbc:h2:file:%s/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

    private final Path jarDir;
    private final Path configPath;

    public ConfigManager() {
        this.jarDir = resolveJarDir();
        this.configPath = jarDir.resolve(CONFIG_FILE);
        log.info("JAR directory: {}", jarDir);
        log.info("Config path: {}", configPath);
    }

    public ConfigManager(Path jarDir) {
        this.jarDir = jarDir;
        this.configPath = jarDir.resolve(CONFIG_FILE);
    }

    public boolean exists() {
        return Files.exists(configPath);
    }

    public ArumeConfig load() {
        var yaml = new Yaml();
        try (var input = new FileInputStream(configPath.toFile())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = yaml.load(input);

            @SuppressWarnings("unchecked")
            var arume = (Map<String, Object>) data.get("arume");

            @SuppressWarnings("unchecked")
            var db = (Map<String, Object>) arume.get("db");

            @SuppressWarnings("unchecked")
            var spring = (Map<String, Object>) data.get("spring");

            @SuppressWarnings("unchecked")
            var datasource = (Map<String, Object>) spring.get("datasource");

            return new ArumeConfig(
                (String) arume.getOrDefault("language", "en"),
                (String) db.get("type"),
                (boolean) db.getOrDefault("encrypt", false),
                (String) datasource.get("url"),
                (String) datasource.get("driver-class-name"),
                (String) datasource.get("username"),
                (String) datasource.get("password"),
                (String) arume.getOrDefault("theme", "light")
            );
        } catch (IOException e) {
            throw new ConfigException("Failed to load configuration from " + configPath, e);
        }
    }

    public void save(ArumeConfig config) {
        var db = new LinkedHashMap<String, Object>();
        db.put("type", config.dbType());
        db.put("encrypt", config.encrypt());

        var arume = new LinkedHashMap<String, Object>();
        arume.put("language", config.language());
        arume.put("theme", config.theme());
        arume.put("db", db);

        var datasource = new LinkedHashMap<String, Object>();
        datasource.put("url", config.url());
        datasource.put("driver-class-name", config.driverClassName());
        datasource.put("username", config.username());
        datasource.put("password", config.password());

        var spring = new LinkedHashMap<String, Object>();
        spring.put("datasource", datasource);

        var data = new LinkedHashMap<String, Object>();
        data.put("arume", arume);
        data.put("spring", spring);

        var options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        var yaml = new Yaml(options);
        try (var writer = new FileWriter(configPath.toFile())) {
            yaml.dump(data, writer);
            log.info("Configuration saved to {}", configPath);
        } catch (IOException e) {
            throw new ConfigException("Failed to save configuration to " + configPath, e);
        }
    }

    public void applyToSystemProperties(ArumeConfig config) {
        System.setProperty("spring.datasource.url", config.url());
        System.setProperty("spring.datasource.driver-class-name", config.driverClassName());
        System.setProperty("spring.datasource.username", config.username());
        System.setProperty("spring.datasource.password", config.password());
        log.info("Applied datasource configuration to system properties");
    }

    public void updateLanguage(String language) {
        var config = load();
        var updated = new ArumeConfig(
            language,
            config.dbType(),
            config.encrypt(),
            config.url(),
            config.driverClassName(),
            config.username(),
            config.password(),
            config.theme()
        );
        save(updated);
        log.info("Updated language to {} in configuration", language);
    }

    public void updateTheme(String theme) {
        var config = load();
        var updated = new ArumeConfig(
            config.language(),
            config.dbType(),
            config.encrypt(),
            config.url(),
            config.driverClassName(),
            config.username(),
            config.password(),
            theme
        );
        save(updated);
        log.info("Updated theme to {} in configuration", theme);
    }

    public Path getJarDir() {
        return jarDir;
    }

    public Path getDefaultDbDir() {
        return jarDir.resolve("data");
    }

    public String buildH2Url(Path storagePath) {
        return DEFAULT_DATASOURCE_URL.formatted(storagePath.toAbsolutePath());
    }

    public static void ensureStorageDir(ArumeConfig config) {
        var url = config.url();
        var prefix = "jdbc:h2:file:";
        if (!url.startsWith(prefix)) return;
        var remainder = url.substring(prefix.length());
        var semicolonIdx = remainder.indexOf(';');
        if (semicolonIdx >= 0) {
            remainder = remainder.substring(0, semicolonIdx);
        }
        var dbPath = Path.of(remainder);
        var dir = dbPath.getParent();
        if (dir == null) return;
        try {
            Files.createDirectories(dir);
            log.info("Ensured storage directory exists: {}", dir);
        } catch (IOException e) {
            throw new ConfigException("Failed to create storage directory: " + dir, e);
        }
    }

    public static Path resolveJarDir() {
        var jarFile = findJarFile();
        if (jarFile != null) {
            var parent = jarFile.toPath().getParent();
            if (parent != null) {
                log.info("Resolved JAR directory: {}", parent);
                return parent;
            }
        }
        log.info("Falling back to user.dir");
        return Path.of(System.getProperty("user.dir"));
    }

    private static java.io.File findJarFile() {
        for (var strategy : new JarResolutionStrategy[]{
            ConfigManager::fromProtectionDomain,
            ConfigManager::fromClasspath
        }) {
            var result = strategy.resolve();
            if (result != null && result.isFile()) {
                return result;
            }
        }
        return null;
    }

    private static java.io.File fromProtectionDomain() {
        try {
            var mainClass = Class.forName("com.angazo.arume.app.ArumeApp");
            return extractJarFile(mainClass.getProtectionDomain().getCodeSource().getLocation());
        } catch (Exception e) {
            try {
                return extractJarFile(ConfigManager.class.getProtectionDomain().getCodeSource().getLocation());
            } catch (Exception ex) {
                log.warn("Could not resolve via ProtectionDomain", ex);
                return null;
            }
        }
    }

    private static java.io.File fromClasspath() {
        var classpath = System.getProperty("java.class.path");
        if (classpath == null) return null;
        for (var entry : classpath.split(java.io.File.pathSeparator)) {
            var file = new java.io.File(entry);
            if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                return file;
            }
        }
        return null;
    }

    private static java.io.File extractJarFile(URL url) {
        if (url == null) return null;
        var path = url.toString();
        if (path.startsWith("jar:")) {
            path = path.substring(4);
            var exclIdx = path.indexOf("!");
            if (exclIdx >= 0) {
                path = path.substring(0, exclIdx);
            }
        }
        if (path.startsWith("file:")) {
            path = path.substring(5);
            // Handle triple slash file:///...
            while (path.startsWith("/")) {
                path = path.substring(1);
            }
            // Restore leading slash for absolute paths
            if (!path.startsWith("/") && (path.length() > 1 && path.charAt(1) != ':')) {
                path = "/" + path;
            }
        }
        var file = new java.io.File(path);
        return file.isFile() ? file : null;
    }

    @FunctionalInterface
    private interface JarResolutionStrategy {
        java.io.File resolve();
    }
}
