package com.angazo.arume.es;

import java.util.Collection;
import java.util.List;

import com.angazo.arume.core.module.FiscalCapability;
import com.angazo.arume.core.module.FiscalModule;
import com.angazo.arume.core.module.FiscalModuleDescriptor;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;
import com.angazo.arume.es.logic.legalform.LegalFormsFacade;

public final class SpainFiscalModule implements FiscalModule {

    private final FiscalModuleDescriptor descriptor = SpainModuleDescriptor.descriptor();
    private final InvoiceSeriesCapability invoiceSeriesCapability;
    private final SpainLegalFormsCapability legalFormsCapability;

    public SpainFiscalModule(
        InvoiceSeriesApplicationService invoiceSeriesService,
        LegalFormsFacade legalFormsFacade
    ) {
        invoiceSeriesCapability = new InvoiceSeriesCapability(invoiceSeriesService);
        legalFormsCapability = new SpainLegalFormsCapability(legalFormsFacade);
    }

    @Override
    public FiscalModuleDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public Collection<? extends FiscalCapability> capabilities() {
        return List.of(invoiceSeriesCapability, legalFormsCapability);
    }

    public record InvoiceSeriesCapability(InvoiceSeriesApplicationService service) implements FiscalCapability {

        public InvoiceSeriesCapability {
            if (service == null) {
                throw new NullPointerException("service");
            }
        }

        @Override
        public String capabilityId() {
            return "invoice-series";
        }
    }
}
