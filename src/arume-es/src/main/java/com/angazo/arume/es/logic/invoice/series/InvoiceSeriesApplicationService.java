package com.angazo.arume.es.logic.invoice.series;

import java.util.List;
import java.util.Objects;
import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.core.port.fiscalyear.FiscalYearFacade;
import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;

public final class InvoiceSeriesApplicationService {

    private final InvoiceSeriesFacade repository;
    private final CompanyFacade companyFacade;
    private final FiscalYearFacade fiscalYearFacade;

    public InvoiceSeriesApplicationService(
        InvoiceSeriesFacade repository,
        CompanyFacade companyFacade,
        FiscalYearFacade fiscalYearFacade
    ) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.companyFacade = Objects.requireNonNull(companyFacade, "companyRepository");
        this.fiscalYearFacade = Objects.requireNonNull(fiscalYearFacade, "fiscalYearRepository");
    }

    public InvoiceSeries create(CreateInvoiceSeriesCommand command) {
        Objects.requireNonNull(command, "command");
        ensureCompanyExists(command.companyId());
        if (repository.existsByCompanyAndCode(command.companyId(), command.code())) {
            throw new IllegalArgumentException("An invoice series with this code already exists for the company");
        }
        return repository.save(InvoiceSeries.create(
            InvoiceSeriesId.unassigned(),
            command.companyId(),
            command.code(),
            command.description(),
            command.active()
        ));
    }

    public InvoiceSeries configureFiscalYear(ConfigureInvoiceSeriesFiscalYearCommand command) {
        Objects.requireNonNull(command, "command");
        var series = repository.findById(command.seriesId())
            .orElseThrow(() -> new IllegalArgumentException("Invoice series not found: " + command.seriesId()));
        var fiscalYear = fiscalYearFacade.findById(command.fiscalYearId())
            .orElseThrow(() -> new IllegalArgumentException("Fiscal year not found: " + command.fiscalYearId()));
        if (!series.companyId().equals(fiscalYear.companyId())) {
            throw new IllegalArgumentException("The fiscal year belongs to another company");
        }
        return repository.save(series.configureFiscalYear(new InvoiceSeriesFiscalYearState(
            command.fiscalYearId(),
            command.numberingMode(),
            command.active(),
            command.lastAssignedNumber()
        )));
    }

    public List<InvoiceSeries> findByCompanyId(com.angazo.arume.core.domain.company.CompanyId companyId) {
        ensureCompanyExists(companyId);
        return List.copyOf(repository.findByCompanyId(companyId));
    }

    private void ensureCompanyExists(com.angazo.arume.core.domain.company.CompanyId companyId) {
        if (companyFacade.findById(companyId).isEmpty()) {
            throw new IllegalArgumentException("Company not found: " + companyId);
        }
    }
}
