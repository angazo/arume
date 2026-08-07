package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T3CountryCurrency;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.Param;

public interface T3CountryCurrencyMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_country_currency")
    int deleteByPrimaryKey(@Param("countryNumericCode") Short countryNumericCode, @Param("currencyNumericCode") Short currencyNumericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_country_currency")
    int insert(T3CountryCurrency row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_country_currency")
    int insertSelective(T3CountryCurrency row);
}