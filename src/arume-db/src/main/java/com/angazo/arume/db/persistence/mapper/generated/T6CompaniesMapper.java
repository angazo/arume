package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T6Companies;
import jakarta.annotation.Generated;

public interface T6CompaniesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_companies")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_companies")
    int insert(T6Companies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_companies")
    int insertSelective(T6Companies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_companies")
    T6Companies selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_companies")
    int updateByPrimaryKeySelective(T6Companies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_companies")
    int updateByPrimaryKey(T6Companies row);
}