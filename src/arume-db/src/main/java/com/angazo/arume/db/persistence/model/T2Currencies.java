package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T2Currencies implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_currencies.numeric_code")
    private Short numericCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_currencies.alpha3_code")
    private String alpha3Code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_currencies.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t2_currencies.symbol")
    private String symbol;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    private static final long serialVersionUID = 1L;
}