package com.angazo.arume.db.persistence.adapter;

import java.util.List;

import com.angazo.arume.core.port.catalog.CountryFacade;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.application.catalog.CountryCatalogService;
import com.angazo.arume.core.domain.catalog.CountryCatalogEntry;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.db.persistence.model.T2CountryNames;

@Repository
public class CountryAdapter implements CountryFacade {

    private final com.angazo.arume.db.persistence.mapper.CountryCatalogRepository repository;

    public CountryAdapter(com.angazo.arume.db.persistence.mapper.CountryCatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryCatalogEntry> findAll(String languageCode) {
        return toEntries(repository.selectCountriesByLanguage(languageCode, CountryCatalogService.FALLBACK_LANGUAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryCatalogEntry> findSupportedJurisdictions(String languageCode) {
        return toEntries(
            repository.selectSupportedJurisdictionsByLanguage(languageCode, CountryCatalogService.FALLBACK_LANGUAGE)
        );
    }

    private static List<CountryCatalogEntry> toEntries(List<T2CountryNames> rows) {
        return rows.stream()
            .map(row -> new CountryCatalogEntry(
                new JurisdictionCode(row.getCountryAlpha2Code()),
                row.getName()
            ))
            .toList();
    }
}
