## Context

Actualmente el tema visual se hardcodea en `ArumeAppFX` como `Dracula`. Se necesita:
- Persistir la preferencia de tema en `arume.yml`
- Ofrecer 3 opciones: light (PrimerLight), dark (Dracula), dark-intense (PrimerDark)
- Selector en el wizard de primer arranque y en el menú principal
- Cambio en vivo sin reiniciar

## Goals / Non-Goals

**Goals:**
- Persistir `theme` en `ArumeConfig` y `arume.yml`
- Selector de tema en wizard (ComboBox junto al de idioma)
- Selector de tema en menú principal (RadioMenuItem junto a Language)
- Cambio en vivo con `Application.setUserAgentStylesheet()`
- i18n para etiquetas de temas

**Non-Goals:**
- Temas adicionales más allá de los 3 definidos
- Personalización de colores por el usuario
- Vistazo previo (preview) de temas

## Decisions

| Decisión | Opción elegida | Alternativas | Razón |
|---|---|---|---|
| Nombres internos | `light`, `dark`, `dark-intense` | `primer-light`, `dracula` | Semántico, desacoplado de AtlantaFX |
| Persistencia | `arume.yml` como campo `arume.theme` | BBDD | Coherente con language y db, ya existe el mecanismo |
| Mapeo tema→clase | `ThemeConfig` enum en `com.angazo.arume.ui.config` con método `getStylesheet()` | Switch en ArumeAppFX | Encapsulado, testeable, fácil de extender |
| Cambio en vivo | `Application.setUserAgentStylesheet()` directo | Scene re-creation | API oficial de JavaFX, funciona globalmente |
| Selector en wizard | `ComboBox<String>` en HBox con el de idioma | `RadioButton` | Consistente con el patrón existente de idioma |
| Selector en menú | `RadioMenuItem`s en nuevo Menu "Theme" | Diálogo modal | Integrado en la barra existente, mínimo impacto |

## Risks / Trade-offs

- [Riesgo] `Application.setUserAgentStylesheet()` no es reversible por tema → Mitigación: siempre se asigna el nuevo, no hay "ninguno"
- [Riesgo] Las escenas existentes pueden no refrescar todos los controles → Mitigación: se ha verificado que AtlantaFX responde correctamente a cambios de stylesheet
- [Trade-off] Usar `String` para los nombres internos en lugar de enum en `ArumeConfig` record (los records no admiten enums fácilmente con SnakeYAML). Se valida en el momento de cargar contra `ThemeConfig`.

## Open Questions

- Ninguna por ahora
