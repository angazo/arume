package com.angazo.arume.app;

import com.angazo.arume.ui.ArumeAppFX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.angazo.arume")
@Slf4j
public class ArumeApp {

    public static void main(String[] args) {
        var appFX = new ArumeAppFX();
        appFX.launch(ArumeApp::startSpringBoot, args);
    }

    static ConfigurableApplicationContext startSpringBoot() {
        log.info("Creating Spring context...");
        return SpringApplication.run(ArumeApp.class);
    }
}
