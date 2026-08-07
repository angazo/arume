package com.angazo.arume.es.logic.invoice.series;

import java.util.Objects;

import com.angazo.arume.core.domain.company.CompanyId;

public record CreateInvoiceSeriesCommand(CompanyId companyId, String code, String description, boolean active) {

    public CreateInvoiceSeriesCommand {
        Objects.requireNonNull(companyId, "companyId");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(description, "description");
    }
}
