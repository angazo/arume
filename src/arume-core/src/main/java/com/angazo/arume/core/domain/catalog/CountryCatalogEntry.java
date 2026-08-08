package com.angazo.arume.core.domain.catalog;

import java.util.Objects;

import com.angazo.arume.core.domain.common.JurisdictionCode;

public record CountryCatalogEntry(JurisdictionCode code, String name) {

    public CountryCatalogEntry {
        Objects.requireNonNull(code, "code");
        name = requireText(name, "name");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
