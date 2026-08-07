package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T5CompanyProfiles implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.company_id")
    private Long companyId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.legal_name")
    private String legalName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.fiscal_residence")
    private String fiscalResidence;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.domicile")
    private String domicile;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.valid_from")
    private LocalDate validFrom;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t5_company_profiles.valid_to")
    private LocalDate validTo;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_company_profiles")
    private static final long serialVersionUID = 1L;
}