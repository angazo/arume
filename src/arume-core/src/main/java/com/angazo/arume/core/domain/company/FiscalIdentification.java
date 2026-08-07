package com.angazo.arume.core.domain.company;

import java.util.Objects;

import com.angazo.arume.core.domain.common.JurisdictionCode;

public record FiscalIdentification(JurisdictionCode jurisdiction, String value) {

    public FiscalIdentification {
        Objects.requireNonNull(jurisdiction, "jurisdiction");
        value = requireText(value, "value");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
