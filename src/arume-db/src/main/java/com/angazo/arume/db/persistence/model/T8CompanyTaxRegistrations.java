package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T8CompanyTaxRegistrations implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t8_company_tax_registrations.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t8_company_tax_registrations.company_id")
    private Long companyId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t8_company_tax_registrations.jurisdiction")
    private String jurisdiction;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t8_company_tax_registrations.tax_id")
    private String taxId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t8_company_tax_registrations.valid_from")
    private LocalDate validFrom;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t8_company_tax_registrations.valid_to")
    private LocalDate validTo;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t8_company_tax_registrations")
    private static final long serialVersionUID = 1L;
}