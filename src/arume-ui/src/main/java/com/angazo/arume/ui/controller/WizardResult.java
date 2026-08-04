package com.angazo.arume.ui.controller;

public record WizardResult(
    String country,
    String language,
    String dbType,
    String storagePath,
    String username,
    String password,
    String dbEncryptPassword,
    boolean encrypt,
    String theme
) {}
