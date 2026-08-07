package com.angazo.arume.core.domain.company;

public enum SubjectType {

    NATURAL_PERSON(false),
    LEGAL_PERSON(true);

    private final boolean legalPerson;

    SubjectType(boolean legalPerson) {
        this.legalPerson = legalPerson;
    }

    public boolean isLegalPerson() {
        return legalPerson;
    }
}
