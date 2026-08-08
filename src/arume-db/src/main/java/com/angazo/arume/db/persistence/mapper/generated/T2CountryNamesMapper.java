package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T2CountryNames;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.Param;

public interface T2CountryNamesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    int deleteByPrimaryKey(@Param("countryAlpha2Code") String countryAlpha2Code, @Param("languageCode") String languageCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    int insert(T2CountryNames row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    int insertSelective(T2CountryNames row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    T2CountryNames selectByPrimaryKey(@Param("countryAlpha2Code") String countryAlpha2Code, @Param("languageCode") String languageCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    int updateByPrimaryKeySelective(T2CountryNames row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_country_names")
    int updateByPrimaryKey(T2CountryNames row);
}