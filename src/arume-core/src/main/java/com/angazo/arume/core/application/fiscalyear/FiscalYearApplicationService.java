package com.angazo.arume.core.application.fiscalyear;

import java.util.Objects;

import com.angazo.arume.core.port.company.CompanyRepository;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearStatus;
import com.angazo.arume.core.port.fiscalyear.FiscalYearRepository;

public final class FiscalYearApplicationService implements CreateFiscalYearUseCase {

    private final FiscalYearRepository repository;
    private final CompanyRepository companyRepository;

    public FiscalYearApplicationService(
        FiscalYearRepository repository,
        CompanyRepository companyRepository
    ) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.companyRepository = Objects.requireNonNull(companyRepository, "companyRepository");
    }

    @Override
    public FiscalYear create(CreateFiscalYearCommand command) {
        Objects.requireNonNull(command, "command");
        if (companyRepository.findById(command.companyId()).isEmpty()) {
            throw new IllegalArgumentException("Company not found: " + command.companyId());
        }
        if (repository.existsOverlapping(command.companyId(), command.startDate(), command.endDate())) {
            throw new IllegalArgumentException("The fiscal year overlaps an existing period");
        }
        return repository.save(new FiscalYear(
            FiscalYearId.unassigned(),
            command.companyId(),
            command.startDate(),
            command.endDate(),
            FiscalYearStatus.OPEN,
            command.label()
        ));
    }
}
