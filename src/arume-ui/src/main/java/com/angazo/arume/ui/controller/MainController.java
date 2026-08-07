package com.angazo.arume.ui.controller;

import javax.sql.DataSource;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignH;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import org.kordamp.ikonli.materialdesign2.MaterialDesignW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.angazo.arume.ui.config.ConfigManager;
import com.angazo.arume.ui.config.ThemeConfig;
import com.angazo.arume.ui.i18n.I18nManager;

@Component
public class MainController {

    @FXML private HBox titleBar;

    @FXML private ImageView logoView;

    @FXML private MenuButton helpMenu;

    @FXML private MenuItem aboutMenuItem;

@FXML private Button languageBtn;

@FXML private Button themeBtn;

@FXML private Button minimizeBtn;

    @FXML private Button maximizeBtn;

    @FXML private Button closeBtn;

    @FXML private VBox leftSidebar;

    @FXML private ToggleGroup navGroup;

    @FXML private ToggleButton dashboardBtn;

    @FXML private ToggleButton invoicesBtn;

    @FXML private ToggleButton accountingBtn;

    @FXML private Button settingsBtn;

    @FXML private StackPane contentArea;

    @FXML private VBox dashboardPane;

    @FXML private VBox invoicesPane;

    @FXML private VBox accountingPane;

    @FXML private VBox settingsPane;

    @FXML private StackPane companiesPane;

    @FXML private VBox rightSidebar;

    @FXML private Button helpBtn;

    @FXML private Button companiesBtn;

    @FXML private HBox statusBar;

    @FXML private Region dbDot;

    @FXML private Label dbLabel;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    private final ConfigManager configManager = new ConfigManager();
    private Stage stage;
    private double dragOffsetX;
    private double dragOffsetY;

    private FontIcon sunIcon;
    private FontIcon moonIcon;

    public void setStage(Stage stage) {
        this.stage = stage;
        setupWindowControls();
        setupWindowDrag();
    }

@FXML
public void initialize() {
    setupIcons();
    setupNavigation();
    setupStatusBar();

    dashboardBtn.setSelected(true);

    refreshTexts();
    I18nManager.onLanguageChange(this::onLanguageChanged);
}

    private void setupIcons() {
        var size = 16;
        var navSize = 22;

        var logoImage = new Image(getClass().getResourceAsStream("/icons/arume.png"));
        logoView.setImage(logoImage);

        helpMenu.setGraphic(FontIcon.of(MaterialDesignH.HELP_CIRCLE, size));

sunIcon = FontIcon.of(MaterialDesignW.WHITE_BALANCE_SUNNY, size);
moonIcon = FontIcon.of(FontAwesomeSolid.MOON, size);

minimizeBtn.setGraphic(FontIcon.of(MaterialDesignW.WINDOW_MINIMIZE, size));
        maximizeBtn.setGraphic(FontIcon.of(MaterialDesignW.WINDOW_MAXIMIZE, size));
        closeBtn.setGraphic(FontIcon.of(MaterialDesignW.WINDOW_CLOSE, size));

        dashboardBtn.setGraphic(FontIcon.of(MaterialDesignD.DESKTOP_MAC_DASHBOARD, navSize));
        invoicesBtn.setGraphic(FontIcon.of(MaterialDesignR.RECEIPT, navSize));
        accountingBtn.setGraphic(FontIcon.of(MaterialDesignA.ACCOUNT, navSize));
        settingsBtn.setGraphic(FontIcon.of(MaterialDesignC.COG, navSize));
        companiesBtn.setGraphic(FontIcon.of(FontAwesomeSolid.BUILDING, navSize));

        var sidebarButtons = new javafx.scene.control.ButtonBase[]{dashboardBtn, invoicesBtn, accountingBtn, settingsBtn, companiesBtn, helpBtn};
        for (var btn : sidebarButtons) {
            btn.setContentDisplay(ContentDisplay.TOP);
            btn.setStyle(btn.getStyle() + "-fx-font-size: 14px;");
            btn.setWrapText(true);
        }

        helpBtn.setGraphic(FontIcon.of(MaterialDesignH.HELP_CIRCLE, size));

        selectCurrentThemeIcon();
    }

