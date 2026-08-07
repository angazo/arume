package com.angazo.arume.core.domain.fiscalyear;

import java.time.LocalDate;
import java.util.Objects;

import com.angazo.arume.core.domain.company.CompanyId;

public record FiscalYear(
    FiscalYearId id,
    CompanyId companyId,
    LocalDate startDate,
    LocalDate endDate,
    FiscalYearStatus status,
    String label
) {

    public FiscalYear {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(companyId, "companyId");
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not precede startDate");
        }
        Objects.requireNonNull(status, "status");
        label = requireText(label, "label");
    }

    public FiscalYear close() {
        return new FiscalYear(id, companyId, startDate, endDate, FiscalYearStatus.CLOSED, label);
    }

    public FiscalYear withId(FiscalYearId assignedId) {
        Objects.requireNonNull(assignedId, "assignedId");
        if (!assignedId.isAssigned()) {
            throw new IllegalArgumentException("assignedId must be assigned");
        }
        if (id.isAssigned()) {
            throw new IllegalStateException("Fiscal year already has an assigned id");
        }
        return new FiscalYear(assignedId, companyId, startDate, endDate, status, label);
    }

    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        return !endDate.isBefore(otherStart) && !otherEnd.isBefore(startDate);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
