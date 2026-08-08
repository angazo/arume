package com.angazo.arume.core.port.catalog;

import java.util.List;

import com.angazo.arume.core.domain.catalog.CountryCatalogEntry;

public interface CountryFacade {

    List<CountryCatalogEntry> findAll(String languageCode);

    List<CountryCatalogEntry> findSupportedJurisdictions(String languageCode);
}
