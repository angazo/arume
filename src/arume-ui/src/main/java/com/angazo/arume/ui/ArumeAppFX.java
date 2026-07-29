package com.angazo.arume.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import com.angazo.arume.ui.config.ArumeConfig;
import com.angazo.arume.ui.config.ConfigException;
import com.angazo.arume.ui.config.ConfigManager;
import com.angazo.arume.ui.config.ThemeConfig;
import com.angazo.arume.ui.controller.FirstRunWizardController;
import com.angazo.arume.ui.controller.WizardResult;
import com.angazo.arume.ui.i18n.I18nManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class ArumeAppFX {

    public void launch(Supplier<ConfigurableApplicationContext> springBootStarter, String[] args) {
        ApplicationLoader.setSpringBootStarter(springBootStarter);
        ApplicationLoader.launch(ApplicationLoader.class, args);
    }

    public static class ApplicationLoader extends Application {

        @Setter
        private static Supplier<ConfigurableApplicationContext> springBootStarter;

        private ConfigManager configManager;
        private ConfigurableApplicationContext springContext;

        @Override
        public void init() {
            configManager = new ConfigManager();
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            ThemeConfig.LIGHT.apply();
            primaryStage.setTitle("Arume");
            primaryStage.setScene(new Scene(new BorderPane(), 800, 600));
            primaryStage.centerOnScreen();
            primaryStage.show();

            ArumeConfig config;

            if (configManager.exists()) {
                log.info("Configuration found, attempting to load");
                try {
                    config = configManager.load();
                } catch (ConfigException e) {
                    log.error("Failed to load configuration", e);
                    if (handleDecryptError(primaryStage)) {
                        Files.deleteIfExists(configManager.getJarDir().resolve("arume.yml"));
                        config = runWizardFlow(primaryStage);
                        if (config == null) { Platform.exit(); return; }
                    } else {
                        Platform.exit();
                        return;
                    }
                }
            } else {
                log.info("No configuration found, showing first-run wizard");
                config = runWizardFlow(primaryStage);
                if (config == null) { Platform.exit(); return; }
            }

            ThemeConfig.fromId(config.theme()).apply();
            primaryStage.setTitle(I18nManager.getString("app.name"));

            ConfigManager.ensureStorageDir(config);
            configManager.applyToSystemProperties(config);
            log.info("Starting Spring Boot...");
            springContext = springBootStarter.get();

            replaceWithMainScene(primaryStage);
        }

        @Override
        public void stop() {
            if (springContext != null) {
                springContext.close();
            }
        }

        private ArumeConfig runWizardFlow(Stage owner) {
            I18nManager.init(I18nManager.detectDefaultLanguage());
            var wizardResult = showFirstRunWizard(owner);
            if (wizardResult == null) {
                log.info("First-run wizard cancelled, exiting application");
                return null;
            }
            var config = buildConfigFromWizard(wizardResult);
            configManager.save(config);
            return config;
        }

        private WizardResult showFirstRunWizard(Stage owner) {
            try {
                var fxmlLocation = getClass().getResource("/fxml/first-run-wizard.fxml");
                var loader = new FXMLLoader(fxmlLocation);
                Parent root = loader.load();

                var controller = (FirstRunWizardController) loader.getController();
                controller.setDefaultStoragePath(configManager.getDefaultDbDir().toString());

                var scene = new Scene(root, 494, 800);
                var wizardStage = new Stage();
                wizardStage.setTitle(I18nManager.getString("wizard.title"));
                wizardStage.initOwner(owner);
                wizardStage.initModality(Modality.APPLICATION_MODAL);
                wizardStage.setResizable(false);
                wizardStage.setScene(scene);

                wizardStage.setOnShown(e -> {
                    double x = owner.getX() + (owner.getWidth() - wizardStage.getWidth()) / 2;
                    double y = owner.getY() + (owner.getHeight() - wizardStage.getHeight()) / 2;
                    wizardStage.setX(x);
                    wizardStage.setY(y);
                });

                wizardStage.showAndWait();

                return controller.getResult();
            } catch (Exception e) {
                log.error("Failed to load first-run wizard", e);
                return null;
            }
        }

        private ArumeConfig buildConfigFromWizard(WizardResult result) {
            var url = configManager.buildH2Url(
                Path.of(result.storagePath()),
                result.username(),
                result.password(),
                result.dbEncryptPassword(),
                result.encrypt()
            );
            return new ArumeConfig(
                result.language(),
                result.dbType(),
                result.encrypt(),
                url,
                "org.h2.Driver",
                result.theme()
            );
        }

        private boolean handleDecryptError(Stage primaryStage) {
            I18nManager.init(I18nManager.detectDefaultLanguage());
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(I18nManager.getString("decrypt.error.title"));
            alert.setHeaderText(I18nManager.getString("decrypt.error.header"));
            alert.setContentText(I18nManager.getString("decrypt.error.content"));

            var reconfigure = new ButtonType(I18nManager.getString("decrypt.error.reconfigure"));
            var exit = new ButtonType(I18nManager.getString("decrypt.error.exit"));
            alert.getButtonTypes().setAll(reconfigure, exit);

            alert.initOwner(primaryStage);
            var result = alert.showAndWait();

            return result.isPresent() && result.get() == reconfigure;
        }

        private void replaceWithMainScene(Stage stage) throws Exception {
            var loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 800, 600));
        }
    }
}
