package com.angazo.arume.ui.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import com.angazo.arume.ui.controller.FirstRunWizardController;
import com.angazo.arume.ui.controller.WizardResult;
import com.angazo.arume.ui.i18n.I18nManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class FirstRunWizardUiTest {

    @TempDir
    Path tempDir;

    private FirstRunWizardController controller;
    private Path storagePath;
    private Stage stage;

    @Start
    private void start(Stage stage) throws IOException {
        this.stage = stage;
        I18nManager.init("en");

        var loader = new FXMLLoader(getClass().getResource("/fxml/first-run-wizard.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        storagePath = tempDir.resolve("data");
        controller.setDefaultStoragePath(storagePath.toString());

        var scene = new Scene(root, 494, 840);
        scene.getStylesheets().add(getClass().getResource("/css/arume.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void shouldSaveValidConfiguration(FxRobot robot) {
        robot.clickOn("#storagePathField");
        robot.push(KeyCode.CONTROL, KeyCode.A);
        robot.write(storagePath.toString());

        robot.clickOn("#usernameField");
        robot.write("test-user");

        robot.clickOn("#passwordField");
        robot.write("user-password-12");

        robot.clickOn("#dbEncryptPasswordField");
        robot.write("file-password-12");

        robot.clickOn("#saveButton");

        assertTrue(controller.isSaved());
        WizardResult result = controller.getResult();
        assertNotNull(result);
        assertEquals("en", result.language());
        assertEquals("h2", result.dbType());
        assertEquals(storagePath.toString(), result.storagePath());
        assertEquals("test-user", result.username());
        assertEquals("user-password-12", result.password());
        assertEquals("file-password-12", result.dbEncryptPassword());
        assertFalse(result.encrypt());
        assertEquals("light", result.theme());
        assertFalse(stage.isShowing());
    }
}
