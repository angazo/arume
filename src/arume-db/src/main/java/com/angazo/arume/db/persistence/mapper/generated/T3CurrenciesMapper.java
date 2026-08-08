package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T3Currencies;
import jakarta.annotation.Generated;

public interface T3CurrenciesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_currencies")
    int deleteByPrimaryKey(Short numericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_currencies")
    int insert(T3Currencies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_currencies")
    int insertSelective(T3Currencies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_currencies")
    T3Currencies selectByPrimaryKey(Short numericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_currencies")
    int updateByPrimaryKeySelective(T3Currencies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t3_currencies")
    int updateByPrimaryKey(T3Currencies row);
}