    private void setupWindowControls() {
        minimizeBtn.setOnAction(e -> stage.setIconified(true));

        maximizeBtn.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
            updateMaximizeIcon();
        });

        closeBtn.setOnAction(e -> {
            var scene = titleBar.getScene();
            if (scene != null) {
                var stageFromScene = (Stage) scene.getWindow();
                stageFromScene.close();
            }
        });

        stage.maximizedProperty().addListener((_, _, _) -> updateMaximizeIcon());
    }

    private void updateMaximizeIcon() {
        if (stage.isMaximized()) {
            maximizeBtn.setGraphic(FontIcon.of(MaterialDesignW.WINDOW_RESTORE, 16));
        } else {
            maximizeBtn.setGraphic(FontIcon.of(MaterialDesignW.WINDOW_MAXIMIZE, 16));
        }
    }

    private void setupWindowDrag() {
        titleBar.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && !isInteractive(event.getTarget())) {
                dragOffsetX = event.getScreenX() - stage.getX();
                dragOffsetY = event.getScreenY() - stage.getY();
            }
        });

        titleBar.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                stage.setX(event.getScreenX() - dragOffsetX);
                stage.setY(event.getScreenY() - dragOffsetY);
            }
        });

        titleBar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !isInteractive(event.getTarget())) {
                stage.setMaximized(!stage.isMaximized());
            }
        });
    }

    private boolean isInteractive(Object target) {
        return target instanceof Button || target instanceof MenuButton;
    }

    private void setupNavigation() {
        navGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
            if (newValue == dashboardBtn) {
                showPane(dashboardPane, "dashboard");
            } else if (newValue == invoicesBtn) {
                showPane(invoicesPane, "invoices");
            } else if (newValue == accountingBtn) {
                showPane(accountingPane, "accounting");
            }
        });

        settingsBtn.setOnAction(_ -> {
            navGroup.selectToggle(null);
            showPane(settingsPane, "settings");
        });

        companiesBtn.setOnAction(_ -> showCompanies());
    }

    private void showPane(Node pane, String name) {
        hideAllPanes();
        pane.setVisible(true);
        pane.setManaged(true);
    }

    private void showCompanies() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/fxml/companies.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent view = loader.load();
            companiesPane.getChildren().setAll(view);
            showPane(companiesPane, "companies");
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load the Companies view", exception);
        }
    }

    private void hideAllPanes() {
        for (var node : contentArea.getChildren()) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    private void setupStatusBar() {
        checkDbStatus();
    }    private void checkDbStatus() {
        if (dataSource != null) {
            try (var conn = dataSource.getConnection()) {
                var valid = conn.isValid(2);
                if (valid) {
                    dbDot.setStyle("-fx-background-color: #4caf50; -fx-min-width: 8; -fx-min-height: 8; -fx-max-width: 8; -fx-max-height: 8; -fx-background-radius: 4;");
                    dbDot.setAccessibleText("connected");
                } else {
                    dbDot.setStyle("-fx-background-color: #f44336; -fx-min-width: 8; -fx-min-height: 8; -fx-max-width: 8; -fx-max-height: 8; -fx-background-radius: 4;");
                    dbDot.setAccessibleText("disconnected");
                }
            } catch (Exception e) {
                dbDot.setStyle("-fx-background-color: #f44336; -fx-min-width: 8; -fx-min-height: 8; -fx-max-width: 8; -fx-max-height: 8; -fx-background-radius: 4;");
                dbDot.setAccessibleText("disconnected");
            }
        }
    }

    @FXML
    public void onAbout() {
        showAboutDialog();
    }

    @FXML
    public void onCompanies() {
        showCompanies();
    }

    private void showAboutDialog() {
        var dialog = new Stage();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setResizable(false);

        var titleBar = new HBox();
        titleBar.getStyleClass().add("title-bar");
        titleBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        var logoView = new ImageView(new Image(getClass().getResourceAsStream("/icons/arume.png")));
        logoView.setFitWidth(22);
        logoView.setFitHeight(22);
        logoView.setPreserveRatio(true);
        HBox.setMargin(logoView, new javafx.geometry.Insets(0, 20, 0, 0));
        var barTitle = new Label(I18nManager.getString("about.title"));
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var closeBtn = new Button("\u2715");
        closeBtn.getStyleClass().add("window-close");
        closeBtn.setOnAction(_ -> dialog.close());
        titleBar.getChildren().addAll(logoView, barTitle, spacer, closeBtn);

        var content = new VBox(16);
        content.setStyle("-fx-padding: 24; -fx-alignment: center;");

        var aboutLogo = new ImageView(new Image(getClass().getResourceAsStream("/icons/arume.png")));
        aboutLogo.setFitWidth(48);
        aboutLogo.setFitHeight(48);
        aboutLogo.setPreserveRatio(true);

        var appName = new Label(I18nManager.getString("app.name"));
        appName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        var versionLabel = new Label(I18nManager.getString("about.version") + " " + getAppVersion());

        var descLabel = new Label(I18nManager.getString("about.description"));
        descLabel.setWrapText(true);

        var closeBtnBottom = new Button(I18nManager.getString("about.close"));
        closeBtnBottom.setOnAction(_ -> dialog.close());
        content.getChildren().addAll(aboutLogo, appName, versionLabel, descLabel, closeBtnBottom);

        var root = new BorderPane();
        root.getStyleClass().add("modal-root");
        root.setTop(titleBar);
        root.setCenter(content);

        var scene = new Scene(root, 360, 318);
        scene.getStylesheets().add(getClass().getResource("/css/arume.css").toExternalForm());
        dialog.setScene(scene);
        dialog.setOnShown(e -> {
            dialog.setX(stage.getX() + (stage.getWidth() - dialog.getWidth()) / 2);
            dialog.setY(stage.getY() + (stage.getHeight() - dialog.getHeight()) / 2);
        });
        dialog.showAndWait();
    }

    private String getAppVersion() {
        var pkg = getClass().getPackage();
        return pkg.getImplementationVersion() != null ? pkg.getImplementationVersion() : "0.0.1-SNAPSHOT";
    }

    @FXML
    public void showAboutFromHelp() {
        showAboutDialog();
    }

@FXML
public void onLanguageToggle() {
    var current = I18nManager.getCurrentLanguage();
    var next = "es".equals(current) ? "en" : "es";
    I18nManager.setLanguage(next);
    configManager.updateLanguage(next);
}

    @FXML
    public void onThemeToggle() {
        var config = configManager.load();
        var current = config.theme();
        var next = "light".equals(current) ? "dark" : "light";
        ThemeConfig.fromId(next).apply();
        configManager.updateTheme(next);
        selectCurrentThemeIcon();
    }

    private void selectCurrentThemeIcon() {
        var config = configManager.load();
        var icon = "dark".equals(config.theme()) ? moonIcon : sunIcon;
        themeBtn.setGraphic(icon);
    }

private void onLanguageChanged() {
    refreshTexts();
    selectLanguageText();
}

private void selectLanguageText() {
    var current = I18nManager.getCurrentLanguage();
    languageBtn.setGraphic(null);
    languageBtn.setText(I18nManager.getString("main.language." + current));
}

    private void updateStatusBarTooltip() {
        var connected = "connected".equals(dbDot.getAccessibleText());
        var text = connected
                ? I18nManager.getString("status.db.connected")
                : I18nManager.getString("status.db.disconnected");
        dbDot.setAccessibleHelp(text);
        dbLabel.setAccessibleHelp(text);
    }

private void refreshTexts() {
    selectLanguageText();
    selectCurrentThemeIcon();

    dashboardBtn.setText(I18nManager.getString("nav.dashboard"));
    invoicesBtn.setText(I18nManager.getString("nav.invoices"));
    accountingBtn.setText(I18nManager.getString("nav.accounting"));
    settingsBtn.setText(I18nManager.getString("nav.settings"));
    companiesBtn.setText(I18nManager.getString("nav.companies"));

    helpMenu.setText(I18nManager.getString("menu.help"));
    aboutMenuItem.setText(I18nManager.getString("menu.help.about"));

    helpBtn.setText(I18nManager.getString("menu.help"));
}
}
