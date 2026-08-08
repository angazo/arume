package com.angazo.arume.db.persistence.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.db.persistence.model.T5LegalForms;

@Mapper
public interface LegalFormCatalogQueryMapper {

    @Select("""
        SELECT * FROM t5_legal_forms
        WHERE country_alpha2_code = #{countryAlpha2Code}
        ORDER BY description
        """)
    List<T5LegalForms> selectByCountry(@Param("countryAlpha2Code") String countryAlpha2Code);
}
