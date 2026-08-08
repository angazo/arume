package com.angazo.arume.db.persistence.mapper.generated;

import com.angazo.arume.db.persistence.model.T5LegalForms;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.Param;

public interface T5LegalFormsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    int deleteByPrimaryKey(@Param("countryAlpha2Code") String countryAlpha2Code, @Param("code") String code);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    int insert(T5LegalForms row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    int insertSelective(T5LegalForms row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    T5LegalForms selectByPrimaryKey(@Param("countryAlpha2Code") String countryAlpha2Code, @Param("code") String code);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    int updateByPrimaryKeySelective(T5LegalForms row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.t5_legal_forms")
    int updateByPrimaryKey(T5LegalForms row);
}