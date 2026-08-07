package com.angazo.arume.db.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.company.LocalTaxRegistration;
import com.angazo.arume.db.persistence.mapper.CompanyProfileRepository;
import com.angazo.arume.db.persistence.mapper.CompanyTaxRegistrationRepository;
import com.angazo.arume.db.persistence.mapper.CompanyRepository;
import com.angazo.arume.db.persistence.model.T4Companies;
import com.angazo.arume.db.persistence.model.T5CompanyProfiles;
import com.angazo.arume.db.persistence.model.T6CompanyTaxRegistrations;

@Repository
public class CompanyAdapter implements com.angazo.arume.core.port.company.CompanyRepository {

    private final CompanyRepository companyRepository;
    private final CompanyProfileRepository profileRepository;
    private final CompanyTaxRegistrationRepository registrationRepository;

    public CompanyAdapter(
        CompanyRepository companyRepository,
        CompanyProfileRepository profileRepository,
        CompanyTaxRegistrationRepository registrationRepository
    ) {
        this.companyRepository = companyRepository;
        this.profileRepository = profileRepository;
        this.registrationRepository = registrationRepository;
    }

    @Override
    @Transactional
    public Company save(Company company) {
        var id = company.id().isAssigned() ? company.id().value() : null;
        var row = toCompanyRow(company);
        var persisted = company;
        if (!company.id().isAssigned()) {
            companyRepository.insertSelective(row);
            persisted = company.withId(new CompanyId(row.getId()));
        } else if (companyRepository.selectByPrimaryKey(id) == null) {
            companyRepository.insertSelective(row);
        } else {
            companyRepository.updateByPrimaryKey(row);
            companyRepository.deleteProfiles(id);
            companyRepository.deleteTaxRegistrations(id);
        }

        for (var profile : persisted.profiles()) {
            profileRepository.insertSelective(toProfileRow(persisted, profile));
        }
        for (var registration : persisted.localTaxRegistrations()) {
            registrationRepository.insertSelective(toRegistrationRow(persisted, registration));
        }
        return persisted;
    }

    @Override
    public Optional<Company> findById(CompanyId id) {
        var row = companyRepository.selectByPrimaryKey(id.value());
        return row == null ? Optional.empty() : Optional.of(toDomain(row));
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.selectAllCompanies().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification) {
        return companyRepository.selectByFiscalIdentification(
            identification.jurisdiction().value(),
            identification.value()
        ) != null;
    }

    private Company toDomain(T4Companies row) {
        var companyId = new CompanyId(row.getId());
        var profiles = companyRepository.selectProfiles(row.getId()).stream().map(this::toProfile).toList();
        var registrations = companyRepository.selectTaxRegistrations(row.getId()).stream().map(this::toRegistration).toList();
        return Company.restore(
            companyId,
            new FiscalIdentification(
                new JurisdictionCode(row.getPrimaryFiscalJurisdiction()),
                row.getPrimaryFiscalId()
            ),
            new LegalFormCode(
                new JurisdictionCode(row.getLegalFormJurisdiction()),
                row.getLegalFormCode()
            ),
            profiles,
            registrations
        );
    }

    private T4Companies toCompanyRow(Company company) {
        return T4Companies.builder()
            .id(company.id().isAssigned() ? company.id().value() : null)
            .primaryFiscalJurisdiction(company.primaryFiscalIdentification().jurisdiction().value())
            .primaryFiscalId(company.primaryFiscalIdentification().value())
            .legalFormJurisdiction(company.legalForm().jurisdiction().value())
            .legalFormCode(company.legalForm().value())
            .createdAt(LocalDateTime.now())
            .build();
    }

    private T5CompanyProfiles toProfileRow(Company company, CompanyProfile profile) {
        return T5CompanyProfiles.builder()
            .companyId(company.id().value())
            .legalName(profile.legalName())
            .fiscalResidence(profile.fiscalResidence().value())
            .domicile(profile.domicile())
            .validFrom(profile.validFrom())
            .validTo(profile.validTo())
            .build();
    }

    private T6CompanyTaxRegistrations toRegistrationRow(Company company, LocalTaxRegistration registration) {
        return T6CompanyTaxRegistrations.builder()
            .companyId(company.id().value())
            .jurisdiction(registration.jurisdiction().value())
            .taxId(registration.value())
            .validFrom(registration.validFrom())
            .validTo(registration.validTo())
            .build();
    }

    private CompanyProfile toProfile(T5CompanyProfiles row) {
        return new CompanyProfile(
            row.getLegalName(),
            new JurisdictionCode(row.getFiscalResidence()),
            row.getDomicile(),
            row.getValidFrom(),
            row.getValidTo()
        );
    }

    private LocalTaxRegistration toRegistration(T6CompanyTaxRegistrations row) {
        return new LocalTaxRegistration(
            new JurisdictionCode(row.getJurisdiction()),
            row.getTaxId(),
            row.getValidFrom(),
            row.getValidTo()
        );
    }
}
