package com.angazo.arume.ui.controller;

import java.time.LocalDate;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import org.springframework.stereotype.Component;

import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.application.company.CreateCompanyCommand;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.company.CompanySummary;
import com.angazo.arume.ui.i18n.I18nManager;

@Component
public class CompaniesController {

    @FXML private Label titleLabel;
    @FXML private Label jurisdictionLabel;
    @FXML private Label fiscalIdLabel;
    @FXML private Label legalFormLabel;
    @FXML private Label legalNameLabel;
    @FXML private Label domicileLabel;
    @FXML private TextField jurisdictionField;
    @FXML private TextField fiscalIdField;
    @FXML private TextField legalFormField;
    @FXML private TextField legalNameField;
    @FXML private TextField domicileField;
    @FXML private Button createButton;
    @FXML private Label feedbackLabel;
    @FXML private ListView<CompanySummary> companyList;

    private final CompanyApplicationService companyService;

    public CompaniesController(CompanyApplicationService companyService) {
        this.companyService = companyService;
    }

    @FXML
    public void initialize() {
        companyList.setItems(FXCollections.observableArrayList());
        companyList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(CompanySummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                    ? null
                    : item.legalName() + " (" + item.primaryFiscalIdentification() + ")");
            }
        });
        jurisdictionField.setText("ESP");
        legalFormField.setText("SL");
        I18nManager.onLanguageChange(this::refreshTexts);
        refreshTexts();
        refreshCompanies();
    }

    @FXML
    public void onCreate() {
        try {
            var jurisdiction = new JurisdictionCode(jurisdictionField.getText().trim().toUpperCase(Locale.ROOT));
            var company = companyService.create(new CreateCompanyCommand(
                new FiscalIdentification(jurisdiction, fiscalIdField.getText()),
                new LegalFormCode(jurisdiction, legalFormField.getText()),
                new CompanyProfile(
                    legalNameField.getText(),
                    jurisdiction,
                    domicileField.getText(),
                    LocalDate.now(),
                    null
                )
            ));
            feedbackLabel.setText(I18nManager.getString("companies.created") + " " + company.currentProfile().legalName());
            refreshCompanies();
            clearForm();
        } catch (RuntimeException exception) {
            feedbackLabel.setText(I18nManager.getString("companies.validation.invalid"));
        }
    }

    private void refreshCompanies() {
        companyList.getItems().setAll(companyService.list());
    }

    private void clearForm() {
        fiscalIdField.clear();
        legalNameField.clear();
        domicileField.clear();
    }

    private void refreshTexts() {
        titleLabel.setText(I18nManager.getString("companies.title"));
        jurisdictionLabel.setText(I18nManager.getString("companies.jurisdiction"));
        fiscalIdLabel.setText(I18nManager.getString("companies.fiscalId"));
        legalFormLabel.setText(I18nManager.getString("companies.legalForm"));
        legalNameLabel.setText(I18nManager.getString("companies.legalName"));
        domicileLabel.setText(I18nManager.getString("companies.domicile"));
        createButton.setText(I18nManager.getString("companies.create"));
    }
}
