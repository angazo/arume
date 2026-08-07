package com.angazo.arume.es.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.module.LegalFormsCapability;
import com.angazo.arume.es.logic.legalform.LegalFormsFacade;
import com.angazo.arume.es.persistence.mapper.LegalFormsRepository;

@Repository
public class LegalFormsAdapter implements LegalFormsFacade {

    private final LegalFormsRepository repository;

    public LegalFormsAdapter(LegalFormsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalFormsCapability.LegalFormItem> listByCountryNumericCodeAndLegalPerson(
        short countryNumericCode,
        SubjectType subjectType
    ) {
        return repository.selectByCountryNumericCodeAndLegalPerson(countryNumericCode, subjectType.isLegalPerson()).stream()
            .map(row -> new LegalFormsCapability.LegalFormItem(row.getCode(), row.getDescription()))
            .toList();
    }
}
