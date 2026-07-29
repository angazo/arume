# Configuration Encryption

## Purpose

Encrypt sensitive configuration values in `arume.yml` using a key derived from filesystem attributes of the JAR's location. Provide detection and recovery when decryption fails due to environment changes.

## Requirements

### Requirement: Encryption key derivation from JAR filesystem
The system SHALL derive an AES-256 encryption key from the filesystem where the application JAR resides, using platform-specific identifiers.

#### Scenario: Key derived from filesystem UUID on Linux
- **WHEN** the application runs on Linux and the JAR directory resides on a filesystem with a UUID
- **THEN** the system SHALL use the filesystem UUID as key material to derive the encryption key via PBKDF2

#### Scenario: Key derived from volume serial on Windows
- **WHEN** the application runs on Windows
- **THEN** the system SHALL use `FileStore.getAttribute("volume:vsn")` as key material to derive the encryption key

#### Scenario: Key derived from volume UUID on macOS
- **WHEN** the application runs on macOS and the JAR directory resides on a volume with a UUID
- **THEN** the system SHALL use the volume UUID as key material to derive the encryption key

#### Scenario: Fallback when platform-specific identifier is unavailable
- **WHEN** the platform-specific identifier cannot be obtained
- **THEN** the system SHALL fall back to a hash of `FileStore.name()`, `FileStore.type()`, and `FileStore.getTotalSpace()` as key material

#### Scenario: Same key for same JAR location
- **WHEN** the application runs from the same JAR file path on the same filesystem
- **THEN** the derived encryption key SHALL be identical across restarts

#### Scenario: Different key for different filesystem
- **WHEN** the application JAR is moved to a different disk or partition
- **THEN** the derived encryption key SHALL be different

### Requirement: URL encryption
The system SHALL encrypt the JDBC URL using AES-256/GCM when the `encrypt` configuration flag is true.

#### Scenario: URL is encrypted before saving
- **WHEN** `ConfigManager.save()` is called with `encrypt=true`
- **THEN** the `spring.datasource.url` value SHALL be stored encrypted in `arume.yml` with the format `ENC(<base64>)` where `<base64>` encodes the 12-byte IV followed by the ciphertext with GCM authentication tag

#### Scenario: URL is stored in plain text when encrypt is false
- **WHEN** `ConfigManager.save()` is called with `encrypt=false`
- **THEN** the `spring.datasource.url` value SHALL be stored in plain text without the `ENC()` wrapper

### Requirement: URL decryption
The system SHALL decrypt the JDBC URL when loading `arume.yml` if the value is encrypted.

#### Scenario: Encrypted URL is decrypted on load
- **WHEN** `ConfigManager.load()` reads an `arume.yml` where `spring.datasource.url` starts with `ENC(`
- **THEN** the system SHALL decrypt the value and return the plain text URL in the `ArumeConfig` record

#### Scenario: Plain text URL is returned as-is
- **WHEN** `ConfigManager.load()` reads an `arume.yml` where `spring.datasource.url` does NOT start with `ENC(`
- **THEN** the system SHALL return the URL value without modification

### Requirement: Decryption failure handling
The system SHALL detect decryption failures and offer the user a path to recover.

#### Scenario: Decryption fails due to environment change
- **WHEN** `ConfigManager.load()` attempts to decrypt an `ENC()` URL and the decryption fails (wrong key due to filesystem change, corrupted data, or tampering)
- **THEN** the system SHALL display an error dialog explaining that the encrypted configuration cannot be decrypted and that this typically occurs when the JAR has been moved to a different machine or disk

#### Scenario: User chooses to reconfigure after decryption failure
- **WHEN** the decryption failure dialog is displayed and the user selects the "Reconfigure" option
- **THEN** the system SHALL delete `arume.yml` and terminate, allowing the next launch to show the first-run wizard

#### Scenario: User chooses to exit after decryption failure
- **WHEN** the decryption failure dialog is displayed and the user selects the "Exit" option
- **THEN** the system SHALL terminate without deleting any files
