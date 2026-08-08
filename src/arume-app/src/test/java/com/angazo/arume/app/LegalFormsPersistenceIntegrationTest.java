package com.angazo.arume.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.angazo.arume.core.application.catalog.LegalFormCatalogService;
import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.SubjectType;

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

    @Autowired
    private LegalFormCatalogService legalFormCatalogService;

    @Test
    void spainLegalPersonSeedIsAvailableThroughTheCoreCatalog() {
        var forms = legalFormCatalogService.list(SPAIN, SubjectType.LEGAL_PERSON);

        assertEquals(14, forms.size());
        assertTrue(contains(forms, "SL", "Sociedad Limitada"));
        assertTrue(contains(forms, "SA", "Sociedad Anónima"));
        assertTrue(contains(forms, "SColl", "Sociedad Colectiva"));
        assertTrue(contains(forms, "SC", "Sociedad Civil"));
    }

    @Test
    void spainNaturalPersonSeedIsAvailableThroughTheCoreCatalog() {
        var forms = legalFormCatalogService.list(SPAIN, SubjectType.NATURAL_PERSON);

        assertEquals(3, forms.size());
        assertTrue(contains(forms, "EI", "Empresario individual"));
        assertTrue(contains(forms, "PA", "Profesional autónomo"));
        assertTrue(contains(forms, "ERL", "Emprendedor de Responsabilidad Limitada"));
    }

    @Test
    void legalFormsAreSortedByDescription() {
        var forms = legalFormCatalogService.list(SPAIN, SubjectType.LEGAL_PERSON);

        assertEquals(
            forms.stream().sorted(Comparator.comparing(LegalFormItem::description)).toList(),
            forms
        );
    }

    @Test
    void otherCountriesHaveNoLegalFormsSeed() {
        assertTrue(legalFormCatalogService.list(UNITED_KINGDOM, SubjectType.LEGAL_PERSON).isEmpty());
        assertTrue(!legalFormCatalogService.hasCatalog(UNITED_KINGDOM, SubjectType.NATURAL_PERSON));
    }

    private static boolean contains(java.util.List<LegalFormItem> forms, String code, String description) {
        return forms.stream().anyMatch(form -> form.code().equals(code) && form.description().equals(description));
    }
}
