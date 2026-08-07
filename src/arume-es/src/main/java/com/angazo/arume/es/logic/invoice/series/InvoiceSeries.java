package com.angazo.arume.es.logic.invoice.series;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;

public final class InvoiceSeries {

    private final InvoiceSeriesId id;
    private final CompanyId companyId;
    private final String code;
    private final String description;
    private final boolean active;
    private final List<InvoiceSeriesFiscalYearState> fiscalYearStates;

    private InvoiceSeries(
        InvoiceSeriesId id,
        CompanyId companyId,
        String code,
        String description,
        boolean active,
        List<InvoiceSeriesFiscalYearState> fiscalYearStates
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.companyId = Objects.requireNonNull(companyId, "companyId");
        this.code = requireText(code, "code");
        this.description = requireText(description, "description");
        this.active = active;
        this.fiscalYearStates = List.copyOf(fiscalYearStates);
    }

    public static InvoiceSeries create(
        InvoiceSeriesId id,
        CompanyId companyId,
        String code,
        String description,
        boolean active
    ) {
        return new InvoiceSeries(id, companyId, code, description, active, List.of());
    }

    public static InvoiceSeries restore(
        InvoiceSeriesId id,
        CompanyId companyId,
        String code,
        String description,
        boolean active,
        List<InvoiceSeriesFiscalYearState> fiscalYearStates
    ) {
        return new InvoiceSeries(id, companyId, code, description, active, fiscalYearStates);
    }

    public InvoiceSeries withId(InvoiceSeriesId assignedId) {
        Objects.requireNonNull(assignedId, "assignedId");
        if (!assignedId.isAssigned()) {
            throw new IllegalArgumentException("assignedId must be assigned");
        }
        if (id.isAssigned()) {
            throw new IllegalStateException("Invoice series already has an assigned id");
        }
        return new InvoiceSeries(assignedId, companyId, code, description, active, fiscalYearStates);
    }

    public InvoiceSeries configureFiscalYear(InvoiceSeriesFiscalYearState state) {
        Objects.requireNonNull(state, "state");
        var updated = new ArrayList<>(fiscalYearStates);
        var existing = updated.stream()
            .filter(current -> current.fiscalYearId().equals(state.fiscalYearId()))
            .findFirst();
        if (existing.isPresent()) {
            updated.set(updated.indexOf(existing.get()), state);
        } else {
            updated.add(state);
        }
        return new InvoiceSeries(id, companyId, code, description, active, updated);
    }

    public InvoiceSeriesFiscalYearState stateFor(FiscalYearId fiscalYearId) {
        return fiscalYearStates.stream()
            .filter(state -> state.fiscalYearId().equals(fiscalYearId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No series state exists for fiscal year " + fiscalYearId));
    }

    public InvoiceSeriesId id() {
        return id;
    }

    public CompanyId companyId() {
        return companyId;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

    public boolean active() {
        return active;
    }

    public List<InvoiceSeriesFiscalYearState> fiscalYearStates() {
        return fiscalYearStates;
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
