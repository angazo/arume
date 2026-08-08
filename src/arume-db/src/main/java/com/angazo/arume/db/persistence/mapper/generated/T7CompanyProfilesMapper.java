package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T7CompanyProfiles;
import jakarta.annotation.Generated;

public interface T7CompanyProfilesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_company_profiles")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_company_profiles")
    int insert(T7CompanyProfiles row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_company_profiles")
    int insertSelective(T7CompanyProfiles row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_company_profiles")
    T7CompanyProfiles selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_company_profiles")
    int updateByPrimaryKeySelective(T7CompanyProfiles row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_company_profiles")
    int updateByPrimaryKey(T7CompanyProfiles row);
}