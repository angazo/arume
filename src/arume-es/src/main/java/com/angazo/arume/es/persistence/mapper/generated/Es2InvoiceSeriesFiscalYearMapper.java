package com.angazo.arume.es.persistence.mapper.generated;

import com.angazo.arume.es.persistence.model.Es2InvoiceSeriesFiscalYear;
import jakarta.annotation.Generated;

public interface Es2InvoiceSeriesFiscalYearMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es2_invoice_series_fiscal_year")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es2_invoice_series_fiscal_year")
    int insert(Es2InvoiceSeriesFiscalYear row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es2_invoice_series_fiscal_year")
    int insertSelective(Es2InvoiceSeriesFiscalYear row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es2_invoice_series_fiscal_year")
    Es2InvoiceSeriesFiscalYear selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es2_invoice_series_fiscal_year")
    int updateByPrimaryKeySelective(Es2InvoiceSeriesFiscalYear row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es2_invoice_series_fiscal_year")
    int updateByPrimaryKey(Es2InvoiceSeriesFiscalYear row);
}