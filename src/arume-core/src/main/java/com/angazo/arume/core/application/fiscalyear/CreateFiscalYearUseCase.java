package com.angazo.arume.core.application.fiscalyear;

import com.angazo.arume.core.domain.fiscalyear.FiscalYear;

public interface CreateFiscalYearUseCase {

    FiscalYear create(CreateFiscalYearCommand command);
}
