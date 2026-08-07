package com.angazo.arume.es.persistence.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.es.persistence.model.Es3LegalForms;

@Mapper
public interface LegalFormsMapper {

    @Select("""
        SELECT * FROM es3_legal_forms
        WHERE country_numeric_code = #{countryNumericCode}
          AND is_legal_person = #{isLegalPerson}
        ORDER BY description
        """)
    List<Es3LegalForms> selectByCountryNumericCodeAndLegalPerson(
        @Param("countryNumericCode") Short countryNumericCode,
        @Param("isLegalPerson") Boolean isLegalPerson
    );

}
