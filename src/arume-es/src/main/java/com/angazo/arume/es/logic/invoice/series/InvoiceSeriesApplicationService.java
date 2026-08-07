package com.angazo.arume.es.logic.invoice.series;

import java.util.List;
import java.util.Objects;
import com.angazo.arume.core.port.company.CompanyRepository;
import com.angazo.arume.core.port.fiscalyear.FiscalYearRepository;
import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;

public final class InvoiceSeriesApplicationService {

    private final InvoiceSeriesFacade repository;
    private final CompanyRepository companyRepository;
    private final FiscalYearRepository fiscalYearRepository;

    public InvoiceSeriesApplicationService(
        InvoiceSeriesFacade repository,
        CompanyRepository companyRepository,
        FiscalYearRepository fiscalYearRepository
    ) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.companyRepository = Objects.requireNonNull(companyRepository, "companyRepository");
        this.fiscalYearRepository = Objects.requireNonNull(fiscalYearRepository, "fiscalYearRepository");
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
        var fiscalYear = fiscalYearRepository.findById(command.fiscalYearId())
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
        if (companyRepository.findById(companyId).isEmpty()) {
            throw new IllegalArgumentException("Company not found: " + companyId);
        }
    }
}
