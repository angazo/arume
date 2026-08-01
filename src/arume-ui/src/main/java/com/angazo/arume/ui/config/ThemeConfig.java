package com.angazo.arume.ui.config;

import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;

public enum ThemeConfig {

    LIGHT("light", "wizard.theme.light", () -> new PrimerLight().getUserAgentStylesheet()),
    DARK("dark", "wizard.theme.dark", () -> new Dracula().getUserAgentStylesheet());

    private final String id;
    private final String labelKey;
    private final StyleSheetSupplier stylesheet;

    ThemeConfig(String id, String labelKey, StyleSheetSupplier stylesheet) {
        this.id = id;
        this.labelKey = labelKey;
        this.stylesheet = stylesheet;
    }

    public String getId() { return id; }

    public String getLabelKey() { return labelKey; }

    public void apply() {
        Application.setUserAgentStylesheet(stylesheet.get());
    }

    public static ThemeConfig fromId(String id) {
        if (id == null) return LIGHT;
        for (var theme : values()) {
            if (theme.id.equals(id)) return theme;
        }
        return LIGHT;
    }

    @FunctionalInterface
    private interface StyleSheetSupplier {
        String get();
    }
}
