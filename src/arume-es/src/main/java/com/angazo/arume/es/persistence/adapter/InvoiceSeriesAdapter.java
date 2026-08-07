package com.angazo.arume.es.persistence.adapter;

import java.util.List;
import java.util.Optional;

import com.angazo.arume.es.persistence.mapper.InvoiceSeriesFiscalYearRepository;
import com.angazo.arume.es.persistence.mapper.InvoiceSeriesRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.fiscalyear.FiscalYearId;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeries;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesFiscalYearState;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesId;
import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;
import com.angazo.arume.es.persistence.model.Es1InvoiceSeries;
import com.angazo.arume.es.persistence.model.Es2InvoiceSeriesFiscalYear;

@Repository
public class InvoiceSeriesAdapter implements InvoiceSeriesFacade {

    private final InvoiceSeriesRepository seriesRepository;
    private final InvoiceSeriesFiscalYearRepository stateRepository;

    public InvoiceSeriesAdapter(
            InvoiceSeriesRepository seriesRepository,
            InvoiceSeriesFiscalYearRepository stateRepository
    ) {
        this.seriesRepository = seriesRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    @Transactional
    public InvoiceSeries save(InvoiceSeries series) {
        var row = toSeriesRow(series);
        var persisted = series;
        if (!series.id().isAssigned()) {
            seriesRepository.insertSelective(row);
            persisted = series.withId(new InvoiceSeriesId(row.getId()));
        } else {
            seriesRepository.updateByPrimaryKeySelective(row);
            stateRepository.deleteStates(series.id().value());
        }
        for (var state : persisted.fiscalYearStates()) {
            stateRepository.insertSelective(toStateRow(persisted, state));
        }
        return persisted;
    }

    @Override
    public Optional<InvoiceSeries> findById(InvoiceSeriesId id) {
        var row = seriesRepository.selectByPrimaryKey(id.value());
        return row == null ? Optional.empty() : Optional.of(toDomain(row));
    }

    @Override
    public List<InvoiceSeries> findByCompanyId(CompanyId companyId) {
        return seriesRepository.selectByCompanyId(companyId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByCompanyAndCode(CompanyId companyId, String code) {
        return seriesRepository.existsByCompanyAndCode(companyId.value(), code);
    }

    private InvoiceSeries toDomain(Es1InvoiceSeries row) {
        var states = stateRepository.selectStates(row.getId()).stream().map(state -> new InvoiceSeriesFiscalYearState(
            new FiscalYearId(state.getFiscalYearId()),
            state.getNumberingMode(),
            state.getActive(),
            state.getLastAssignedNumber()
        )).toList();
        return InvoiceSeries.restore(
            new InvoiceSeriesId(row.getId()),
            new CompanyId(row.getCompanyId()),
            row.getCode(),
            row.getDescription(),
            row.getActive(),
            states
        );
    }

    private Es1InvoiceSeries toSeriesRow(InvoiceSeries series) {
        var row = new Es1InvoiceSeries();
        row.setId(series.id().isAssigned() ? series.id().value() : null);
        row.setCompanyId(series.companyId().value());
        row.setCode(series.code());
        row.setDescription(series.description());
        row.setActive(series.active());
        return row;
    }

    private Es2InvoiceSeriesFiscalYear toStateRow(
        InvoiceSeries series,
        InvoiceSeriesFiscalYearState state
    ) {
        var row = new Es2InvoiceSeriesFiscalYear();
        row.setSeriesId(series.id().value());
        row.setFiscalYearId(state.fiscalYearId().value());
        row.setNumberingMode(state.numberingMode());
        row.setActive(state.active());
        row.setLastAssignedNumber(state.lastAssignedNumber());
        return row;
    }

}
