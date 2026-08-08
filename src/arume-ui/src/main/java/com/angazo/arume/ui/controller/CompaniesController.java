package com.angazo.arume.ui.controller;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import org.springframework.stereotype.Component;

import com.angazo.arume.core.application.catalog.CountryCatalogService;
import com.angazo.arume.core.application.catalog.LegalFormCatalogService;
import com.angazo.arume.core.application.company.CompanyApplicationService;
import com.angazo.arume.core.application.company.CreateCompanyCommand;
import com.angazo.arume.core.domain.catalog.CountryCatalogEntry;
import com.angazo.arume.core.domain.common.JurisdictionCode;
import com.angazo.arume.core.domain.company.CompanyProfile;
import com.angazo.arume.core.domain.company.CompanySummary;
import com.angazo.arume.core.domain.company.FiscalIdentification;
import com.angazo.arume.core.domain.company.LegalFormCode;
import com.angazo.arume.core.domain.company.SubjectType;
import com.angazo.arume.ui.i18n.I18nManager;

@Component
public class CompaniesController {

    private static final String FORMAT_SEPARATOR = " — ";
    private static final String DEFAULT_JURISDICTION = "ES";

    @FXML private Label titleLabel;
    @FXML private Label subjectTypeLabel;
    @FXML private Label jurisdictionLabel;
    @FXML private Label fiscalIdLabel;
    @FXML private Label legalFormLabel;
    @FXML private Label legalNameLabel;
    @FXML private Label domicileLabel;
    @FXML private ComboBox<String> subjectTypeCombo;
    @FXML private ComboBox<CountryCatalogEntry> jurisdictionCombo;
    @FXML private TextField fiscalIdField;
    @FXML private ComboBox<String> legalFormCombo;
    @FXML private TextField legalNameField;
    @FXML private TextField domicileField;
    @FXML private Button createButton;
    @FXML private Label feedbackLabel;
    @FXML private ListView<CompanySummary> companyList;

    private final CompanyApplicationService companyService;
    private final CountryCatalogService countryCatalogService;
    private final LegalFormCatalogService legalFormCatalogService;

    public CompaniesController(
        CompanyApplicationService companyService,
        CountryCatalogService countryCatalogService,
        LegalFormCatalogService legalFormCatalogService
    ) {
        this.companyService = companyService;
        this.countryCatalogService = countryCatalogService;
        this.legalFormCatalogService = legalFormCatalogService;
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
        jurisdictionCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CountryCatalogEntry entry) {
                return entry == null ? null : entry.name();
            }

            @Override
            public CountryCatalogEntry fromString(String value) {
                return null;
            }
        });
        jurisdictionCombo.valueProperty().addListener((_, _, _) -> refreshLegalForms());
        subjectTypeCombo.setItems(FXCollections.observableArrayList(
            I18nManager.getString("companies.subjectType.naturalPerson"),
            I18nManager.getString("companies.subjectType.legalPerson")
        ));
        subjectTypeCombo.getSelectionModel().select(
            I18nManager.getString("companies.subjectType.legalPerson")
        );
        subjectTypeCombo.valueProperty().addListener((_, _, _) -> refreshLegalForms());
        I18nManager.onLanguageChange(this::refreshTexts);
        refreshTexts();
        refreshCountries();
        refreshCompanies();
        refreshLegalForms();
    }

    @FXML
    public void onCreate() {
        try {
            var jurisdiction = selectedJurisdiction();
            var subjectType = selectedSubjectType();
            var legalFormCode = selectedLegalFormCode();
            var company = companyService.create(new CreateCompanyCommand(
                subjectType,
                new FiscalIdentification(jurisdiction, fiscalIdField.getText()),
                new LegalFormCode(jurisdiction, legalFormCode),
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

    private SubjectType selectedSubjectType() {
        var selected = subjectTypeCombo.getValue();
        if (selected == null || selected.isBlank()) {
            throw new IllegalArgumentException("No subject type selected");
        }
        if (selected.equals(I18nManager.getString("companies.subjectType.naturalPerson"))) {
            return SubjectType.NATURAL_PERSON;
        }
        return SubjectType.LEGAL_PERSON;
    }

    private JurisdictionCode selectedJurisdiction() {
        var selected = jurisdictionCombo.getValue();
        if (selected == null) {
            throw new IllegalArgumentException("No jurisdiction selected");
        }
        return selected.code();
    }

    private String selectedLegalFormCode() {
        var selected = legalFormCombo.getValue();
        if (selected == null || selected.isBlank()) {
            throw new IllegalArgumentException("No legal form selected");
        }
        return selected.split(java.util.regex.Pattern.quote(FORMAT_SEPARATOR), 2)[0].trim();
    }

    private void refreshCountries() {
        var previous = jurisdictionCombo.getValue();
        var countries = countryCatalogService.list(I18nManager.getCurrentLanguage());
        jurisdictionCombo.setItems(FXCollections.observableArrayList(countries));
        selectJurisdiction(countries, previous == null ? DEFAULT_JURISDICTION : previous.code().value());
    }

    private void selectJurisdiction(List<CountryCatalogEntry> countries, String code) {
        countries.stream()
            .filter(entry -> entry.code().value().equals(code))
            .findFirst()
            .ifPresentOrElse(
                entry -> jurisdictionCombo.getSelectionModel().select(entry),
                () -> jurisdictionCombo.getSelectionModel().selectFirst()
            );
    }

    private void refreshLegalForms() {
        var jurisdiction = jurisdictionCombo.getValue();
        if (jurisdiction == null) {
            disableLegalFormCombo(null);
            return;
        }

        var items = legalFormCatalogService.list(jurisdiction.code(), selectedSubjectType());
        if (items.isEmpty()) {
            disableLegalFormCombo(I18nManager.getString("companies.legalForm.noCatalog"));
            return;
        }

        var entries = items.stream()
            .map(item -> item.code() + FORMAT_SEPARATOR + item.description())
            .toList();
        legalFormCombo.setItems(FXCollections.observableArrayList(entries));
        legalFormCombo.setDisable(false);
        legalFormCombo.getSelectionModel().selectFirst();
    }

    private void disableLegalFormCombo(String promptText) {
        legalFormCombo.setDisable(true);
        legalFormCombo.getItems().clear();
        legalFormCombo.setValue(null);
        legalFormCombo.setPromptText(promptText);
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
        subjectTypeLabel.setText(I18nManager.getString("companies.subjectType"));
        jurisdictionLabel.setText(I18nManager.getString("companies.jurisdiction"));
        fiscalIdLabel.setText(I18nManager.getString("companies.fiscalId"));
        legalFormLabel.setText(I18nManager.getString("companies.legalForm"));
        legalNameLabel.setText(I18nManager.getString("companies.legalName"));
        domicileLabel.setText(I18nManager.getString("companies.domicile"));
        createButton.setText(I18nManager.getString("companies.create"));
        refreshCountries();
        refreshLegalForms();
    }
}
