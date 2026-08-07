package com.angazo.arume.es.persistence.model;

import jakarta.annotation.Generated;

public class Es3LegalForms {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.code")
    private String code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.country_numeric_code")
    private Short countryNumericCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.description")
    private String description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.is_legal_person")
    private Boolean isLegalPerson;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.code")
    public String getCode() {
        return code;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.code")
    public void setCode(String code) {
        this.code = code;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.country_numeric_code")
    public Short getCountryNumericCode() {
        return countryNumericCode;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.country_numeric_code")
    public void setCountryNumericCode(Short countryNumericCode) {
        this.countryNumericCode = countryNumericCode;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.description")
    public String getDescription() {
        return description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.description")
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.is_legal_person")
    public Boolean getIsLegalPerson() {
        return isLegalPerson;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es3_legal_forms.is_legal_person")
    public void setIsLegalPerson(Boolean isLegalPerson) {
        this.isLegalPerson = isLegalPerson;
    }
}