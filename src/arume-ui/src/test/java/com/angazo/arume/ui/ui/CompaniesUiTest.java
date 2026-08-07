package com.angazo.arume.ui.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
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
import com.angazo.arume.core.port.company.CompanyRepository;
import com.angazo.arume.ui.controller.CompaniesController;
import com.angazo.arume.ui.i18n.I18nManager;

@ExtendWith(ApplicationExtension.class)
class CompaniesUiTest {

    private final InMemoryCompanyRepository repository = new InMemoryCompanyRepository();
    private CompaniesController controller;

    @Start
    private void start(Stage stage) throws IOException {
        I18nManager.init("en");
        var service = new CompanyApplicationService(repository);
        var loader = new FXMLLoader(getClass().getResource("/fxml/companies.fxml"));
        loader.setControllerFactory(type -> type == CompaniesController.class
            ? new CompaniesController(service)
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
