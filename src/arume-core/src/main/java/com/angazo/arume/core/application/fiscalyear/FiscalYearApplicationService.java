package com.angazo.arume.core.application.fiscalyear;

import java.util.Objects;

import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearStatus;
import com.angazo.arume.core.port.fiscalyear.FiscalYearFacade;

public final class FiscalYearApplicationService implements CreateFiscalYearUseCase {

    private final FiscalYearFacade repository;
    private final CompanyFacade companyFacade;

    public FiscalYearApplicationService(
        FiscalYearFacade repository,
        CompanyFacade companyFacade
    ) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.companyFacade = Objects.requireNonNull(companyFacade, "companyRepository");
    }

    @Override
    public FiscalYear create(CreateFiscalYearCommand command) {
        Objects.requireNonNull(command, "command");
        if (companyFacade.findById(command.companyId()).isEmpty()) {
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
