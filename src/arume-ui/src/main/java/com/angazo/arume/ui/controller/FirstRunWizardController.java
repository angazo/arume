package com.angazo.arume.ui.controller;

import java.io.File;

import com.angazo.arume.ui.config.ThemeConfig;
import com.angazo.arume.ui.i18n.I18nManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FirstRunWizardController {

    @FXML
    private ComboBox<String> languageCombo;

    @FXML
    private Label languageLabel;

    @FXML
    private ComboBox<String> themeCombo;

    @FXML
    private Label themeLabel;

    @FXML
    private ComboBox<String> dbTypeCombo;

    @FXML
    private Label dbTypeLabel;

    @FXML
    private Label h2SettingsLabel;

    @FXML
    private Label storagePathLabel;

    @FXML
    private TextField storagePathField;

    @FXML
    private Button browseButton;

    @FXML
    private Label storageHintLabel;

    @FXML
    private Label credentialsLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private CheckBox encryptCheckbox;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    private WizardResult result;
    private boolean saved = false;

    @FXML
    public void initialize() {
        var dbTypes = FXCollections.observableArrayList(
            I18nManager.getString("wizard.dbType.h2")
        );
        dbTypeCombo.setItems(dbTypes);
        dbTypeCombo.getSelectionModel().selectFirst();

        var languages = FXCollections.observableArrayList(
            I18nManager.getString("wizard.lang.en"),
            I18nManager.getString("wizard.lang.es")
        );
        languageCombo.setItems(languages);

        var currentLanguage = I18nManager.getCurrentLanguage();
        languageCombo.getSelectionModel().select("es".equals(currentLanguage) ? 1 : 0);

        languageCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) return;
            var code = I18nManager.getString("wizard.lang.es").equals(newVal) ? "es" : "en";
            I18nManager.setLanguage(code);
        });

        var themes = FXCollections.observableArrayList(
            I18nManager.getString("wizard.theme.light"),
            I18nManager.getString("wizard.theme.dark"),
            I18nManager.getString("wizard.theme.darkIntense")
        );
        themeCombo.setItems(themes);
        themeCombo.getSelectionModel().selectFirst();

        themeCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) return;
            var themeId = resolveThemeId(newVal);
            com.angazo.arume.ui.config.ThemeConfig.fromId(themeId).apply();
        });

        I18nManager.onLanguageChange(this::refreshTexts);

        refreshTexts();
    }

    private void refreshTexts() {
        var stageScene = saveButton.getScene();
        if (stageScene != null) {
            var stage = (Stage) stageScene.getWindow();
            stage.setTitle(I18nManager.getString("wizard.title"));
        }

        languageLabel.setText(I18nManager.getString("wizard.language"));
        var items = languageCombo.getItems();
        items.set(0, I18nManager.getString("wizard.lang.en"));
        items.set(1, I18nManager.getString("wizard.lang.es"));

        themeLabel.setText(I18nManager.getString("wizard.theme"));
        var themeItems = themeCombo.getItems();
        themeItems.set(0, I18nManager.getString("wizard.theme.light"));
        themeItems.set(1, I18nManager.getString("wizard.theme.dark"));
        themeItems.set(2, I18nManager.getString("wizard.theme.darkIntense"));

        dbTypeLabel.setText(I18nManager.getString("wizard.dbType"));
        dbTypeCombo.getItems().set(0, I18nManager.getString("wizard.dbType.h2"));

        h2SettingsLabel.setText(I18nManager.getString("wizard.h2Settings"));
        storagePathLabel.setText(I18nManager.getString("wizard.storagePath"));
        browseButton.setText(I18nManager.getString("wizard.browse"));
        storageHintLabel.setText(I18nManager.getString("wizard.storageHint"));

        credentialsLabel.setText(I18nManager.getString("wizard.credentials"));
        usernameLabel.setText(I18nManager.getString("wizard.username"));
        passwordLabel.setText(I18nManager.getString("wizard.password"));
        confirmPasswordLabel.setText(I18nManager.getString("wizard.confirmPassword"));

        encryptCheckbox.setText(I18nManager.getString("wizard.encrypt"));
        cancelButton.setText(I18nManager.getString("wizard.cancel"));
        saveButton.setText(I18nManager.getString("wizard.save"));
    }

    public void setDefaultStoragePath(String path) {
        storagePathField.setText(path);
    }

    @FXML
    public void onBrowse() {
        var chooser = new DirectoryChooser();
        chooser.setTitle(I18nManager.getString("wizard.browseTitle"));
        var currentPath = storagePathField.getText();
        if (currentPath != null && !currentPath.isBlank()) {
            var dir = new File(currentPath);
            if (dir.exists() && dir.isDirectory()) {
                chooser.setInitialDirectory(dir);
            }
        }
        var selected = chooser.showDialog(browseButton.getScene().getWindow());
        if (selected != null) {
            storagePathField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    public void onSave() {
        if (!validateForm()) {
            return;
        }
        var themeId = resolveThemeId(themeCombo.getValue());
        result = new WizardResult(
            I18nManager.getCurrentLanguage(),
            "h2",
            storagePathField.getText().trim(),
            usernameField.getText().trim(),
            passwordField.getText(),
            encryptCheckbox.isSelected(),
            themeId
        );
        saved = true;
        closeWindow();
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        var storagePath = storagePathField.getText();
        if (storagePath == null || storagePath.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.emptyPath");
            return false;
        }

        var username = usernameField.getText();
        if (username == null || username.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.emptyUser");
            return false;
        }

        var password = passwordField.getText();
        if (password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.emptyPassword");
            return false;
        }
        if (password.length() < 12) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.passwordLength");
            return false;
        }

        var confirm = confirmPasswordField.getText();
        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.passwordMismatch");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String titleKey, String messageKey) {
        var alert = new Alert(type);
        alert.setTitle(I18nManager.getString(titleKey));
        alert.setHeaderText(null);
        alert.setContentText(I18nManager.getString(messageKey));
        alert.showAndWait();
    }

    private void closeWindow() {
        var stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public WizardResult getResult() {
        return result;
    }

    public boolean isSaved() {
        return saved;
    }

    private static String resolveThemeId(String displayLabel) {
        if (displayLabel == null) return "light";
        for (var theme : ThemeConfig.values()) {
            if (I18nManager.getString(theme.getLabelKey()).equals(displayLabel)) {
                return theme.getId();
            }
        }
        return "light";
    }
}
