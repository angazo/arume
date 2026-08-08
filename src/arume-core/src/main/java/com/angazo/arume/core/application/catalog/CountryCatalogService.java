package com.angazo.arume.core.application.catalog;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.angazo.arume.core.domain.catalog.CountryCatalogEntry;
import com.angazo.arume.core.port.catalog.CountryFacade;

public final class CountryCatalogService {

    public static final String FALLBACK_LANGUAGE = "en";

    private final CountryFacade repository;

    public CountryCatalogService(CountryFacade repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<CountryCatalogEntry> list(String languageCode) {
        return repository.findAll(normalize(languageCode));
    }

    public List<CountryCatalogEntry> listSupportedJurisdictions(String languageCode) {
        return repository.findSupportedJurisdictions(normalize(languageCode));
    }

    private static String normalize(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return FALLBACK_LANGUAGE;
        }
        return languageCode.trim().toLowerCase(Locale.ROOT);
    }
}
