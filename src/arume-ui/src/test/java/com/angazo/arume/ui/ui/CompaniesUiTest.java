package com.angazo.arume.ui.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.domain.company.Company;
import com.angazo.arume.core.domain.company.CompanyId;
import com.angazo.arume.core.domain.company.CompanySummary;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.core.module.FiscalCapability;
import com.angazo.arume.core.module.FiscalModule;
import com.angazo.arume.core.module.FiscalModuleDescriptor;
import com.angazo.arume.core.module.FiscalModuleRegistry;
import com.angazo.arume.core.module.LegalFormsCapability;
import com.angazo.arume.core.port.company.CompanyRepository;
import com.angazo.arume.ui.controller.CompaniesController;
import com.angazo.arume.ui.i18n.I18nManager;

@ExtendWith(ApplicationExtension.class)
class CompaniesUiTest {

    private final InMemoryCompanyRepository repository = new InMemoryCompanyRepository();
    private final FiscalModuleRegistry registry = legalFormsRegistry();
    private CompaniesController controller;

    @Start
    private void start(Stage stage) throws IOException {
        I18nManager.init("en");
        var service = new CompanyApplicationService(repository);
        var loader = new FXMLLoader(getClass().getResource("/fxml/companies.fxml"));
        loader.setControllerFactory(type -> type == CompaniesController.class
            ? new CompaniesController(service, registry)
            : null);
        Parent root = loader.load();
        controller = loader.getController();

        var scene = new Scene(root, 700, 600);
        scene.getStylesheets().add(getClass().getResource("/css/arume.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
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
        robot.clickOn("#companies-jurisdiction-field");
        robot.eraseText(3);
        robot.write("PRT");

        var combo = robot.lookup("#companies-legal-form-combo").queryComboBox();
        assertEquals(true, combo.isDisable());
        assertEquals(0, combo.getItems().size());
    }

    private static FiscalModuleRegistry legalFormsRegistry() {
        var module = new FiscalModule() {
            @Override
            public FiscalModuleDescriptor descriptor() {
                return new FiscalModuleDescriptor("test-es", "ESP", "0.1.0", "0.1.0");
            }

            @Override
            public Collection<? extends FiscalCapability> capabilities() {
                return List.of(new LegalFormsCapability() {
                    @Override
                    public String capabilityId() {
                        return "legal-forms";
                    }

                    @Override
                    public List<LegalFormItem> getLegalForms(SubjectType subjectType) {
                        return subjectType == SubjectType.NATURAL_PERSON
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
                    }
                });
            }
        };
        return new FiscalModuleRegistry(List.of(module));
    }

    private static final class InMemoryCompanyRepository implements CompanyRepository {
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
