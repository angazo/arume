package com.angazo.arume.es.persistence.mapper.generated;

import com.angazo.arume.es.persistence.model.Es3LegalForms;
import jakarta.annotation.Generated;
import org.apache.ibatis.annotations.Param;

public interface Es3LegalFormsMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es3_legal_forms")
    int deleteByPrimaryKey(@Param("code") String code, @Param("countryNumericCode") Short countryNumericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es3_legal_forms")
    int insert(Es3LegalForms row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es3_legal_forms")
    int insertSelective(Es3LegalForms row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es3_legal_forms")
    Es3LegalForms selectByPrimaryKey(@Param("code") String code, @Param("countryNumericCode") Short countryNumericCode);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es3_legal_forms")
    int updateByPrimaryKeySelective(Es3LegalForms row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: public.es3_legal_forms")
    int updateByPrimaryKey(Es3LegalForms row);
}