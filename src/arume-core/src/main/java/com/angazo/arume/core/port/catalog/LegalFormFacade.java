package com.angazo.arume.core.port.catalog;

import java.util.List;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.SubjectType;

public interface LegalFormFacade {

    List<LegalFormItem> findByJurisdictionAndSubjectType(JurisdictionCode jurisdiction, SubjectType subjectType);
}
