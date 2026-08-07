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
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.module.FiscalModuleRegistry;
import com.angazo.arume.core.module.LegalFormsCapability;
import com.angazo.arume.core.port.company.CompanyRepository;
import com.angazo.arume.core.port.fiscalyear.FiscalYearRepository;
import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeries;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesId;
import com.angazo.arume.es.logic.legalform.LegalFormsFacade;

class SpainFiscalModuleTest {

    @Test
    void resolvesLegalFormsCapabilityForSpain() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService(), stubLegalFormsFacade());
        var registry = new FiscalModuleRegistry(List.of(module));

        var capability = registry.resolve("ESP", "legal-forms", LegalFormsCapability.class);

        assertTrue(capability.isPresent());
        assertEquals("legal-forms", capability.get().capabilityId());
        assertEquals("SL", capability.get().getLegalForms(SubjectType.LEGAL_PERSON).getFirst().code());
    }

    @Test
    void filtersLegalFormsBySubjectType() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService(), stubLegalFormsFacade());
        var registry = new FiscalModuleRegistry(List.of(module));

        var capability = registry.resolve("ESP", "legal-forms", LegalFormsCapability.class);

        assertTrue(capability.isPresent());
        assertEquals("EI", capability.get().getLegalForms(SubjectType.NATURAL_PERSON).getFirst().code());
    }

    @Test
    void returnsEmptyForJurisdictionWithoutLegalFormsCapability() {
        var module = new SpainFiscalModule(stubInvoiceSeriesService(), stubLegalFormsFacade());
        var registry = new FiscalModuleRegistry(List.of(module));

        var capability = registry.resolve("PRT", "legal-forms", LegalFormsCapability.class);

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
            new CompanyRepository() {
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
            new FiscalYearRepository() {
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

    private static LegalFormsFacade stubLegalFormsFacade() {
        return (countryNumericCode, subjectType) -> subjectType == SubjectType.NATURAL_PERSON
            ? List.of(new LegalFormsCapability.LegalFormItem("EI", "Empresario individual"))
            : List.of(new LegalFormsCapability.LegalFormItem("SL", "Sociedad Limitada"));
    }
}
