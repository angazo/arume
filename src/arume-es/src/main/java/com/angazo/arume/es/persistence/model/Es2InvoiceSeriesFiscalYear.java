package com.angazo.arume.es.persistence.model;

import com.angazo.arume.es.logic.invoice.series.NumberingMode;
import jakarta.annotation.Generated;

public class Es2InvoiceSeriesFiscalYear {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.series_id")
    private Long seriesId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.fiscal_year_id")
    private Long fiscalYearId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.numbering_mode")
    private NumberingMode numberingMode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.active")
    private Boolean active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.last_assigned_number")
    private Long lastAssignedNumber;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.series_id")
    public Long getSeriesId() {
        return seriesId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.series_id")
    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.fiscal_year_id")
    public Long getFiscalYearId() {
        return fiscalYearId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.fiscal_year_id")
    public void setFiscalYearId(Long fiscalYearId) {
        this.fiscalYearId = fiscalYearId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.numbering_mode")
    public NumberingMode getNumberingMode() {
        return numberingMode;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.numbering_mode")
    public void setNumberingMode(NumberingMode numberingMode) {
        this.numberingMode = numberingMode;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.active")
    public Boolean getActive() {
        return active;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.last_assigned_number")
    public Long getLastAssignedNumber() {
        return lastAssignedNumber;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es2_invoice_series_fiscal_year.last_assigned_number")
    public void setLastAssignedNumber(Long lastAssignedNumber) {
        this.lastAssignedNumber = lastAssignedNumber;
    }
}