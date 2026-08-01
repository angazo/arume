# Icon Library

## Purpose

Integrate the Ikonli library with FontAwesome5 and MaterialDesign2 icon packs for vector icon rendering throughout the UI.

## Requirements

### Requirement: Ikonli dependency is available
The project SHALL include the Ikonli JavaFX library and at least two icon packs (FontAwesome5 and MaterialDesign2) as Gradle dependencies.

#### Scenario: Ikonli JavaFX is on the classpath
- **WHEN** the project is built
- **THEN** `org.kordamp.ikonli:ikonli-javafx` SHALL be available in the `arume-ui` module

#### Scenario: FontAwesome5 icon pack is on the classpath
- **WHEN** the project is built
- **THEN** `org.kordamp.ikonli:ikonli-fontawesome5-pack` SHALL be available in the `arume-ui` module

#### Scenario: MaterialDesign2 icon pack is on the classpath
- **WHEN** the project is built
- **THEN** `org.kordamp.ikonli:ikonli-materialdesign2-pack` SHALL be available in the `arume-ui` module

### Requirement: Ikonli FontIcon usage
The application SHALL use `org.kordamp.ikonli.javafx.FontIcon` to render vector icons throughout the UI.

#### Scenario: FontIcon can be created from icon code
- **WHEN** a `FontIcon` is created with a MaterialDesign2 icon code (e.g., `MaterialDesignC.COG`)
- **THEN** the icon SHALL render as a scalable vector graphic in the JavaFX scene graph

#### Scenario: FontIcon can be used as graphic in buttons
- **WHEN** a `FontIcon` is set as the graphic of a JavaFX `Button`
- **THEN** the icon SHALL display correctly inside the button, respecting the button's styling

#### Scenario: FontIcon inherits theme colors
- **WHEN** a `FontIcon` is displayed in the UI and the AtlantaFX theme is changed
- **THEN** the icon SHALL automatically reflect the new theme's colors
