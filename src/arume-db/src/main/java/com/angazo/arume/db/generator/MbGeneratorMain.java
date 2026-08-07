package com.angazo.arume.db.generator;

import org.flywaydb.core.Flyway;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Entry point for the {@code mbGenerator} Gradle task.
 *
 * Applies the project's Flyway migrations to a fresh in-memory H2 database and
 * then runs MyBatis Generator against it. It runs in a forked JVM so that the
 * JDBC connection (which MBG never closes) cannot lock a long-lived Gradle
 * daemon.
 */
public final class MbGeneratorMain {

    private MbGeneratorMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: MbGeneratorMain <configFile> <projectDir> <dbUrl> <dbUser> <dbPassword>");
            System.exit(2);
        }

        File configFile = new File(args[0]);
        String projectDir = args[1];
        String dbUrl = args[2];
        String dbUser = args[3];
        String dbPassword = args[4];

        applyMigrations(dbUrl, dbUser, dbPassword);

        Properties properties = new Properties();
        properties.setProperty("projectDir", projectDir);
        properties.setProperty("mbgen.url", dbUrl);
        properties.setProperty("mbgen.user", dbUser);
        properties.setProperty("mbgen.password", dbPassword);

        ConfigurationParser parser = new ConfigurationParser(properties);
        var configuration = parser.parseConfiguration(configFile);

        DefaultShellCallback shellCallback = new DefaultShellCallback();

        MyBatisGenerator generator = new MyBatisGenerator.Builder()
                .withConfiguration(configuration)
                .withShellCallback(shellCallback)
                .withOverwriteEnabled(true)
                .build();

        List<String> warnings = generator.generateAndWrite();
        warnings.forEach(warning -> System.out.println("MyBatis Generator: " + warning));
    }

    private static void applyMigrations(String url, String user, String password) {
        var flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration/core")
                .load();
        flyway.migrate();
    }
}
