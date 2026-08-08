package com.angazo.arume.ui.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.port.catalog.CountryFacade;
import com.angazo.arume.core.port.catalog.LegalFormFacade;
import com.angazo.arume.core.port.company.CompanyFacade;
import com.angazo.arume.ui.controller.CompaniesController;
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
    void jurisdictionComboListsCountriesWithLocalizedNames(FxRobot robot) {
        var combo = robot.lookup("#companies-jurisdiction-combo").queryComboBox();

        assertEquals(
            List.of("Spain", "United Kingdom"),
            combo.getItems().stream().map(item -> ((CountryCatalogEntry) item).name()).toList()
        );
        assertEquals("Spain", ((CountryCatalogEntry) combo.getValue()).name());
    }

    @Test
    void jurisdictionComboFollowsLanguageChange(FxRobot robot) {
        robot.interact(() -> I18nManager.setLanguage("es"));

        var combo = robot.lookup("#companies-jurisdiction-combo").queryComboBox();
        assertEquals(
            List.of("España", "Reino Unido"),
            combo.getItems().stream().map(item -> ((CountryCatalogEntry) item).name()).toList()
        );
        assertEquals("ES", ((CountryCatalogEntry) combo.getValue()).code().value());
    }

    @Test
    void legalFormComboListsSpanishLegalPersonFormsByDefault(FxRobot robot) {
        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();

        assertEquals(14, combo.getItems().size());
        assertTrue(combo.getItems().contains("SL — Sociedad Limitada"));
        assertTrue(combo.getItems().contains("SA — Sociedad Anónima"));
        assertEquals("AIE — Agrupación de Interés Económico", combo.getValue());
    }

    @Test
    void legalFormComboFiltersBySubjectType(FxRobot robot) {
        robot.clickOn("#companies-subject-type-combo");
        robot.clickOn("Natural person");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(3, combo.getItems().size());
        assertTrue(combo.getItems().contains("EI — Empresario individual"));
        assertEquals("ERL — Emprendedor de Responsabilidad Limitada", combo.getValue());
    }

    @Test
    void legalFormComboDisablesForJurisdictionWithoutCatalog(FxRobot robot) {
        robot.clickOn("#companies-jurisdiction-combo");
        robot.clickOn("United Kingdom");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(true, combo.isDisable());
        assertEquals(0, combo.getItems().size());
    }

    private static final class InMemoryCountry implements CountryFacade {

        private static final Map<String, Map<String, String>> NAMES = Map.of(
            "en", Map.of("ES", "Spain", "GB", "United Kingdom"),
            "es", Map.of("ES", "España", "GB", "Reino Unido")
        );

        @Override
        public List<CountryCatalogEntry> findAll(String languageCode) {
            var names = NAMES.getOrDefault(languageCode, NAMES.get(CountryCatalogService.FALLBACK_LANGUAGE));
            return names.entrySet().stream()
                .map(entry -> new CountryCatalogEntry(new JurisdictionCode(entry.getKey()), entry.getValue()))
                .sorted(Comparator.comparing(CountryCatalogEntry::name))
                .toList();
        }
    }

    private static final class InMemoryLegalFormCatalog implements LegalFormFacade {

        @Override
        public List<LegalFormItem> findByJurisdictionAndSubjectType(
            JurisdictionCode jurisdiction,
            SubjectType subjectType
        ) {
            if (!jurisdiction.value().equals("ES")) {
                return List.of();
            }
            var items = subjectType == SubjectType.NATURAL_PERSON
                ? List.of(
                    new LegalFormItem("EI", "Empresario individual"),
                    new LegalFormItem("PA", "Profesional autónomo"),
                    new LegalFormItem("ERL", "Emprendedor de Responsabilidad Limitada")
                )
                : List.of(
                    new LegalFormItem("SA", "Sociedad Anónima"),
                    new LegalFormItem("SL", "Sociedad Limitada"),
                    new LegalFormItem("SLU", "Sociedad Limitada Unipersonal"),
                    new LegalFormItem("SAU", "Sociedad Anónima Unipersonal"),
                    new LegalFormItem("SColl", "Sociedad Colectiva"),
                    new LegalFormItem("SCom", "Sociedad Comanditaria Simple"),
                    new LegalFormItem("SComA", "Sociedad Comanditaria por Acciones"),
                    new LegalFormItem("SCoop", "Sociedad Cooperativa"),
                    new LegalFormItem("SLL", "Sociedad Limitada Laboral"),
                    new LegalFormItem("SAL", "Sociedad Anónima Laboral"),
                    new LegalFormItem("SC", "Sociedad Civil"),
                    new LegalFormItem("CB", "Comunidad de Bienes"),
                    new LegalFormItem("AIE", "Agrupación de Interés Económico"),
                    new LegalFormItem("SAT", "Sociedad Agraria de Transformación")
                );
            return items.stream().sorted(Comparator.comparing(LegalFormItem::description)).toList();
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
