package com.angazo.arume.ui.controller;

import com.angazo.arume.core.domain.catalog.LegalFormItem;
import com.angazo.arume.ui.i18n.I18nManager;

/**
 * Groups the legal forms of a jurisdiction so that the user can narrow a long catalog.
 *
 * <p>This is a navigation aid of the user interface, not a statement about legal personality:
 * it is never stored with the company.
 */
public enum LegalFormFamily {

    ORGANIZATION("companies.legalFormFamily.organization", true),
    INDIVIDUAL("companies.legalFormFamily.individual", false);

    private final String labelKey;
    private final boolean organization;

    LegalFormFamily(String labelKey, boolean organization) {
        this.labelKey = labelKey;
        this.organization = organization;
    }

    public String label() {
        return I18nManager.getString(labelKey);
    }

    public boolean matches(LegalFormItem item) {
        return item.organization() == organization;
    }
}
