package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T2CountryNames implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_country_names.country_alpha2_code")
    private String countryAlpha2Code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_country_names.language_code")
    private String languageCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_country_names.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    private static final long serialVersionUID = 1L;
}