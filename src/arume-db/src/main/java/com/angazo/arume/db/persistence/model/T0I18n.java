package com.angazo.arume.db.persistence.model;

import jakarta.annotation.Generated;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class T0I18n implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t0_i18n.language_code")
    private String languageCode;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: public.t0_i18n.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    private static final long serialVersionUID = 1L;
}