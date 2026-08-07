package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T7FiscalYears;
import jakarta.annotation.Generated;

public interface T7FiscalYearsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    int insert(T7FiscalYears row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    int insertSelective(T7FiscalYears row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    T7FiscalYears selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    int updateByPrimaryKeySelective(T7FiscalYears row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    int updateByPrimaryKey(T7FiscalYears row);
}