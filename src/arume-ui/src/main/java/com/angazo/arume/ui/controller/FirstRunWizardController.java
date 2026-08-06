package com.angazo.arume.ui.controller;

import java.io.File;

import com.angazo.arume.ui.config.ThemeConfig;
import com.angazo.arume.ui.i18n.I18nManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
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
    private TextField passwordTextField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Label dbEncryptHeaderLabel;

    @FXML
    private Label dbEncryptPasswordLabel;

    @FXML
    private PasswordField dbEncryptPasswordField;

    @FXML
    private TextField dbEncryptTextField;

    @FXML
    private Button toggleDbEncryptButton;

    @FXML
    private CheckBox encryptCheckbox;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private HBox titleBar;

    @FXML
    private Button closeBtn;

    @FXML
    private ImageView logoView;

    private WizardResult result;
    private boolean saved = false;
    private double dragOffsetX;
    private double dragOffsetY;

    @FXML
    public void initialize() {
        closeBtn.setText("\u2715");
        logoView.setImage(new Image(getClass().getResourceAsStream("/icons/arume.png")));
        setupTitleBarDrag();

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

        var defaultLanguage = I18nManager.detectDefaultLanguage();
        var defaultLanguageLabel = I18nManager.getString("wizard.lang." + defaultLanguage);
        languageCombo.getSelectionModel().select(defaultLanguageLabel);

        languageCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) return;
            var code = I18nManager.getString("wizard.lang.es").equals(newVal) ? "es" : "en";
            I18nManager.setLanguage(code);
        });

        var themes = FXCollections.<String>observableArrayList();
        for (var theme : ThemeConfig.values()) {
            themes.add(I18nManager.getString(theme.getLabelKey()));
        }
        themeCombo.setItems(themes);
        themeCombo.getSelectionModel().selectFirst();

        themeCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) return;
            var themeId = resolveThemeId(newVal);
            com.angazo.arume.ui.config.ThemeConfig.fromId(themeId).apply();
        });

        togglePasswordButton.setGraphic(createEyeIcon(true));
        togglePasswordButton.setText("");
        toggleDbEncryptButton.setGraphic(createEyeIcon(true));
        toggleDbEncryptButton.setText("");

        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        dbEncryptTextField.textProperty().bindBidirectional(dbEncryptPasswordField.textProperty());

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
    var configThemes = ThemeConfig.values();
    for (var i = 0; i < configThemes.length; i++) {
        if (i < themeItems.size()) {
            themeItems.set(i, I18nManager.getString(configThemes[i].getLabelKey()));
        }
    }

    dbTypeLabel.setText(I18nManager.getString("wizard.dbType"));
    dbTypeCombo.getItems().set(0, I18nManager.getString("wizard.dbType.h2"));

    h2SettingsLabel.setText(I18nManager.getString("wizard.h2Settings"));
    storagePathLabel.setText(I18nManager.getString("wizard.storagePath"));
    browseButton.setText(I18nManager.getString("wizard.browse"));
    storageHintLabel.setText(I18nManager.getString("wizard.storageHint"));

    credentialsLabel.setText(I18nManager.getString("wizard.credentials"));
    usernameLabel.setText(I18nManager.getString("wizard.username"));
    passwordLabel.setText(I18nManager.getString("wizard.password"));

    dbEncryptHeaderLabel.setText(I18nManager.getString("wizard.dbEncryptHeader"));
    dbEncryptPasswordLabel.setText(I18nManager.getString("wizard.dbEncryptPassword"));

    encryptCheckbox.setText(I18nManager.getString("wizard.encrypt"));
    cancelButton.setText(I18nManager.getString("wizard.cancel"));
    saveButton.setText(I18nManager.getString("wizard.save"));
}

    @FXML
    public void onTogglePassword() {
        togglePasswordVisibility(passwordField, passwordTextField);
    }

    @FXML
    public void onToggleDbEncryptPassword() {
        togglePasswordVisibility(dbEncryptPasswordField, dbEncryptTextField);
    }

    private void togglePasswordVisibility(PasswordField passwordField, TextField textField) {
        var show = !textField.isVisible();
        textField.setVisible(show);
        textField.setManaged(show);
        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
        var button = passwordField == this.passwordField ? togglePasswordButton : toggleDbEncryptButton;
        button.setGraphic(createEyeIcon(!show));
    }

    private Node createEyeIcon(boolean open) {
        var scale = 0.75;

        var outline = new SVGPath();
        outline.setContent("M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6z");
        outline.setStroke(Color.web("#7a7a7a"));
        outline.setFill(null);
        outline.setStrokeWidth(2.5);
        outline.setScaleX(scale);
        outline.setScaleY(scale);

        var pupil = new SVGPath();
        pupil.setContent("M 9,12 a 3,3 0 1,1 6,0 a 3,3 0 1,1 -6,0");
        pupil.setStroke(Color.web("#7a7a7a"));
        pupil.setFill(null);
        pupil.setStrokeWidth(2.5);
        pupil.setScaleX(scale);
        pupil.setScaleY(scale);

        var group = new Group(outline, pupil);

        if (!open) {
            var slash = new SVGPath();
            slash.setContent("M 3.5 3.5 L 20.5 20.5");
            slash.setStroke(Color.web("#7a7a7a"));
            slash.setFill(null);
            slash.setStrokeWidth(2.5);
            slash.setScaleX(scale);
            slash.setScaleY(scale);
            group.getChildren().add(slash);
        }

        return group;
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
        dbEncryptPasswordField.getText(),
        encryptCheckbox.isSelected(),
        themeId
    );
    saved = true;
    closeWindow();
}

    private void setupTitleBarDrag() {
        titleBar.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                var stage = (Stage) titleBar.getScene().getWindow();
                dragOffsetX = event.getScreenX() - stage.getX();
                dragOffsetY = event.getScreenY() - stage.getY();
            }
        });

        titleBar.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                var stage = (Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() - dragOffsetX);
                stage.setY(event.getScreenY() - dragOffsetY);
            }
        });
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

        var dbEncryptPassword = dbEncryptPasswordField.getText();
        if (dbEncryptPassword == null || dbEncryptPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.emptyDbEncryptPassword");
            return false;
        }
        if (dbEncryptPassword.length() < 12) {
            showAlert(Alert.AlertType.ERROR, "validation.title", "validation.dbEncryptPasswordLength");
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
