package com.angazo.arume.core.domain.company;

import java.time.LocalDate;
import java.util.Objects;

import com.angazo.arume.core.domain.common.JurisdictionCode;

public record LocalTaxRegistration(
    JurisdictionCode jurisdiction,
    String value,
    LocalDate validFrom,
    LocalDate validTo
) {

    public LocalTaxRegistration {
        Objects.requireNonNull(jurisdiction, "jurisdiction");
        value = requireText(value, "value");
        Objects.requireNonNull(validFrom, "validFrom");
        if (validTo != null && validTo.isBefore(validFrom)) {
            throw new IllegalArgumentException("validTo must not precede validFrom");
        }
    }

    public boolean overlaps(LocalTaxRegistration other) {
        return jurisdiction.equals(other.jurisdiction)
            && value.equals(other.value)
            && !startsAfter(validTo, other.validFrom)
            && !startsAfter(other.validTo, validFrom);
    }

    private static boolean startsAfter(LocalDate end, LocalDate start) {
        return end != null && end.isBefore(start);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
