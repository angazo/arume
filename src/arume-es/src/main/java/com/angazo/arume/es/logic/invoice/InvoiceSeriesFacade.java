package com.angazo.arume.es.logic.invoice;

import java.util.List;
import java.util.Optional;

import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeries;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesId;

public interface InvoiceSeriesFacade {

    InvoiceSeries save(InvoiceSeries series);

    Optional<InvoiceSeries> findById(InvoiceSeriesId id);

    List<InvoiceSeries> findByCompanyId(CompanyId companyId);

    boolean existsByCompanyAndCode(CompanyId companyId, String code);
}
