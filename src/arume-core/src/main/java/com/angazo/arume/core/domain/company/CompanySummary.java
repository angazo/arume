package com.angazo.arume.core.domain.company;

import com.angazo.arume.core.domain.common.JurisdictionCode;

public record CompanySummary(
    CompanyId id,
    String legalName,
    String primaryFiscalIdentification,
    JurisdictionCode fiscalResidence
) {
}
