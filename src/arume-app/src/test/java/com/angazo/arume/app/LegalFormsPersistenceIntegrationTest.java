package com.angazo.arume.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.es.logic.legalform.LegalFormsFacade;

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

    private static final short ESP = 724;
    private static final short GBR = 826;

    @Autowired
    private LegalFormsFacade legalFormsFacade;

    @Autowired
    private DataSource dataSource;

    @Test
    void spainLegalPersonSeedIsAvailableThroughRepository() throws SQLException {
        var forms = legalFormsFacade.listByCountryNumericCodeAndLegalPerson(ESP, SubjectType.LEGAL_PERSON);

        assertEquals(14, forms.size());
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("SL")
            && form.description().equals("Sociedad Limitada")));
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("SA")
            && form.description().equals("Sociedad Anónima")));
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("SColl")
            && form.description().equals("Sociedad Colectiva")));
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("SC")
            && form.description().equals("Sociedad Civil")));
    }

    @Test
    void spainNaturalPersonSeedIsAvailableThroughRepository() throws SQLException {
        var forms = legalFormsFacade.listByCountryNumericCodeAndLegalPerson(ESP, SubjectType.NATURAL_PERSON);

        assertEquals(3, forms.size());
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("EI")
            && form.description().equals("Empresario individual")));
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("PA")
            && form.description().equals("Profesional autónomo")));
        assertTrue(forms.stream().anyMatch(form -> form.code().equals("ERL")
            && form.description().equals("Emprendedor de Responsabilidad Limitada")));
    }

    @Test
    void otherCountriesHaveNoLegalFormsSeed() throws SQLException {
        var forms = legalFormsFacade.listByCountryNumericCodeAndLegalPerson(GBR, SubjectType.LEGAL_PERSON);

        assertTrue(forms.isEmpty());
    }
}
