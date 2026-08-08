package com.angazo.arume.core.application.catalog;

import java.util.List;
import java.util.Objects;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.port.catalog.LegalFormFacade;

public final class LegalFormCatalogService {

    private final LegalFormFacade repository;

    public LegalFormCatalogService(LegalFormFacade repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<LegalFormItem> list(JurisdictionCode jurisdiction, SubjectType subjectType) {
        Objects.requireNonNull(jurisdiction, "jurisdiction");
        Objects.requireNonNull(subjectType, "subjectType");
        return repository.findByJurisdictionAndSubjectType(jurisdiction, subjectType);
    }

    public boolean hasCatalog(JurisdictionCode jurisdiction, SubjectType subjectType) {
        return !list(jurisdiction, subjectType).isEmpty();
    }
}
