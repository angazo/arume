package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T6CompanyTaxRegistrations;
import jakarta.annotation.Generated;

public interface T6CompanyTaxRegistrationsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_company_tax_registrations")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_company_tax_registrations")
    int insert(T6CompanyTaxRegistrations row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_company_tax_registrations")
    int insertSelective(T6CompanyTaxRegistrations row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_company_tax_registrations")
    T6CompanyTaxRegistrations selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_company_tax_registrations")
    int updateByPrimaryKeySelective(T6CompanyTaxRegistrations row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t6_company_tax_registrations")
    int updateByPrimaryKey(T6CompanyTaxRegistrations row);
}