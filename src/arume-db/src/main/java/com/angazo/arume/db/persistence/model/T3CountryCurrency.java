package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T3CountryCurrency implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t3_country_currency.country_numeric_code")
    private Short countryNumericCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t3_country_currency.currency_numeric_code")
    private Short currencyNumericCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_country_currency")
    private static final long serialVersionUID = 1L;
}