package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T5LegalForms implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_legal_forms.country_alpha2_code")
    private String countryAlpha2Code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_legal_forms.code")
    private String code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_legal_forms.description")
    private String description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_legal_forms.is_organization")
    private Boolean isOrganization;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    private static final long serialVersionUID = 1L;
}