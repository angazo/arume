package com.angazo.arume.core.domain.company;

import java.time.LocalDate;
import java.util.Objects;

import com.angazo.arume.core.domain.common.JurisdictionCode;

public record CompanyProfile(
    String legalName,
    JurisdictionCode fiscalResidence,
    String domicile,
    LocalDate validFrom,
    LocalDate validTo
) {

    public CompanyProfile {
        legalName = requireText(legalName, "legalName");
        Objects.requireNonNull(fiscalResidence, "fiscalResidence");
        domicile = requireText(domicile, "domicile");
        Objects.requireNonNull(validFrom, "validFrom");
        if (validTo != null && validTo.isBefore(validFrom)) {
            throw new IllegalArgumentException("validTo must not precede validFrom");
        }
    }

    public CompanyProfile closeOn(LocalDate date) {
        Objects.requireNonNull(date, "date");
        if (date.isBefore(validFrom)) {
            throw new IllegalArgumentException("Profile cannot close before it starts");
        }
        return new CompanyProfile(legalName, fiscalResidence, domicile, validFrom, date);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
