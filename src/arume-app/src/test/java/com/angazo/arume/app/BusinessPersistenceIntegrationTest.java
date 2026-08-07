package com.angazo.arume.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.SQLException;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.application.company.CreateCompanyCommand;
import com.angazo.arume.core.application.fiscalyear.CreateFiscalYearCommand;
import com.angazo.arume.core.application.fiscalyear.FiscalYearApplicationService;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.es.logic.invoice.series.ConfigureInvoiceSeriesFiscalYearCommand;
import com.angazo.arume.es.logic.invoice.series.CreateInvoiceSeriesCommand;
import com.angazo.arume.es.logic.invoice.series.InvoiceSeriesApplicationService;
import com.angazo.arume.es.logic.invoice.series.NumberingMode;

@SpringBootTest(
    classes = ArumeApp.class,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:business_persistence;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BusinessPersistenceIntegrationTest {

    @Autowired
    private CompanyApplicationService companyService;

    @Autowired
    private FiscalYearApplicationService fiscalYearService;

    @Autowired
    private InvoiceSeriesApplicationService seriesService;

    @Autowired
    private DataSource dataSource;

    @Test
    void persistsCoreAndSpainBusinessData() throws SQLException {
        var spain = new JurisdictionCode("ESP");
        var company = companyService.create(new CreateCompanyCommand(
            SubjectType.LEGAL_PERSON,
            new FiscalIdentification(spain, "CIF-INTEGRATION-1"),
            new LegalFormCode(spain, "SL"),
            new CompanyProfile("Integration Company", spain, "Madrid", LocalDate.of(2024, 1, 1), null)
        ));
        var fiscalYear = fiscalYearService.create(new CreateFiscalYearCommand(
            company.id(),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            "2024"
        ));
        var series = seriesService.create(new CreateInvoiceSeriesCommand(
            company.id(), "ALU", "Main series", true
        ));
        seriesService.configureFiscalYear(new ConfigureInvoiceSeriesFiscalYearCommand(
            series.id(), fiscalYear.id(), NumberingMode.RESET_EACH_FISCAL_YEAR, true, 0
        ));
        var persistedSeries = seriesService.findByCompanyId(company.id()).getFirst();

        assertEquals(1, countRows("t4_companies"));
        assertEquals(1, countRows("t5_company_profiles"));
        assertEquals(1, countRows("t7_fiscal_years"));
        assertEquals(1, countRows("es1_invoice_series"));
        assertEquals(1, countRows("es2_invoice_series_fiscal_year"));
        assertEquals(NumberingMode.RESET_EACH_FISCAL_YEAR, persistedSeries.stateFor(fiscalYear.id()).numberingMode());
    }

    @Test
    void databaseAssignsDistinctCompanyIdsConcurrently() throws Exception {
        var executor = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var first = executor.submit(() -> createCompany("CIF-CONCURRENT-1"));
            var second = executor.submit(() -> createCompany("CIF-CONCURRENT-2"));

            var firstId = first.get().id().value();
            var secondId = second.get().id().value();

            assertNotEquals(firstId, secondId);
            assertEquals(2, countRows("t4_companies"));
        } finally {
            executor.shutdownNow();
        }
    }

    private com.angazo.arume.core.domain.company.Company createCompany(String fiscalId) {
        var spain = new JurisdictionCode("ESP");
        return companyService.create(new CreateCompanyCommand(
            SubjectType.LEGAL_PERSON,
            new FiscalIdentification(spain, fiscalId),
            new LegalFormCode(spain, "SL"),
            new CompanyProfile("Concurrent Company", spain, "Madrid", LocalDate.of(2024, 1, 1), null)
        ));
    }

    private int countRows(String table) throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            result.next();
            return result.getInt(1);
        }
    }
}
