package com.angazo.arume.ui.controller;

public record WizardResult(
    String language,
    String dbType,
    String storagePath,
    String username,
    String password,
    boolean encrypt,
    String theme
) {}
