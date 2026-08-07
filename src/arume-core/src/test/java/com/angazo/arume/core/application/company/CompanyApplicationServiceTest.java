package com.angazo.arume.core.application.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.company.LocalTaxRegistration;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.port.company.CompanyRepository;

class CompanyApplicationServiceTest {

    private static final JurisdictionCode SPAIN = new JurisdictionCode("ESP");
    private static final CompanyId COMPANY_ID = new CompanyId(1);

    @Test
    void createsCompanyWithProtectedIdentity() {
        var repository = new InMemoryCompanyRepository();
        var service = new CompanyApplicationService(repository);

        var company = service.create(command("A Company", "CIF-1"));

        assertEquals(COMPANY_ID, company.id());
        assertEquals("CIF-1", company.primaryFiscalIdentification().value());
        assertEquals("SL", company.legalForm().value());
    }

    @Test
    void rejectsDuplicateFiscalIdentification() {
        var repository = new InMemoryCompanyRepository();
        var service = new CompanyApplicationService(repository);
        service.create(command("A Company", "CIF-1"));

        assertThrows(IllegalArgumentException.class, () -> service.create(command("Another Company", "CIF-1")));
    }

    @Test
    void preservesPreviousProfileWhenCompanyChangesDomicile() {
        var repository = new InMemoryCompanyRepository();
        var service = new CompanyApplicationService(repository);
        service.create(command("A Company", "CIF-1"));

        var updated = service.changeProfile(COMPANY_ID, profile("A Company", "Madrid", LocalDate.of(2025, 1, 1)));

        assertEquals("Madrid", updated.currentProfile().domicile());
        assertEquals("Old address", updated.profileAt(LocalDate.of(2024, 12, 31)).domicile());
    }

    @Test
    void preservesLocalTaxRegistrationsWithoutChangingPrimaryIdentity() {
        var repository = new InMemoryCompanyRepository();
        var service = new CompanyApplicationService(repository);
        service.create(command("A Company", "CIF-1"));

        var updated = service.registerLocalTaxRegistration(
            COMPANY_ID,
            new LocalTaxRegistration(new JurisdictionCode("PRT"), "PT-1", LocalDate.of(2025, 1, 1), null)
        );

        assertEquals("CIF-1", updated.primaryFiscalIdentification().value());
        assertEquals(1, updated.localTaxRegistrations().size());
    }

    @Test
    void listsCompanySummaries() {
        var repository = new InMemoryCompanyRepository();
        var service = new CompanyApplicationService(repository);
        service.create(command("A Company", "CIF-1"));

        assertEquals("A Company", service.list().getFirst().legalName());
    }

    private static CreateCompanyCommand command(String name, String fiscalId) {
        return new CreateCompanyCommand(
            SubjectType.LEGAL_PERSON,
            new FiscalIdentification(SPAIN, fiscalId),
            new LegalFormCode(SPAIN, "SL"),
            profile(name, "Old address", LocalDate.of(2024, 1, 1))
        );
    }

    private static CompanyProfile profile(String name, String domicile, LocalDate validFrom) {
        return new CompanyProfile(name, SPAIN, domicile, validFrom, null);
    }

    private static final class InMemoryCompanyRepository implements CompanyRepository {
        private final List<Company> companies = new ArrayList<>();

        @Override
        public Company save(Company company) {
            if (!company.id().isAssigned()) {
                company = company.withId(new CompanyId(companies.size() + 1L));
            }
            var id = company.id();
            companies.removeIf(existing -> existing.id().equals(id));
            companies.add(company);
            return company;
        }

        @Override
        public Optional<Company> findById(CompanyId id) {
            return companies.stream().filter(company -> company.id().equals(id)).findFirst();
        }

        @Override
        public List<Company> findAll() {
            return List.copyOf(companies);
        }

        @Override
        public boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification) {
            return companies.stream().anyMatch(company -> company.primaryFiscalIdentification().equals(identification));
        }
    }
}
