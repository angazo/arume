package com.angazo.arume.core.port.catalog;

import java.util.List;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;

public interface LegalFormFacade {

    List<LegalFormItem> findByJurisdiction(JurisdictionCode jurisdiction);
}
