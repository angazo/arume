package com.angazo.arume.es.invoice.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;
import com.angazo.arume.es.logic.invoice.series.*;
import org.junit.jupiter.api.Test;

import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearStatus;
import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.core.port.fiscalyear.FiscalYearFacade;

class InvoiceSeriesApplicationServiceTest {

    private static final JurisdictionCode SPAIN = new JurisdictionCode("ES");
    private static final CompanyId COMPANY_ID = new CompanyId(1);
    private static final FiscalYearId FISCAL_YEAR_ID = new FiscalYearId(1);

    @Test
    void createsSeriesForExistingCompany() {
        var repository = new InMemoryInvoiceSeriesRepository();
        var service = service(repository);

        var series = service.create(new CreateInvoiceSeriesCommand(COMPANY_ID, "ALU", "Main series", true));

        assertEquals("ALU", series.code());
    }

    @Test
    void rejectsDuplicateCodeForSameCompany() {
        var repository = new InMemoryInvoiceSeriesRepository();
        var service = service(repository);
        service.create(new CreateInvoiceSeriesCommand(COMPANY_ID, "ALU", "Main series", true));

        assertThrows(IllegalArgumentException.class, () -> service.create(
            new CreateInvoiceSeriesCommand(COMPANY_ID, "ALU", "Duplicate", true)
        ));
    }

    @Test
    void storesResetStatePerFiscalYear() {
        var repository = new InMemoryInvoiceSeriesRepository();
        var fiscalYears = fiscalYearRepositoryFor(FISCAL_YEAR_ID, COMPANY_ID);
        var service = new InvoiceSeriesApplicationService(repository, new InMemoryCompanyFacade(), fiscalYears);
        var series = service.create(new CreateInvoiceSeriesCommand(COMPANY_ID, "ALU", "Main series", true));

        var configured = service.configureFiscalYear(new ConfigureInvoiceSeriesFiscalYearCommand(
            series.id(), FISCAL_YEAR_ID, NumberingMode.RESET_EACH_FISCAL_YEAR, true, 0
        ));

        assertEquals(NumberingMode.RESET_EACH_FISCAL_YEAR, configured.stateFor(FISCAL_YEAR_ID).numberingMode());
        assertEquals(0, configured.stateFor(FISCAL_YEAR_ID).lastAssignedNumber());
    }

    @Test
    void rejectsFiscalYearFromAnotherCompany() {
        var repository = new InMemoryInvoiceSeriesRepository();
        var fiscalYears = new InMemoryFiscalYearFacade();
        fiscalYears.fiscalYear = new FiscalYear(
            FISCAL_YEAR_ID,
            new CompanyId(2),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            FiscalYearStatus.OPEN,
            "2024"
        );
        var service = new InvoiceSeriesApplicationService(repository, new InMemoryCompanyFacade(), fiscalYears);
        var series = service.create(new CreateInvoiceSeriesCommand(COMPANY_ID, "ALU", "Main series", true));

        assertThrows(IllegalArgumentException.class, () -> service.configureFiscalYear(
            new ConfigureInvoiceSeriesFiscalYearCommand(series.id(), FISCAL_YEAR_ID, NumberingMode.CONTINUE, true, 10)
        ));
    }

    private static InvoiceSeriesApplicationService service(InMemoryInvoiceSeriesRepository repository) {
        return new InvoiceSeriesApplicationService(repository, new InMemoryCompanyFacade(), new InMemoryFiscalYearFacade());
    }

    private static InMemoryFiscalYearFacade fiscalYearRepositoryFor(FiscalYearId id, CompanyId companyId) {
        var repository = new InMemoryFiscalYearFacade();
        repository.fiscalYear = new FiscalYear(
            id,
            companyId,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            FiscalYearStatus.OPEN,
            "2024"
        );
        return repository;
    }

    private static final class InMemoryInvoiceSeriesRepository implements InvoiceSeriesFacade {
        private final List<InvoiceSeries> series = new ArrayList<>();

        @Override
        public InvoiceSeries save(InvoiceSeries value) {
            if (!value.id().isAssigned()) {
                value = value.withId(new InvoiceSeriesId(series.size() + 1L));
            }
            var id = value.id();
            series.removeIf(existing -> existing.id().equals(id));
            series.add(value);
            return value;
        }

        @Override
        public Optional<InvoiceSeries> findById(InvoiceSeriesId id) {
            return series.stream().filter(value -> value.id().equals(id)).findFirst();
        }

        @Override
        public List<InvoiceSeries> findByCompanyId(CompanyId companyId) {
            return series.stream().filter(value -> value.companyId().equals(companyId)).toList();
        }

        @Override
        public boolean existsByCompanyAndCode(CompanyId companyId, String code) {
            return series.stream().anyMatch(value -> value.companyId().equals(companyId) && value.code().equals(code));
        }
    }

    private static final class InMemoryCompanyFacade implements CompanyFacade {
        private final Company company = Company.create(
            COMPANY_ID,
            SubjectType.LEGAL_PERSON,
            new FiscalIdentification(SPAIN, "CIF-1"),
            new LegalFormCode(SPAIN, "SL"),
            new CompanyProfile("Company", SPAIN, "Address", LocalDate.of(2024, 1, 1), null)
        );

        @Override
        public Company save(Company value) {
            return value;
        }

        @Override
        public Optional<Company> findById(CompanyId id) {
            return company.id().equals(id) ? Optional.of(company) : Optional.empty();
        }

        @Override
        public List<Company> findAll() {
            return List.of(company);
        }

        @Override
        public boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification) {
            return company.primaryFiscalIdentification().equals(identification);
        }
    }

    private static final class InMemoryFiscalYearFacade implements FiscalYearFacade {
        private FiscalYear fiscalYear;

        @Override
        public FiscalYear save(FiscalYear value) {
            fiscalYear = value;
            return value;
        }

        @Override
        public Optional<FiscalYear> findById(FiscalYearId id) {
            return fiscalYear != null && fiscalYear.id().equals(id) ? Optional.of(fiscalYear) : Optional.empty();
        }

        @Override
        public List<FiscalYear> findByCompanyId(CompanyId companyId) {
            return fiscalYear != null && fiscalYear.companyId().equals(companyId) ? List.of(fiscalYear) : List.of();
        }

        @Override
        public boolean existsOverlapping(CompanyId companyId, LocalDate startDate, LocalDate endDate) {
            return false;
        }
    }
}
