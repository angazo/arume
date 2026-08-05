package com.angazo.arume.db.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T1Countries implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t1_countries.numeric_code")
    private Short numericCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t1_countries.alpha3_code")
    private String alpha3Code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t1_countries.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    private static final long serialVersionUID = 1L;
}