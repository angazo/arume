package com.angazo.arume.core.domain.company;

public record CompanyId(long value) {

    public CompanyId {
        if (value < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
    }

    public static CompanyId unassigned() {
        return new CompanyId(0);
    }

    public boolean isAssigned() {
        return value > 0;
    }
}
