package com.angazo.arume.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.angazo.arume.core.application.catalog.LegalFormCatalogService;
import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;

@SpringBootTest(
    classes = ArumeApp.class,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:legal_forms_persistence;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LegalFormsPersistenceIntegrationTest {

    private static final JurisdictionCode SPAIN = new JurisdictionCode("ES");
    private static final JurisdictionCode UNITED_KINGDOM = new JurisdictionCode("GB");
    private static final JurisdictionCode UNITED_STATES = new JurisdictionCode("US");

    @Autowired
    private LegalFormCatalogService legalFormCatalogService;

    @Test
    void spainSeedIsAvailableThroughTheCoreCatalog() {
        var forms = legalFormCatalogService.list(SPAIN);

        assertEquals(17, forms.size());
        assertTrue(contains(forms, "SL", "Sociedad Limitada", true));
        assertTrue(contains(forms, "SColl", "Sociedad Colectiva", true));
        assertTrue(contains(forms, "EI", "Empresario individual", false));
        assertTrue(contains(forms, "PA", "Profesional autónomo", false));
        assertTrue(contains(forms, "ERL", "Emprendedor de Responsabilidad Limitada", false));
    }

    @Test
    void unitedKingdomSeedIsAvailableThroughTheCoreCatalog() {
        var forms = legalFormCatalogService.list(UNITED_KINGDOM);

        assertEquals(7, forms.size());
        assertTrue(contains(forms, "ST", "Sole Trader", false));
        assertTrue(contains(forms, "PS", "Partnership", true));
        assertTrue(contains(forms, "LLP", "Limited Liability Partnership", true));
        assertTrue(contains(forms, "Ltd", "Private Limited Company", true));
        assertTrue(contains(forms, "PLC", "Public Limited Company", true));
        assertTrue(contains(forms, "CLG", "Company Limited by Guarantee", true));
        assertTrue(contains(forms, "CIC", "Community Interest Company", true));
    }

    @Test
    void onlySoleTraderIsNotAnOrganizationInTheUnitedKingdom() {
        var individualForms = legalFormCatalogService.list(UNITED_KINGDOM).stream()
            .filter(form -> !form.organization())
            .map(LegalFormItem::code)
            .toList();

        assertEquals(List.of("ST"), individualForms);
    }

    @Test
    void charityIsNotAUnitedKingdomLegalForm() {
        assertFalse(legalFormCatalogService.list(UNITED_KINGDOM).stream()
            .anyMatch(form -> form.description().toLowerCase().contains("charity")));
    }

    @Test
    void legalFormsAreSortedByDescription() {
        for (var jurisdiction : List.of(SPAIN, UNITED_KINGDOM)) {
            var forms = legalFormCatalogService.list(jurisdiction);

            assertEquals(
                forms.stream().sorted(Comparator.comparing(LegalFormItem::description)).toList(),
                forms
            );
        }
    }

    @Test
    void countriesWithoutANationalModuleHaveNoLegalFormsSeed() {
        assertTrue(legalFormCatalogService.list(UNITED_STATES).isEmpty());
        assertFalse(legalFormCatalogService.hasCatalog(UNITED_STATES));
    }

    private static boolean contains(List<LegalFormItem> forms, String code, String description, boolean organization) {
        return forms.stream().anyMatch(form -> form.code().equals(code)
            && form.description().equals(description)
            && form.organization() == organization);
    }
}
