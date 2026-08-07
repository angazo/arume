package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T7FiscalYears implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t7_fiscal_years.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t7_fiscal_years.company_id")
    private Long companyId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t7_fiscal_years.start_date")
    private LocalDate startDate;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t7_fiscal_years.end_date")
    private LocalDate endDate;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t7_fiscal_years.status")
    private String status;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t7_fiscal_years.label")
    private String label;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t7_fiscal_years")
    private static final long serialVersionUID = 1L;
}