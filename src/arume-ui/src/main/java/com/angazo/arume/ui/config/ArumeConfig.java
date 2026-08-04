package com.angazo.arume.ui.config;

public record ArumeConfig(
    String country,
    String language,
    String dbType,
    boolean encrypt,
    String url,
    String driverClassName,
    String theme
) {

    public ArumeConfig {
        if (theme == null) theme = "light";
        if (country == null) country = "esp";
    }
}
