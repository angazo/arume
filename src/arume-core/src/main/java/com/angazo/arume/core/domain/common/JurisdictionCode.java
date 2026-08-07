package com.angazo.arume.core.domain.common;

import java.util.Objects;

public record JurisdictionCode(String value) {

    public JurisdictionCode {
        Objects.requireNonNull(value, "value");
        if (!value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Jurisdiction code must be an ISO alpha-3 code in uppercase");
        }
    }
}
