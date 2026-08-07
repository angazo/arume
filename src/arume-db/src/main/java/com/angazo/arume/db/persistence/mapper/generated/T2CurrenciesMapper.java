package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T2Currencies;
import jakarta.annotation.Generated;

public interface T2CurrenciesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    int deleteByPrimaryKey(Short numericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    int insert(T2Currencies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    int insertSelective(T2Currencies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    T2Currencies selectByPrimaryKey(Short numericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    int updateByPrimaryKeySelective(T2Currencies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t2_currencies")
    int updateByPrimaryKey(T2Currencies row);
}