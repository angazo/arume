package com.angazo.arume.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.angazo.arume.core.application.catalog.CountryCatalogService;
import com.angazo.arume.core.application.catalog.LegalFormCatalogService;
import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.application.fiscalyear.FiscalYearApplicationService;
import com.angazo.arume.core.module.FiscalModule;
import com.angazo.arume.core.module.FiscalModuleRegistry;
import com.angazo.arume.core.port.catalog.CountryFacade;
import com.angazo.arume.core.port.catalog.LegalFormFacade;
import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.core.port.fiscalyear.FiscalYearFacade;

@Configuration
public class BusinessApplicationConfiguration {

    @Bean
    public CompanyApplicationService companyApplicationService(CompanyFacade repository) {
        return new CompanyApplicationService(repository);
    }

    @Bean
    public CountryCatalogService countryCatalogService(CountryFacade repository) {
        return new CountryCatalogService(repository);
    }

    @Bean
    public LegalFormCatalogService legalFormCatalogService(LegalFormFacade repository) {
        return new LegalFormCatalogService(repository);
    }

    @Bean
    public FiscalYearApplicationService fiscalYearApplicationService(
        FiscalYearFacade repository,
        CompanyFacade companyFacade
    ) {
        return new FiscalYearApplicationService(repository, companyFacade);
    }

    @Bean
    public FiscalModuleRegistry fiscalModuleRegistry(java.util.List<FiscalModule> modules) {
        return new FiscalModuleRegistry(modules);
    }
}
