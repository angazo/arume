package com.angazo.arume.ui.config;

import java.util.Locale;

public enum Country {
    ES("es", "es"),
    GB("gb", "en"),
    US("us", "en"),
    CL("cl", "es"),
    SG("sg", "en"),
    AU("au", "en"),
    ZA("za", "en");

    private final String code;
    private final String officialLanguage;

    Country(String code, String officialLanguage) {
        this.code = code;
        this.officialLanguage = officialLanguage;
    }

    public String code() {
        return code;
    }

    public String officialLanguage() {
        return officialLanguage;
    }

    public String getLabelKey() {
        return "wizard.country." + code;
    }

    public static Country fromCode(String code) {
        if (code == null) return ES;
        var normalized = code.toLowerCase(Locale.ROOT);
        for (var c : values()) {
            if (c.code.equals(normalized)) return c;
        }
        return ES;
    }

    public static Country detectDefault() {
        return detectDefault(Locale.getDefault());
    }

    public static Country detectDefault(Locale locale) {
        var country = locale.getCountry();
        if (country == null || country.isBlank()) return ES;
        return fromCode(country.toLowerCase(Locale.ROOT));
    }
}