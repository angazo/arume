package com.angazo.arume.ui.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18nManager {

    private static final String BUNDLE_BASE = "i18n/messages";

    private static ResourceBundle bundle;
    private static String currentLanguage;
    private static final List<Runnable> changeListeners = new ArrayList<>();

    public static void init(String language) {
        currentLanguage = language;
        reloadBundle();
    }

    public static void setLanguage(String language) {
        if (!language.equals(currentLanguage)) {
            currentLanguage = language;
            reloadBundle();
            notifyListeners();
        }
    }

    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static void onLanguageChange(Runnable listener) {
        changeListeners.add(listener);
    }

    public static String detectDefaultLanguage() {
        return detectDefaultLanguage(Locale.getDefault());
    }

    public static String detectDefaultLanguage(Locale locale) {
        var lang = locale.getLanguage();
        var country = locale.getCountry();

        if ("es".equals(lang)) return "es";
        if ("ES".equals(country) && List.of("ca", "gl", "eu").contains(lang)) return "es";
        return "en";
    }

    private static void reloadBundle() {
        bundle = ResourceBundle.getBundle(BUNDLE_BASE, Locale.forLanguageTag(currentLanguage));
    }

    private static void notifyListeners() {
        changeListeners.forEach(Runnable::run);
    }
}
