package com.angazo.arume.core.domain.catalog;

import java.util.Objects;

public record LegalFormItem(String code, String description) {

    public LegalFormItem {
        code = requireText(code, "code");
        description = requireText(description, "description");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
