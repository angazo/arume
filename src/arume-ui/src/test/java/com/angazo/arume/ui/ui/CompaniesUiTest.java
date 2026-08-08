package com.angazo.arume.ui.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.angazo.arume.core.application.catalog.CountryCatalogService;
import com.angazo.arume.core.application.catalog.LegalFormCatalogService;
import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.domain.catalog.CountryCatalogEntry;
import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.CompanySummary;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.port.catalog.CountryFacade;
import com.angazo.arume.core.port.catalog.LegalFormFacade;
import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.ui.controller.CompaniesController;
import com.angazo.arume.ui.controller.LegalFormFamily;
import com.angazo.arume.ui.i18n.I18nManager;

@ExtendWith(ApplicationExtension.class)
class CompaniesUiTest {

    private final InMemoryCompanyFacade repository = new InMemoryCompanyFacade();

    @Start
    private void start(Stage stage) throws IOException {
        I18nManager.init("en");
        var service = new CompanyApplicationService(repository);
        var countryCatalog = new CountryCatalogService(new InMemoryCountry());
        var legalFormCatalog = new LegalFormCatalogService(new InMemoryLegalFormCatalog());
        var loader = new FXMLLoader(getClass().getResource("/fxml/companies.fxml"));
        loader.setControllerFactory(type -> type == CompaniesController.class
            ? new CompaniesController(service, countryCatalog, legalFormCatalog)
            : null);
        Parent root = loader.load();

        var scene = new Scene(root, 700, 600);
        scene.getStylesheets().add(getClass().getResource("/css/arume.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    void restoreLanguage(FxRobot robot) {
        robot.interact(() -> I18nManager.setLanguage("en"));
    }

    @Test
    void createsCompanyAndRefreshesList(FxRobot robot) {
        robot.clickOn("#companies-fiscal-id-field").write("CIF-1");
        robot.clickOn("#companies-legal-name-field").write("Arume SL");
        robot.clickOn("#companies-domicile-field").write("Madrid");
        robot.clickOn("#companies-create-button");

        assertEquals(1, repository.findAll().size());
        var item = (CompanySummary) robot.lookup("#companies-list").queryListView().getItems().getFirst();
        assertEquals("Arume SL", item.legalName());
        assertEquals("ES", repository.findAll().getFirst().primaryFiscalIdentification().jurisdiction().value());
    }

    @Test
    void jurisdictionComboOffersOnlySupportedJurisdictions(FxRobot robot) {
        var combo = robot.lookup("#companies-jurisdiction-combo").queryComboBox();

        assertEquals(
            List.of("Spain", "United Kingdom", "United States"),
            combo.getItems().stream().map(item -> ((CountryCatalogEntry) item).name()).toList()
        );
        assertEquals("Spain", ((CountryCatalogEntry) combo.getValue()).name());
        assertFalse(
            combo.getItems().stream().anyMatch(item -> ((CountryCatalogEntry) item).code().value().equals("CL")),
            "Chile has no national module and must not be offered as a jurisdiction"
        );
    }

    @Test
    void jurisdictionComboFollowsLanguageChange(FxRobot robot) {
        robot.interact(() -> I18nManager.setLanguage("es"));

        var combo = robot.lookup("#companies-jurisdiction-combo").queryComboBox();
        assertEquals(
            List.of("España", "Estados Unidos", "Reino Unido"),
            combo.getItems().stream().map(item -> ((CountryCatalogEntry) item).name()).toList()
        );
        assertEquals("ES", ((CountryCatalogEntry) combo.getValue()).code().value());
    }

    @Test
    void legalFormComboListsSpanishOrganizationFormsByDefault(FxRobot robot) {
        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();

        assertEquals(14, combo.getItems().size());
        assertTrue(combo.getItems().contains("SL — Sociedad Limitada"));
        assertTrue(combo.getItems().contains("SA — Sociedad Anónima"));
        assertEquals("AIE — Agrupación de Interés Económico", combo.getValue());
    }

    @Test
    void legalFormComboFiltersByFamily(FxRobot robot) {
        robot.clickOn("#companies-legal-form-family-combo");
        robot.clickOn("Individual");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(3, combo.getItems().size());
        assertTrue(combo.getItems().contains("EI — Empresario individual"));
        assertEquals("ERL — Emprendedor de Responsabilidad Limitada", combo.getValue());
    }

    @Test
    void familyFilterKeepsItsSelectionAcrossALanguageChange(FxRobot robot) {
        robot.clickOn("#companies-legal-form-family-combo");
        robot.clickOn("Individual");

        robot.interact(() -> I18nManager.setLanguage("es"));

        var familyCombo = robot.lookup("#companies-legal-form-family-combo").queryComboBox();
        assertEquals(LegalFormFamily.INDIVIDUAL, familyCombo.getValue());
        assertEquals("Persona a título individual", familyCombo.getConverter().toString(familyCombo.getValue()));

        var legalFormCombo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(3, legalFormCombo.getItems().size());
    }

    @Test
    void legalFormComboListsTheUnitedKingdomCatalog(FxRobot robot) {
        robot.clickOn("#companies-jurisdiction-combo");
        robot.clickOn("United Kingdom");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(6, combo.getItems().size());
        assertTrue(combo.getItems().contains("PS — Partnership"));
        assertTrue(combo.getItems().contains("Ltd — Private Limited Company"));
        assertEquals("CIC — Community Interest Company", combo.getValue());
    }

    @Test
    void unitedKingdomSoleTraderIsTheOnlyIndividualForm(FxRobot robot) {
        robot.clickOn("#companies-jurisdiction-combo");
        robot.clickOn("United Kingdom");
        robot.clickOn("#companies-legal-form-family-combo");
        robot.clickOn("Individual");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(List.of("ST — Sole Trader"), List.copyOf(combo.getItems()));
    }

    @Test
    void createsACompanyInASecondJurisdiction(FxRobot robot) {
        robot.clickOn("#companies-jurisdiction-combo");
        robot.clickOn("United Kingdom");
        robot.clickOn("#companies-fiscal-id-field").write("UTR-1");
        robot.clickOn("#companies-legal-name-field").write("Smith & Jones");
        robot.clickOn("#companies-domicile-field").write("London");
        robot.clickOn("#companies-create-button");

        assertEquals(1, repository.findAll().size());
        var company = repository.findAll().getFirst();
        assertEquals("GB", company.primaryFiscalIdentification().jurisdiction().value());
        assertEquals("CIC", company.legalForm().value());
    }

    @Test
    void legalFormComboDisablesForJurisdictionWithoutCatalog(FxRobot robot) {
        robot.clickOn("#companies-jurisdiction-combo");
        robot.clickOn("United States");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(true, combo.isDisable());
        assertEquals(0, combo.getItems().size());
    }

    /**
     * `US` is listed as a supported jurisdiction but has no legal forms, so that the safeguard of the
     * disabled legal form combo stays covered even though a well-seeded module always provides both.
     */
    private static final class InMemoryCountry implements CountryFacade {

        private static final Map<String, Map<String, String>> NAMES = Map.of(
            "en", Map.of("ES", "Spain", "GB", "United Kingdom", "US", "United States", "CL", "Chile"),
            "es", Map.of("ES", "España", "GB", "Reino Unido", "US", "Estados Unidos", "CL", "Chile")
        );

        private static final List<String> SUPPORTED = List.of("ES", "GB", "US");

        @Override
        public List<CountryCatalogEntry> findAll(String languageCode) {
            return entries(languageCode, NAMES.get(languageCodeOrFallback(languageCode)).keySet());
        }

        @Override
        public List<CountryCatalogEntry> findSupportedJurisdictions(String languageCode) {
            return entries(languageCode, SUPPORTED);
        }

        private static List<CountryCatalogEntry> entries(String languageCode, java.util.Collection<String> codes) {
            var names = NAMES.get(languageCodeOrFallback(languageCode));
            return codes.stream()
                .map(code -> new CountryCatalogEntry(new JurisdictionCode(code), names.get(code)))
                .sorted(Comparator.comparing(CountryCatalogEntry::name))
                .toList();
        }

        private static String languageCodeOrFallback(String languageCode) {
            return NAMES.containsKey(languageCode) ? languageCode : CountryCatalogService.FALLBACK_LANGUAGE;
        }
    }

    private static final class InMemoryLegalFormCatalog implements LegalFormFacade {

        private static final Map<String, List<LegalFormItem>> CATALOG = Map.of(
            "ES", List.of(
                new LegalFormItem("EI", "Empresario individual", false),
                new LegalFormItem("PA", "Profesional autónomo", false),
                new LegalFormItem("ERL", "Emprendedor de Responsabilidad Limitada", false),
                new LegalFormItem("SA", "Sociedad Anónima", true),
                new LegalFormItem("SL", "Sociedad Limitada", true),
                new LegalFormItem("SLU", "Sociedad Limitada Unipersonal", true),
                new LegalFormItem("SAU", "Sociedad Anónima Unipersonal", true),
                new LegalFormItem("SColl", "Sociedad Colectiva", true),
                new LegalFormItem("SCom", "Sociedad Comanditaria Simple", true),
                new LegalFormItem("SComA", "Sociedad Comanditaria por Acciones", true),
                new LegalFormItem("SCoop", "Sociedad Cooperativa", true),
                new LegalFormItem("SLL", "Sociedad Limitada Laboral", true),
                new LegalFormItem("SAL", "Sociedad Anónima Laboral", true),
                new LegalFormItem("SC", "Sociedad Civil", true),
                new LegalFormItem("CB", "Comunidad de Bienes", true),
                new LegalFormItem("AIE", "Agrupación de Interés Económico", true),
                new LegalFormItem("SAT", "Sociedad Agraria de Transformación", true)
            ),
            "GB", List.of(
                new LegalFormItem("ST", "Sole Trader", false),
                new LegalFormItem("PS", "Partnership", true),
                new LegalFormItem("LLP", "Limited Liability Partnership", true),
                new LegalFormItem("Ltd", "Private Limited Company", true),
                new LegalFormItem("PLC", "Public Limited Company", true),
                new LegalFormItem("CLG", "Company Limited by Guarantee", true),
                new LegalFormItem("CIC", "Community Interest Company", true)
            )
        );

        @Override
        public List<LegalFormItem> findByJurisdiction(JurisdictionCode jurisdiction) {
            return CATALOG.getOrDefault(jurisdiction.value(), List.of()).stream()
                .sorted(Comparator.comparing(LegalFormItem::description))
                .toList();
        }
    }

    private static final class InMemoryCompanyFacade implements CompanyFacade {
        private final List<Company> companies = new ArrayList<>();

        @Override
        public Company save(Company company) {
            if (!company.id().isAssigned()) {
                company = company.withId(new CompanyId(companies.size() + 1L));
            }
            var id = company.id();
            companies.removeIf(existing -> existing.id().equals(id));
            companies.add(company);
            return company;
        }

        @Override
        public Optional<Company> findById(CompanyId id) {
            return companies.stream().filter(company -> company.id().equals(id)).findFirst();
        }

        @Override
        public List<Company> findAll() {
            return List.copyOf(companies);
        }

        @Override
        public boolean existsByPrimaryFiscalIdentification(FiscalIdentification identification) {
            return companies.stream().anyMatch(company -> company.primaryFiscalIdentification().equals(identification));
        }
    }
}
