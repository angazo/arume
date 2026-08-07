package com.angazo.arume.db.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYear;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearStatus;
import com.angazo.arume.db.persistence.mapper.FiscalYearRepository;
import com.angazo.arume.db.persistence.model.T7FiscalYears;

@Repository
public class FiscalYearAdapter implements com.angazo.arume.core.port.fiscalyear.FiscalYearRepository {

    private final FiscalYearRepository fiscalYearRepository;

    public FiscalYearAdapter(FiscalYearRepository fiscalYearRepository) {
        this.fiscalYearRepository = fiscalYearRepository;
    }

    @Override
    @Transactional
    public FiscalYear save(FiscalYear fiscalYear) {
        var row = toRow(fiscalYear);
        if (!fiscalYear.id().isAssigned()) {
            fiscalYearRepository.insertSelective(row);
            return fiscalYear.withId(new FiscalYearId(row.getId()));
        } else if (fiscalYearRepository.selectByPrimaryKey(row.getId()) == null) {
            fiscalYearRepository.insertSelective(row);
        } else {
            fiscalYearRepository.updateByPrimaryKey(row);
        }
        return fiscalYear;
    }

    @Override
    public Optional<FiscalYear> findById(FiscalYearId id) {
        var row = fiscalYearRepository.selectByPrimaryKey(id.value());
        return row == null ? Optional.empty() : Optional.of(toDomain(row));
    }

    @Override
    public List<FiscalYear> findByCompanyId(CompanyId companyId) {
        return fiscalYearRepository.selectByCompanyId(companyId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsOverlapping(CompanyId companyId, LocalDate startDate, LocalDate endDate) {
        return fiscalYearRepository.existsOverlapping(companyId.value(), startDate, endDate);
    }

    private T7FiscalYears toRow(FiscalYear fiscalYear) {
        return T7FiscalYears.builder()
            .id(fiscalYear.id().isAssigned() ? fiscalYear.id().value() : null)
            .companyId(fiscalYear.companyId().value())
            .startDate(fiscalYear.startDate())
            .endDate(fiscalYear.endDate())
            .status(fiscalYear.status().name())
            .label(fiscalYear.label())
            .build();
    }

    private FiscalYear toDomain(T7FiscalYears row) {
        return new FiscalYear(
            new FiscalYearId(row.getId()),
            new CompanyId(row.getCompanyId()),
            row.getStartDate(),
            row.getEndDate(),
            FiscalYearStatus.valueOf(row.getStatus()),
            row.getLabel()
        );
    }
}
