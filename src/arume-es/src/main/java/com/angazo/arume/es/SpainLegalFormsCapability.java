package com.angazo.arume.es;

import java.util.List;

import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.module.LegalFormsCapability;
import com.angazo.arume.es.logic.legalform.LegalFormsFacade;

public final class SpainLegalFormsCapability implements LegalFormsCapability {

    private static final short ESP_COUNTRY_NUMERIC_CODE = 724;

    private final LegalFormsFacade legalFormsFacade;

    public SpainLegalFormsCapability(LegalFormsFacade legalFormsFacade) {
        if (legalFormsFacade == null) {
            throw new NullPointerException("legalFormsFacade");
        }
        this.legalFormsFacade = legalFormsFacade;
    }

    @Override
    public String capabilityId() {
        return "legal-forms";
    }

    @Override
    public List<LegalFormItem> getLegalForms(SubjectType subjectType) {
        return legalFormsFacade.listByCountryNumericCodeAndLegalPerson(ESP_COUNTRY_NUMERIC_CODE, subjectType);
    }
}
