package com.angazo.arume.db.persistence.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.db.persistence.model.T2CountryNames;

@Mapper
public interface CountryCatalogQueryMapper {

    @Select("""
        SELECT c.alpha2_code AS country_alpha2_code,
               #{languageCode} AS language_code,
               COALESCE(requested.name, fallback.name) AS name
        FROM t1_countries c
        LEFT JOIN t2_country_names requested
               ON requested.country_alpha2_code = c.alpha2_code
              AND requested.language_code = #{languageCode}
        LEFT JOIN t2_country_names fallback
               ON fallback.country_alpha2_code = c.alpha2_code
              AND fallback.language_code = #{fallbackLanguageCode}
        WHERE COALESCE(requested.name, fallback.name) IS NOT NULL
        ORDER BY COALESCE(requested.name, fallback.name)
        """)
    List<T2CountryNames> selectCountriesByLanguage(
        @Param("languageCode") String languageCode,
        @Param("fallbackLanguageCode") String fallbackLanguageCode
    );

    @Select("""
        SELECT c.alpha2_code AS country_alpha2_code,
               #{languageCode} AS language_code,
               COALESCE(requested.name, fallback.name) AS name
        FROM t1_countries c
        LEFT JOIN t2_country_names requested
               ON requested.country_alpha2_code = c.alpha2_code
              AND requested.language_code = #{languageCode}
        LEFT JOIN t2_country_names fallback
               ON fallback.country_alpha2_code = c.alpha2_code
              AND fallback.language_code = #{fallbackLanguageCode}
        WHERE COALESCE(requested.name, fallback.name) IS NOT NULL
          AND EXISTS (
              SELECT 1 FROM t4_country_currency cc
              WHERE cc.country_alpha2_code = c.alpha2_code
          )
        ORDER BY COALESCE(requested.name, fallback.name)
        """)
    List<T2CountryNames> selectSupportedJurisdictionsByLanguage(
        @Param("languageCode") String languageCode,
        @Param("fallbackLanguageCode") String fallbackLanguageCode
    );
}
