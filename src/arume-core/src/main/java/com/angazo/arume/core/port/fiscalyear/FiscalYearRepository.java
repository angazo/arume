package com.angazo.arume.core.port.fiscalyear;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;

public interface FiscalYearRepository {

    FiscalYear save(FiscalYear fiscalYear);

    Optional<FiscalYear> findById(FiscalYearId id);

    List<FiscalYear> findByCompanyId(CompanyId companyId);

    boolean existsOverlapping(CompanyId companyId, LocalDate startDate, LocalDate endDate);
}
