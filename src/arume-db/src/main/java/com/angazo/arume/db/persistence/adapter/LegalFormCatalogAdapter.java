package com.angazo.arume.db.persistence.adapter;

import java.util.List;

import com.angazo.arume.core.port.catalog.LegalFormFacade;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.SubjectType;

@Repository
public class LegalFormCatalogAdapter implements LegalFormFacade {

    private final com.angazo.arume.db.persistence.mapper.LegalFormCatalogRepository repository;

    public LegalFormCatalogAdapter(com.angazo.arume.db.persistence.mapper.LegalFormCatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalFormItem> findByJurisdictionAndSubjectType(
        JurisdictionCode jurisdiction,
        SubjectType subjectType
    ) {
        return repository.selectByCountryAndSubjectType(jurisdiction.value(), subjectType.isLegalPerson()).stream()
            .map(row -> new LegalFormItem(row.getCode(), row.getDescription()))
            .toList();
    }
}
