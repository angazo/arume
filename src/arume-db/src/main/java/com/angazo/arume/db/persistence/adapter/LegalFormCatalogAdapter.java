package com.angazo.arume.db.persistence.adapter;

import java.util.List;

import com.angazo.arume.core.port.catalog.LegalFormFacade;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;

@Repository
public class LegalFormCatalogAdapter implements LegalFormFacade {

    private final com.angazo.arume.db.persistence.mapper.LegalFormCatalogRepository repository;

    public LegalFormCatalogAdapter(com.angazo.arume.db.persistence.mapper.LegalFormCatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalFormItem> findByJurisdiction(JurisdictionCode jurisdiction) {
        return repository.selectByCountry(jurisdiction.value()).stream()
            .map(row -> new LegalFormItem(
                row.getCode(),
                row.getDescription(),
                Boolean.TRUE.equals(row.getIsOrganization())
            ))
            .toList();
    }
}
