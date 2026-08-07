package com.angazo.arume.es.logic.invoice.series;

import java.util.Objects;

import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;

public record ConfigureInvoiceSeriesFiscalYearCommand(
    InvoiceSeriesId seriesId,
    FiscalYearId fiscalYearId,
    NumberingMode numberingMode,
    boolean active,
    long lastAssignedNumber
) {

    public ConfigureInvoiceSeriesFiscalYearCommand {
        Objects.requireNonNull(seriesId, "seriesId");
        Objects.requireNonNull(fiscalYearId, "fiscalYearId");
        Objects.requireNonNull(numberingMode, "numberingMode");
        if (lastAssignedNumber < 0) {
            throw new IllegalArgumentException("lastAssignedNumber must not be negative");
        }
    }
}
