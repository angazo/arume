package com.angazo.arume.ui.controller;

import com.angazo.arume.ui.config.ConfigManager;
import com.angazo.arume.ui.i18n.I18nManager;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import org.springframework.stereotype.Component;

@Component
public class MainController {

    @FXML
    private Menu languageMenu;

    @FXML
    private RadioMenuItem englishItem;

    @FXML
    private RadioMenuItem spanishItem;

    @FXML
    private ToggleGroup languageToggleGroup;

    private final ConfigManager configManager = new ConfigManager();

    @FXML
    public void initialize() {
        selectCurrentLanguage();
        refreshMenuTexts();

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

    private void onLanguageChanged() {
        selectCurrentLanguage();
        refreshMenuTexts();
    }

    private void selectCurrentLanguage() {
        var current = I18nManager.getCurrentLanguage();
        var target = "es".equals(current) ? spanishItem : englishItem;
        target.setSelected(true);
    }

    private void refreshMenuTexts() {
        languageMenu.setText(I18nManager.getString("main.menu.language"));
        englishItem.setText(I18nManager.getString("main.menu.english"));
        spanishItem.setText(I18nManager.getString("main.menu.spanish"));
    }
}
