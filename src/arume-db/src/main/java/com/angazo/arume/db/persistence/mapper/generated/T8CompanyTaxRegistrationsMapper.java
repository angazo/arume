package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T8CompanyTaxRegistrations;
import jakarta.annotation.Generated;

public interface T8CompanyTaxRegistrationsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    int insert(T8CompanyTaxRegistrations row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    int insertSelective(T8CompanyTaxRegistrations row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    T8CompanyTaxRegistrations selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    int updateByPrimaryKeySelective(T8CompanyTaxRegistrations row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    int updateByPrimaryKey(T8CompanyTaxRegistrations row);
}