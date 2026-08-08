package com.angazo.arume.es.generator;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public final class EsMbGeneratorMain {

    private EsMbGeneratorMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            throw new IllegalArgumentException(
                "Usage: EsMbGeneratorMain <configFile> <projectDir> <dbUrl> <dbUser> <dbPassword>"
            );
        }

        var configFile = new File(args[0]);
        var projectDir = args[1];
        var dbUrl = args[2];
        var dbUser = args[3];
        var dbPassword = args[4];

        migrateCoreAndSpain(dbUrl, dbUser, dbPassword);

        var properties = new Properties();
        properties.setProperty("projectDir", projectDir);
        properties.setProperty("mbgen.url", dbUrl);
        properties.setProperty("mbgen.user", dbUser);
        properties.setProperty("mbgen.password", dbPassword);

        var configuration = new ConfigurationParser(properties).parseConfiguration(configFile);
        var generator = new MyBatisGenerator.Builder()
            .withConfiguration(configuration)
            .withShellCallback(new DefaultShellCallback())
            .withOverwriteEnabled(true)
            .build();

        List<String> warnings = generator.generateAndWrite();
        warnings.forEach(warning -> System.out.println("Spain MyBatis Generator: " + warning));
    }

    private static void migrateCoreAndSpain(String url, String user, String password) {
        Flyway.configure()
            .dataSource(url, user, password)
            .locations("classpath:db/migration/core")
            .table("_flyway_core_schema_history")
            .load()
            .migrate();

        Flyway.configure()
            .dataSource(url, user, password)
            .locations("classpath:db/migration/es")
            .table("_flyway_es_schema_history")
            .baselineOnMigrate(true)
            .baselineVersion("0.0.0.0")
            .load()
            .migrate();
    }
}
