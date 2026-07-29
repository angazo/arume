## 1. Build cleanup

- [x] 1.1 Remove jasypt version from `libs.versions.toml` (already done)
- [x] 1.2 Remove jasypt plugin declaration from `libs.versions.toml` (already done)
- [x] 1.3 Remove jasypt plugin from `arume-app/build.gradle` (already done)
- [x] 1.4 Verify `./gradlew build` succeeds after jasypt removal

## 2. EncryptionService

- [x] 2.1 Create `EncryptionService` class in `com.angazo.arume.ui.config` with AES-256/GCM encrypt/decrypt methods
- [x] 2.2 Implement `deriveKey(Path jarDir)` with multiplatform `KeyProvider`: Linux UUID (via `blkid` or `/dev/disk/by-uuid/`), macOS UUID (via `diskutil info`), Windows volume serial (via `FileStore.getAttribute("volume:vsn")`), and fallback to `FileStore.name() + type() + totalSpace` hash
- [x] 2.3 Implement `encrypt(String plaintext)` returning `"ENC(" + base64(iv + ciphertext + tag) + ")"`
- [x] 2.4 Implement `decrypt(String wrapped)` parsing `ENC(...)` prefix, extracting IV and ciphertext, decrypting and returning plaintext
- [x] 2.5 Implement `isEncrypted(String value)` returning true if value starts with `ENC(`
- [x] 2.6 Create `EncryptionServiceTest`: test encrypt/decrypt roundtrip, test key stability (same path → same key), test key change (different path → different key), test tampered data detection

## 3. I18n messages for decryption error

- [x] 3.1 Add `decrypt.error.title`, `decrypt.error.header`, `decrypt.error.content`, `decrypt.error.reconfigure`, `decrypt.error.exit` to `messages.properties`
- [x] 3.2 Add Spanish translations to `messages_es.properties`

## 4. ArumeConfig simplification

- [x] 4.1 Remove `username` and `password` fields from `ArumeConfig` record
- [x] 4.2 Update all call sites that construct `ArumeConfig` to use the new 5-parameter constructor

## 5. ConfigManager modifications

- [x] 5.1 Change `buildH2Url` signature to `buildH2Url(Path storagePath, String username, String password, boolean encrypt)` — embed `USER`/`PASSWORD` in URL; add `CIPHER=AES` when `encrypt=true`
- [x] 5.2 Integrate `EncryptionService` into `ConfigManager`: instantiate with `jarDir`
- [x] 5.3 Modify `save()`: when `config.encrypt() == true`, encrypt URL via `EncryptionService` before writing to YAML; remove `username`/`password` from YAML output
- [x] 5.4 Modify `load()`: after reading URL from YAML, if `isEncrypted(url)`, decrypt via `EncryptionService`; stop reading `username`/`password` from YAML
- [x] 5.5 Simplify `applyToSystemProperties()`: remove `System.setProperty` calls for `spring.datasource.username` and `spring.datasource.password`
- [x] 5.6 Update `updateLanguage()` and `updateTheme()` to construct `ArumeConfig` with new 5-field signature

## 6. Startup flow and wizard integration

- [x] 6.1 Update `WizardResult` to carry username and password (keep as-is, it already does)
- [x] 6.2 Update `ArumeAppFX.buildConfigFromWizard()`: pass `username` and `password` to `buildH2Url()` instead of to `ArumeConfig` constructor
- [x] 6.3 Add decryption failure handling in `ArumeAppFX.ApplicationLoader.start()`: catch decryption errors during `configManager.load()`, show JavaFX Alert with reconfigure/exit options, delete `arume.yml` and exit if "Reconfigure" chosen

## 7. Tests

- [x] 7.1 Update `ConfigManagerTest` for new `ArumeConfig` signature (5 fields, no username/password)
- [x] 7.2 Update `ConfigManagerTest` for new `arume.yml` format (URL with embedded credentials, no separate username/password in YAML)
- [x] 7.3 Add test: `save()` produces encrypted URL when `encrypt=true`
- [x] 7.4 Add test: `save()` produces plain URL when `encrypt=false`
- [x] 7.5 Add test: `load()` decrypts `ENC(...)` URL correctly
- [x] 7.6 Add test: `applyToSystemProperties()` sets only url and driver-class-name
- [x] 7.7 Add test: `buildH2Url()` includes `CIPHER=AES` when `encrypt=true`
- [x] 7.8 Add test: `buildH2Url()` does NOT include `CIPHER=AES` when `encrypt=false`
- [x] 7.9 Add test: `buildH2Url()` includes `USER` and `PASSWORD` in URL
- [x] 7.10 Add test: `updateLanguage()` preserves encrypted URL state

## 8. Verification

- [x] 8.1 Run `./gradlew test` and verify all tests pass
- [x] 8.2 Run `./gradlew build` and verify successful compilation
- [ ] 8.3 Manual smoke test: delete `arume.yml`, launch app, complete wizard with encrypt=true, verify `arume.yml` has `ENC(...)` URL, restart app, verify it loads without wizard

(End of file - total 59 lines)
