package com.angazo.arume.es;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.core.module.FiscalModuleRegistry;
import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.core.port.fiscalyear.FiscalYearFacade;
import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeries;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesId;

class SpainFiscalModuleTest {

    @Test
    void declaresSpainWithAnAlpha2Jurisdiction() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService());

        assertEquals("ES", module.descriptor().jurisdictionCode());
    }

    @Test
    void resolvesInvoiceSeriesCapabilityForSpain() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService());
        var registry = new FiscalModuleRegistry(List.of(module));

        var capability = registry.resolve("ES", "invoice-series", SpainFiscalModule.InvoiceSeriesCapability.class);

        assertTrue(capability.isPresent());
        assertEquals("invoice-series", capability.get().capabilityId());
    }

    @Test
    void doesNotExposeALegalFormsCapability() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService());

        assertTrue(module.capabilities().stream()
            .noneMatch(capability -> capability.capabilityId().equals("legal-forms")));
    }

    @Test
    void returnsEmptyForAnotherJurisdiction() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService());
        var registry = new FiscalModuleRegistry(List.of(module));

        var capability = registry.resolve("PT", "invoice-series", SpainFiscalModule.InvoiceSeriesCapability.class);

        assertTrue(capability.isEmpty());
    }

    private static InvoiceSeriesApplicationService stubInvoiceSeriesService() {
        return new InvoiceSeriesApplicationService(
            new InvoiceSeriesFacade() {
                @Override
                public InvoiceSeries save(InvoiceSeries series) {
                    return series;
                }

                @Override
                public Optional<InvoiceSeries> findById(InvoiceSeriesId id) {
                    return Optional.empty();
                }

                @Override
                public List<InvoiceSeries> findByCompanyId(CompanyId companyId) {
                    return List.of();
                }

                @Override
                public boolean existsByCompanyAndCode(CompanyId companyId, String code) {
                    return false;
                }
            },
            new CompanyFacade() {
                @Override
                public Company save(Company company) {
                    return company;
                }

                @Override
                public Optional<Company> findById(CompanyId id) {
                    return Optional.empty();
                }

                @Override
                public List<Company> findAll() {
                    return List.of();
                }

                @Override
                public boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification) {
                    return false;
                }
            },
            new FiscalYearFacade() {
                @Override
                public FiscalYear save(FiscalYear fiscalYear) {
                    return fiscalYear;
                }

                @Override
                public Optional<FiscalYear> findById(FiscalYearId id) {
                    return Optional.empty();
                }

                @Override
                public List<FiscalYear> findByCompanyId(CompanyId companyId) {
                    return List.of();
                }

                @Override
                public boolean existsOverlapping(CompanyId companyId, LocalDate startDate, LocalDate endDate) {
                    return false;
                }
            }
        );
    }
}
