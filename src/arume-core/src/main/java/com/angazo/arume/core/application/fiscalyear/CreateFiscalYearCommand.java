package com.angazo.arume.core.application.fiscalyear;

import java.time.LocalDate;
import java.util.Objects;

import com.angazo.arume.core.domain.company.CompanyId;

public record CreateFiscalYearCommand(
    CompanyId companyId,
    LocalDate startDate,
    LocalDate endDate,
    String label
) {

    public CreateFiscalYearCommand {
        Objects.requireNonNull(companyId, "companyId");
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
        Objects.requireNonNull(label, "label");
    }
}
