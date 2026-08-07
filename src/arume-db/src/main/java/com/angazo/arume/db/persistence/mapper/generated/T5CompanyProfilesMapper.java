package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T5CompanyProfiles;
import jakarta.annotation.Generated;

public interface T5CompanyProfilesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    int insert(T5CompanyProfiles row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    int insertSelective(T5CompanyProfiles row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    T5CompanyProfiles selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    int updateByPrimaryKeySelective(T5CompanyProfiles row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    int updateByPrimaryKey(T5CompanyProfiles row);
}