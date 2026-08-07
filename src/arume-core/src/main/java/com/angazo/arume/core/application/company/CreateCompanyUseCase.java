package com.angazo.arume.core.application.company;

import com.angazo.arume.core.domain.company.Company;

public interface CreateCompanyUseCase {

    Company create(CreateCompanyCommand command);
}
