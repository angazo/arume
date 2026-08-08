package com.angazo.arume.core.application.company;

import java.util.Objects;

import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;

public record CreateCompanyCommand(
    FiscalIdentification primaryFiscalIdentification,
    LegalFormCode legalForm,
    CompanyProfile initialProfile
) {

    public CreateCompanyCommand {
        Objects.requireNonNull(primaryFiscalIdentification, "primaryFiscalIdentification");
        Objects.requireNonNull(legalForm, "legalForm");
        Objects.requireNonNull(initialProfile, "initialProfile");
    }
}
