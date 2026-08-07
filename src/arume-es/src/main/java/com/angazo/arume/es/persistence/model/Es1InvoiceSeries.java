package com.angazo.arume.es.persistence.model;

import jakarta.annotation.Generated;
import java.time.LocalDateTime;

public class Es1InvoiceSeries {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.company_id")
    private Long companyId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.code")
    private String code;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.description")
    private String description;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.active")
    private Boolean active;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.created_at")
    private LocalDateTime createdAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.company_id")
    public Long getCompanyId() {
        return companyId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.company_id")
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.code")
    public String getCode() {
        return code;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.code")
    public void setCode(String code) {
        this.code = code;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.description")
    public String getDescription() {
        return description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.description")
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.active")
    public Boolean getActive() {
        return active;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.es1_invoice_series.created_at")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}