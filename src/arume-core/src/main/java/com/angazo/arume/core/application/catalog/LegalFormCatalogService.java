package com.angazo.arume.core.application.catalog;

import java.util.List;
import java.util.Objects;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.port.catalog.LegalFormFacade;

public final class LegalFormCatalogService {

    private final LegalFormFacade repository;

    public LegalFormCatalogService(LegalFormFacade repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<LegalFormItem> list(JurisdictionCode jurisdiction) {
        Objects.requireNonNull(jurisdiction, "jurisdiction");
        return repository.findByJurisdiction(jurisdiction);
    }

    public boolean hasCatalog(JurisdictionCode jurisdiction) {
        return !list(jurisdiction).isEmpty();
    }
}
