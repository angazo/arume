package com.angazo.arume.es.logic.legalform;

import java.util.List;

import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.module.LegalFormsCapability;

public interface LegalFormsFacade {

    List<LegalFormsCapability.LegalFormItem> listByCountryNumericCodeAndLegalPerson(
        short countryNumericCode,
        SubjectType subjectType
    );

}
