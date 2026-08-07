package com.angazo.arume.core.module;

import java.util.List;

import com.angazo.arume.core.domain.company.SubjectType;

public interface LegalFormsCapability extends FiscalCapability {

    List<LegalFormItem> getLegalForms(SubjectType subjectType);

    record LegalFormItem(String code, String description) {
    }
}
