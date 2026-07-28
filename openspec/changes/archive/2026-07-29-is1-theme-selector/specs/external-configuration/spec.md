# External Configuration

## Purpose

Manage the `arume.yml` configuration file that stores database connection settings, language preference, and theme preference alongside the application JAR.

## ADDED Requirements

### Requirement: Theme field in configuration
The `arume.yml` configuration file SHALL include a theme preference field under the `arume` key.

#### Scenario: Config save includes theme
- **WHEN** `ConfigManager.save()` is called with an `ArumeConfig` that has `theme` set to `"dark-intense"`
- **THEN** the resulting YAML SHALL contain `arume.theme: dark-intense`

#### Scenario: Config load reads theme
- **WHEN** `ConfigManager.load()` reads an `arume.yml` containing `arume.theme: dark`
- **THEN** the returned `ArumeConfig` SHALL have `theme()` equal to `"dark"`

#### Scenario: Config file without theme field defaults to light
- **WHEN** `ConfigManager.load()` reads an `arume.yml` created by a previous version that does NOT contain `arume.theme`
- **THEN** the returned `ArumeConfig` SHALL have `theme()` equal to `"light"` (backward compatibility)

#### Scenario: Theme can be updated independently
- **WHEN** the user changes theme from the main application window
- **THEN** the system SHALL update `arume.theme` in `arume.yml` without modifying other configuration values
