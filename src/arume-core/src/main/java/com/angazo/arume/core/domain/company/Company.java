package com.angazo.arume.core.domain.company;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Company {

    private final CompanyId id;
    private final SubjectType subjectType;
    private final FiscalIdentification primaryFiscalIdentification;
    private final LegalFormCode legalForm;
    private final List<CompanyProfile> profiles;
    private final List<LocalTaxRegistration> localTaxRegistrations;

    private Company(
        CompanyId id,
        SubjectType subjectType,
        FiscalIdentification primaryFiscalIdentification,
        LegalFormCode legalForm,
        List<CompanyProfile> profiles,
        List<LocalTaxRegistration> localTaxRegistrations
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.subjectType = Objects.requireNonNull(subjectType, "subjectType");
        this.primaryFiscalIdentification = Objects.requireNonNull(primaryFiscalIdentification, "primaryFiscalIdentification");
        this.legalForm = Objects.requireNonNull(legalForm, "legalForm");
        this.profiles = List.copyOf(profiles);
        this.localTaxRegistrations = List.copyOf(localTaxRegistrations);
        if (this.profiles.isEmpty()) {
            throw new IllegalArgumentException("A company must have at least one profile");
        }
    }

    public static Company create(
        CompanyId id,
        SubjectType subjectType,
        FiscalIdentification primaryFiscalIdentification,
        LegalFormCode legalForm,
        CompanyProfile initialProfile
    ) {
        Objects.requireNonNull(initialProfile, "initialProfile");
        if (initialProfile.validTo() != null) {
            throw new IllegalArgumentException("The initial company profile must be current");
        }
        return new Company(id, subjectType, primaryFiscalIdentification, legalForm, List.of(initialProfile), List.of());
    }

    public static Company restore(
        CompanyId id,
        SubjectType subjectType,
        FiscalIdentification primaryFiscalIdentification,
        LegalFormCode legalForm,
        List<CompanyProfile> profiles,
        List<LocalTaxRegistration> localTaxRegistrations
    ) {
        Objects.requireNonNull(profiles, "profiles");
        Objects.requireNonNull(localTaxRegistrations, "localTaxRegistrations");
        if (profiles.isEmpty() || profiles.getLast().validTo() != null) {
            throw new IllegalArgumentException("Restored company must have a current profile");
        }
        return new Company(id, subjectType, primaryFiscalIdentification, legalForm, profiles, localTaxRegistrations);
    }

    public Company withId(CompanyId assignedId) {
        Objects.requireNonNull(assignedId, "assignedId");
        if (!assignedId.isAssigned()) {
            throw new IllegalArgumentException("assignedId must be assigned");
        }
        if (id.isAssigned()) {
            throw new IllegalStateException("Company already has an assigned id");
        }
        return new Company(assignedId, subjectType, primaryFiscalIdentification, legalForm, profiles, localTaxRegistrations);
    }

    public Company changeProfile(CompanyProfile newProfile) {
        Objects.requireNonNull(newProfile, "newProfile");
        var current = currentProfile();
        if (!newProfile.validFrom().isAfter(current.validFrom())) {
            throw new IllegalArgumentException("A new company profile must start after the current profile");
        }

        var updatedProfiles = new ArrayList<>(profiles);
        updatedProfiles.set(updatedProfiles.size() - 1, current.closeOn(newProfile.validFrom().minusDays(1)));
        updatedProfiles.add(newProfile);
        return new Company(id, subjectType, primaryFiscalIdentification, legalForm, updatedProfiles, localTaxRegistrations);
    }

    public Company registerLocalTaxRegistration(LocalTaxRegistration registration) {
        Objects.requireNonNull(registration, "registration");
        if (localTaxRegistrations.stream().anyMatch(registration::overlaps)) {
            throw new IllegalArgumentException("The local tax registration is already active for this period");
        }
        var updatedRegistrations = new ArrayList<>(localTaxRegistrations);
        updatedRegistrations.add(registration);
        return new Company(id, subjectType, primaryFiscalIdentification, legalForm, profiles, updatedRegistrations);
    }

    public CompanyProfile profileAt(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return profiles.stream()
            .filter(profile -> !profile.validFrom().isAfter(date))
            .filter(profile -> profile.validTo() == null || !profile.validTo().isBefore(date))
            .reduce((first, second) -> second)
            .orElseThrow(() -> new IllegalArgumentException("No company profile is valid for " + date));
    }

    public CompanyProfile currentProfile() {
        return profiles.getLast();
    }

    public CompanySummary summary() {
        return new CompanySummary(
            id,
            subjectType,
            currentProfile().legalName(),
            primaryFiscalIdentification.value(),
            currentProfile().fiscalResidence()
        );
    }

    public CompanyId id() {
        return id;
    }

    public SubjectType subjectType() {
        return subjectType;
    }

    public FiscalIdentification primaryFiscalIdentification() {
        return primaryFiscalIdentification;
    }

    public LegalFormCode legalForm() {
        return legalForm;
    }

    public List<CompanyProfile> profiles() {
        return profiles;
    }

    public List<LocalTaxRegistration> localTaxRegistrations() {
        return localTaxRegistrations;
    }
}
