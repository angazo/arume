package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T4Companies implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t4_companies.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t4_companies.primary_fiscal_jurisdiction")
    private String primaryFiscalJurisdiction;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t4_companies.primary_fiscal_id")
    private String primaryFiscalId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t4_companies.legal_form_jurisdiction")
    private String legalFormJurisdiction;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t4_companies.legal_form_code")
    private String legalFormCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t4_companies.created_at")
    private LocalDateTime createdAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t4_companies")
    private static final long serialVersionUID = 1L;
}