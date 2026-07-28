package com.angazo.arume.ui.controller;

import com.angazo.arume.ui.config.ConfigManager;
import com.angazo.arume.ui.config.ThemeConfig;
import com.angazo.arume.ui.i18n.I18nManager;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.springframework.stereotype.Component;

@Component
public class MainController {

    @FXML
    private MenuButton languageButton;

    @FXML
    private RadioMenuItem englishItem;

    @FXML
    private RadioMenuItem spanishItem;

    @FXML
    private MenuButton themeButton;

    @FXML
    private MenuItem lightThemeItem;

    @FXML
    private MenuItem darkThemeItem;

    @FXML
    private MenuItem darkIntenseThemeItem;

    @FXML
    private Region toolBarSpacer;

    private final ConfigManager configManager = new ConfigManager();

    @FXML
    public void initialize() {
        HBox.setHgrow(toolBarSpacer, Priority.ALWAYS);
        selectCurrentLanguage();
        selectCurrentTheme();
        refreshTexts();

        I18nManager.onLanguageChange(this::onLanguageChanged);
    }

    @FXML
    public void onEnglishSelected() {
        if (!"en".equals(I18nManager.getCurrentLanguage())) {
            I18nManager.setLanguage("en");
            configManager.updateLanguage("en");
        }
    }

    @FXML
    public void onSpanishSelected() {
        if (!"es".equals(I18nManager.getCurrentLanguage())) {
            I18nManager.setLanguage("es");
            configManager.updateLanguage("es");
        }
    }

    @FXML
    public void onLightThemeSelected() {
        applyTheme("light");
    }

    @FXML
    public void onDarkThemeSelected() {
        applyTheme("dark");
    }

    @FXML
    public void onDarkIntenseThemeSelected() {
        applyTheme("dark-intense");
    }

    private void applyTheme(String themeId) {
        var config = configManager.load();
        if (themeId.equals(config.theme())) return;
        ThemeConfig.fromId(themeId).apply();
        configManager.updateTheme(themeId);
        selectCurrentTheme();
    }

    private void onLanguageChanged() {
        selectCurrentLanguage();
        refreshTexts();
    }

    private void selectCurrentLanguage() {
        var current = I18nManager.getCurrentLanguage();
        if ("es".equals(current)) {
            spanishItem.setSelected(true);
            languageButton.setText(I18nManager.getString("main.menu.spanish"));
        } else {
            englishItem.setSelected(true);
            languageButton.setText(I18nManager.getString("main.menu.english"));
        }
    }

    private static final String ICON_LIGHT = "\u2600\uFE0F";
    private static final String ICON_DARK = "\uD83C\uDF19\uFE0F";
    private static final String ICON_DARK_INTENSE = "\uD83C\uDF11\uFE0F";

    private void selectCurrentTheme() {
        var config = configManager.load();
        var themeId = config.theme();
        if ("dark".equals(themeId)) {
            themeButton.setText(ICON_DARK);
        } else if ("dark-intense".equals(themeId)) {
            themeButton.setText(ICON_DARK_INTENSE);
        } else {
            themeButton.setText(ICON_LIGHT);
        }
    }

    private void refreshTexts() {
        selectCurrentLanguage();
        selectCurrentTheme();
        englishItem.setText(I18nManager.getString("main.menu.english"));
        spanishItem.setText(I18nManager.getString("main.menu.spanish"));
        lightThemeItem.setText(ICON_LIGHT);
        darkThemeItem.setText(ICON_DARK);
        darkIntenseThemeItem.setText(ICON_DARK_INTENSE);
    }
}
