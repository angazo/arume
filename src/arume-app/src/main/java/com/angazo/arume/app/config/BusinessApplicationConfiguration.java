package com.angazo.arume.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.application.fiscalyear.FiscalYearApplicationService;
import com.angazo.arume.core.module.FiscalModule;
import com.angazo.arume.core.module.FiscalModuleRegistry;
import com.angazo.arume.core.port.company.CompanyRepository;
import com.angazo.arume.core.port.fiscalyear.FiscalYearRepository;
import com.angazo.arume.es.SpainFiscalModule;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;
import com.angazo.arume.es.logic.invoice.InvoiceSeriesFacade;

@Configuration
public class BusinessApplicationConfiguration {

    @Bean
    public CompanyApplicationService companyApplicationService(CompanyRepository repository) {
        return new CompanyApplicationService(repository);
    }

    @Bean
    public FiscalYearApplicationService fiscalYearApplicationService(
        FiscalYearRepository repository,
        CompanyRepository companyRepository
    ) {
        return new FiscalYearApplicationService(repository, companyRepository);
    }

    @Bean
    public InvoiceSeriesApplicationService invoiceSeriesApplicationService(
        InvoiceSeriesFacade repository,
        CompanyRepository companyRepository,
        FiscalYearRepository fiscalYearRepository
    ) {
        return new InvoiceSeriesApplicationService(repository, companyRepository, fiscalYearRepository);
    }

    @Bean
    public SpainFiscalModule spainFiscalModule(InvoiceSeriesApplicationService invoiceSeriesService) {
        return new SpainFiscalModule(invoiceSeriesService);
    }

    @Bean
    public FiscalModuleRegistry fiscalModuleRegistry(java.util.List<FiscalModule> modules) {
        return new FiscalModuleRegistry(modules);
    }
}
