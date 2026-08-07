package com.angazo.arume.core.domain.fiscalyear;

public record FiscalYearId(long value) {

    public FiscalYearId {
        if (value < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
    }

    public static FiscalYearId unassigned() {
        return new FiscalYearId(0);
    }

    public boolean isAssigned() {
        return value > 0;
    }
}
