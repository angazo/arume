package com.angazo.arume.ui.config;

import java.util.Locale;

public enum Country {
    ESP("esp", "es", "es"),
    GBR("gbr", "gb", "en"),
    USA("usa", "us", "en"),
    CHL("chl", "cl", "es"),
    SGP("sgp", "sg", "en"),
    AUS("aus", "au", "en"),
    ZAF("zaf", "za", "en");

    private final String alpha3;
    private final String alpha2;
    private final String officialLanguage;

    Country(String alpha3, String alpha2, String officialLanguage) {
        this.alpha3 = alpha3;
        this.alpha2 = alpha2;
        this.officialLanguage = officialLanguage;
    }

    public String code() {
        return alpha3;
    }

    public String officialLanguage() {
        return officialLanguage;
    }

    public String getLabelKey() {
        return "wizard.country." + alpha3;
    }

    public static Country fromCode(String code) {
        if (code == null) return ESP;
        var normalized = code.toLowerCase(Locale.ROOT);
        for (var c : values()) {
            if (c.alpha3.equals(normalized)) return c;
        }
        return ESP;
    }

    public static Country detectDefault() {
        return detectDefault(Locale.getDefault());
    }

    public static Country detectDefault(Locale locale) {
        var country = locale.getCountry();
        if (country == null || country.isBlank()) return ESP;
        var normalized = country.toLowerCase(Locale.ROOT);
        for (var c : values()) {
            if (c.alpha2.equals(normalized)) return c;
        }
        return ESP;
    }
}