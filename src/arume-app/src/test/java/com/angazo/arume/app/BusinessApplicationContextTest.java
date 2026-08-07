package com.angazo.arume.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.application.fiscalyear.FiscalYearApplicationService;
import com.angazo.arume.core.module.FiscalModuleRegistry;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;

@SpringBootTest(
    classes = ArumeApp.class,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:business_context;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
    }
)
class BusinessApplicationContextTest {

    @Autowired
    private CompanyApplicationService companyApplicationService;

    @Autowired
    private FiscalYearApplicationService fiscalYearApplicationService;

    @Autowired
    private InvoiceSeriesApplicationService invoiceSeriesApplicationService;

    @Autowired
    private FiscalModuleRegistry fiscalModuleRegistry;

    @Test
    void composesCoreAndSpainBusinessBeans() {
        assertNotNull(companyApplicationService);
        assertNotNull(fiscalYearApplicationService);
        assertNotNull(invoiceSeriesApplicationService);
        assertNotNull(fiscalModuleRegistry);
    }
}
