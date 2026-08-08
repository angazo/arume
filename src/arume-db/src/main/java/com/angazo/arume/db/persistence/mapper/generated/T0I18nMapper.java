package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T0I18n;
import jakarta.annotation.Generated;

public interface T0I18nMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    int deleteByPrimaryKey(String languageCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    int insert(T0I18n row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    int insertSelective(T0I18n row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    T0I18n selectByPrimaryKey(String languageCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    int updateByPrimaryKeySelective(T0I18n row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t0_i18n")
    int updateByPrimaryKey(T0I18n row);
}