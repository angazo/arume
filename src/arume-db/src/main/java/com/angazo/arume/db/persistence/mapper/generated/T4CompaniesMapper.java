package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T4Companies;
import jakarta.annotation.Generated;

public interface T4CompaniesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    int insert(T4Companies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    int insertSelective(T4Companies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    T4Companies selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    int updateByPrimaryKeySelective(T4Companies row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    int updateByPrimaryKey(T4Companies row);
}