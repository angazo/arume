package com.angazo.arume.db.repository.generated;

import com.angazo.arume.db.model.T1Countries;
import jakarta.annotation.Generated;

public interface T1CountriesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    int deleteByPrimaryKey(Short numericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    int insert(T1Countries row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    int insertSelective(T1Countries row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    T1Countries selectByPrimaryKey(Short numericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    int updateByPrimaryKeySelective(T1Countries row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t1_countries")
    int updateByPrimaryKey(T1Countries row);
}