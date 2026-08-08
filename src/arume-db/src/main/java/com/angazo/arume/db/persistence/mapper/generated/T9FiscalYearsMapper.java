package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T9FiscalYears;
import jakarta.annotation.Generated;

public interface T9FiscalYearsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t9_fiscal_years")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t9_fiscal_years")
    int insert(T9FiscalYears row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t9_fiscal_years")
    int insertSelective(T9FiscalYears row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t9_fiscal_years")
    T9FiscalYears selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t9_fiscal_years")
    int updateByPrimaryKeySelective(T9FiscalYears row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t9_fiscal_years")
    int updateByPrimaryKey(T9FiscalYears row);
}