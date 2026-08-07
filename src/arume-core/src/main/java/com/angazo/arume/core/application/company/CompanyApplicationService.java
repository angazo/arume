package com.angazo.arume.core.application.company;

import java.util.List;
import java.util.Objects;

import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.CompanySummary;
import com.angazo.arume.core.domain.company.LocalTaxRegistration;
import com.angazo.arume.core.port.company.CompanyRepository;

public final class CompanyApplicationService implements CreateCompanyUseCase, ListCompaniesUseCase {

    private final CompanyRepository repository;

    public CompanyApplicationService(CompanyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public Company create(CreateCompanyCommand command) {
        Objects.requireNonNull(command, "command");
        if (repository.existsByPrimaryFiscalIdentification(command.primaryFiscalIdentification())) {
            throw new IllegalArgumentException("A company with this fiscal identification already exists");
        }
        var company = Company.create(
            CompanyId.unassigned(),
            command.primaryFiscalIdentification(),
            command.legalForm(),
            command.initialProfile()
        );
        return repository.save(company);
    }

    @Override
    public List<CompanySummary> list() {
        return List.copyOf(repository.findAll().stream().map(Company::summary).toList());
    }

    public Company changeProfile(CompanyId id, CompanyProfile profile) {
        return repository.save(find(id).changeProfile(profile));
    }

    public Company registerLocalTaxRegistration(CompanyId id, LocalTaxRegistration registration) {
        return repository.save(find(id).registerLocalTaxRegistration(registration));
    }

    private Company find(CompanyId id) {
        return repository.findById(Objects.requireNonNull(id, "id"))
            .orElseThrow(() -> new IllegalArgumentException("Company not found: " + id));
    }
}
