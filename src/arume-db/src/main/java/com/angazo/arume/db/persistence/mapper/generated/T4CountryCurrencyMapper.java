package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T4CountryCurrency;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.Param;

public interface T4CountryCurrencyMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_country_currency")
    int deleteByPrimaryKey(@Param("countryAlpha2Code") String countryAlpha2Code, @Param("currencyNumericCode") Short currencyNumericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_country_currency")
    int insert(T4CountryCurrency row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_country_currency")
    int insertSelective(T4CountryCurrency row);
}