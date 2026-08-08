package com.angazo.arume.es;

import java.util.Collection;
import java.util.List;

import com.angazo.arume.core.module.FiscalCapability;
import com.angazo.arume.core.module.FiscalModule;
import com.angazo.arume.core.module.FiscalModuleDescriptor;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;
import org.springframework.stereotype.Component;

@Component
public final class SpainFiscalModule implements FiscalModule {

    private final FiscalModuleDescriptor descriptor = SpainModuleDescriptor.descriptor();
    private final InvoiceSeriesCapability invoiceSeriesCapability;

    public SpainFiscalModule(InvoiceSeriesApplicationService invoiceSeriesService) {
        invoiceSeriesCapability = new InvoiceSeriesCapability(invoiceSeriesService);
    }

    @Override
    public FiscalModuleDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public Collection<? extends FiscalCapability> capabilities() {
        return List.of(invoiceSeriesCapability);
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
