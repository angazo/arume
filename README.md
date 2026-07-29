<p align="center">
  <img src="arume.png" alt="Arume logo" width="128" />
</p>

<h1 align="center">Arume</h1>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-GPLv3-blue.svg" alt="License" /></a>
  <a href="#"><img src="https://img.shields.io/badge/java-25-ED8B00?logo=openjdk&logoColor=white" alt="Java 25" /></a>
  <a href="#"><img src="https://img.shields.io/badge/gradle-9.6-02303A?logo=gradle" alt="Gradle" /></a>
  <a href="#"><img src="https://img.shields.io/badge/spring_boot-4.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot" /></a>
</p>

<p align="center">
  Cross-platform desktop application for business invoicing and accounting.
</p>

---

## Purpose

This project is developed for **non-profit and educational purposes**, aiming to explore and implement the latest technologies available in the Java ecosystem for desktop applications.

## Project Status

> **Phase 0** — Scaffolding and initial setup

- [x] First-run wizard: database storage path, credentials, language, and theme selection
- [x] External configuration via `arume.yml` alongside the JAR
- [x] H2 database with Flyway migrations in PostgreSQL compatibility mode
- [x] I18n: English and Spanish with runtime language switching
- [x] Theme selector: 3 AtlantaFX variants (Light, Dark, Dark Intense)
- [x] Credential encryption: AES-256/GCM for config URL, H2 `CIPHER=AES` for database files
- [ ] Business domain: entities, mappers, and services
- [ ] Invoicing and accounting features

## Development Tools

| Tool | Description |
|---|---|
| [opencode](https://github.com/anomalyco/opencode) | AI coding assistant |
| [OpenSpec](https://github.com/Fission-AI/OpenSpec/) | Specification-driven development |

## Tech Stack

| Category | Technology | Version |
|---|---|---|
| **Language** | ![Java](https://img.shields.io/badge/-Java-ED8B00?logo=openjdk&logoColor=white) | 25 LTS |
| **Build** | ![Gradle](https://img.shields.io/badge/-Gradle-02303A?logo=gradle) | 9.6 |
| **Framework** | ![Spring Boot](https://img.shields.io/badge/-Spring_Boot-6DB33F?logo=springboot&logoColor=white) | 4.1 |
| **ORM** | ![MyBatis](https://img.shields.io/badge/-MyBatis_Spring_Boot-CD0000) | 4.0 |
| **SQL Gen** | ![MyBatis Generator](https://img.shields.io/badge/-MyBatis_Generator-CD0000) | 2.0 |
| **Database** | ![H2](https://img.shields.io/badge/-H2_Database-004085?logo=h2&logoColor=white) | 2.4 |
| **Desktop UI** | ![JavaFX](https://img.shields.io/badge/-JavaFX-5382A1?logo=openjdk&logoColor=white) | 25 |
| **UI Theme** | ![AtlantaFX](https://img.shields.io/badge/-AtlantaFX-2E7D32) | 2.1 |
| **Logging** | ![Logback](https://img.shields.io/badge/-Logback-000000) | — |

All libraries and tools are **open source**, with no third-party license fees.

## License

Distributed under the **GNU General Public License v3.0**.

Anyone who clones, modifies, or distributes this software must release their changes under the same license, contributing back to the community. Use in proprietary or closed-source products is not permitted.
