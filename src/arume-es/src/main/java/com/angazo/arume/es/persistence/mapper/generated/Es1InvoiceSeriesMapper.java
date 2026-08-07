package com.angazo.arume.es.persistence.mapper.generated;

import com.angazo.arume.es.persistence.model.Es1InvoiceSeries;
import jakarta.annotation.Generated;

public interface Es1InvoiceSeriesMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es1_invoice_series")
    int deleteByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es1_invoice_series")
    int insert(Es1InvoiceSeries row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es1_invoice_series")
    int insertSelective(Es1InvoiceSeries row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es1_invoice_series")
    Es1InvoiceSeries selectByPrimaryKey(Long id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es1_invoice_series")
    int updateByPrimaryKeySelective(Es1InvoiceSeries row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es1_invoice_series")
    int updateByPrimaryKey(Es1InvoiceSeries row);
}