package com.angazo.arume.core.application.fiscalyear;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.angazo.arume.core.port.company.CompanyFacade;
import org.junit.jupiter.api.Test;

import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearStatus;
import com.angazo.arume.core.port.fiscalyear.FiscalYearFacade;

class FiscalYearApplicationServiceTest {

    private static final CompanyId COMPANY_ID = new CompanyId(1);

    @Test
    void createsShortFiscalYearAsOpen() {
        var repository = new InMemoryFiscalYearFacade();
        var companyRepository = new InMemoryCompanyFacade(COMPANY_ID);
        var service = service(repository, companyRepository);

        var fiscalYear = service.create(new CreateFiscalYearCommand(
            COMPANY_ID,
            LocalDate.of(2024, 7, 1),
            LocalDate.of(2024, 12, 31),
            "2024 corto"
        ));

        assertEquals(FiscalYearStatus.OPEN, fiscalYear.status());
        assertEquals("2024 corto", fiscalYear.label());
    }

    @Test
    void rejectsOverlappingFiscalYearForSameCompany() {
        var repository = new InMemoryFiscalYearFacade();
        var companyRepository = new InMemoryCompanyFacade(COMPANY_ID);
        var service = service(repository, companyRepository);
        service.create(command(COMPANY_ID, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));

        assertThrows(IllegalArgumentException.class, () -> service.create(
            command(COMPANY_ID, LocalDate.of(2024, 6, 1), LocalDate.of(2025, 5, 31))
        ));
    }

    @Test
    void allowsSamePeriodForDifferentCompanies() {
        var repository = new InMemoryFiscalYearFacade();
        var otherCompanyId = new CompanyId(2);
        var companyRepository = new InMemoryCompanyFacade(COMPANY_ID, otherCompanyId);
        var service = service(repository, companyRepository);

        service.create(command(COMPANY_ID, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
        var other = service.create(command(
            otherCompanyId,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31)
        ));

        assertEquals(FiscalYearStatus.OPEN, other.status());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void closesFiscalYearWithoutChangingItsPeriod() {
        var fiscalYear = new FiscalYear(
            new FiscalYearId(2),
            COMPANY_ID,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            FiscalYearStatus.OPEN,
            "2024"
        );

        var closed = fiscalYear.close();

        assertEquals(FiscalYearStatus.CLOSED, closed.status());
        assertEquals(fiscalYear.startDate(), closed.startDate());
        assertEquals(fiscalYear.endDate(), closed.endDate());
    }

    @Test
    void rejectsFiscalYearForUnknownCompany() {
        var repository = new InMemoryFiscalYearFacade();
        var service = service(repository, new InMemoryCompanyFacade());

        assertThrows(IllegalArgumentException.class, () -> service.create(
            command(COMPANY_ID, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))
        ));
    }

    private static FiscalYearApplicationService service(
        InMemoryFiscalYearFacade repository,
        InMemoryCompanyFacade companyRepository
    ) {
        return new FiscalYearApplicationService(repository, companyRepository);
    }

    private static CreateFiscalYearCommand command(CompanyId companyId, LocalDate start, LocalDate end) {
        return new CreateFiscalYearCommand(companyId, start, end, "2024");
    }

    private static final class InMemoryFiscalYearFacade implements FiscalYearFacade {
        private final List<FiscalYear> fiscalYears = new ArrayList<>();

        @Override
        public FiscalYear save(FiscalYear fiscalYear) {
            if (!fiscalYear.id().isAssigned()) {
                fiscalYear = fiscalYear.withId(new FiscalYearId(fiscalYears.size() + 1L));
            }
            fiscalYears.add(fiscalYear);
            return fiscalYear;
        }

        @Override
        public Optional<FiscalYear> findById(FiscalYearId id) {
            return fiscalYears.stream().filter(fiscalYear -> fiscalYear.id().equals(id)).findFirst();
        }

        @Override
        public List<FiscalYear> findByCompanyId(CompanyId companyId) {
            return fiscalYears.stream().filter(fiscalYear -> fiscalYear.companyId().equals(companyId)).toList();
        }

        @Override
        public boolean existsOverlapping(CompanyId companyId, LocalDate startDate, LocalDate endDate) {
            return fiscalYears.stream()
                .filter(fiscalYear -> fiscalYear.companyId().equals(companyId))
                .anyMatch(fiscalYear -> fiscalYear.overlaps(startDate, endDate));
        }

        private List<FiscalYear> findAll() {
            return List.copyOf(fiscalYears);
        }
    }

    private static final class InMemoryCompanyFacade implements CompanyFacade {
        private final List<Company> companies;

        private InMemoryCompanyFacade(CompanyId... ids) {
            companies = java.util.Arrays.stream(ids)
                .map(id -> Company.create(
                    id,
                    SubjectType.LEGAL_PERSON,
                    new FiscalIdentification(new JurisdictionCode("ES"), Long.toString(id.value())),
                    new LegalFormCode(new JurisdictionCode("ES"), "SL"),
                    new CompanyProfile("Company", new JurisdictionCode("ES"), "Address", LocalDate.of(2024, 1, 1), null)
                ))
                .toList();
        }

        @Override
        public Company save(Company company) {
            return company;
        }

        @Override
        public Optional<Company> findById(CompanyId id) {
            return companies.stream().filter(company -> company.id().equals(id)).findFirst();
        }

        @Override
        public List<Company> findAll() {
            return companies;
        }

        @Override
        public boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification) {
            return false;
        }
    }
}
