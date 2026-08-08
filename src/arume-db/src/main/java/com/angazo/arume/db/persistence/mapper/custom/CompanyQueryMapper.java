package com.angazo.arume.db.persistence.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.db.persistence.model.T6Companies;
import com.angazo.arume.db.persistence.model.T7CompanyProfiles;
import com.angazo.arume.db.persistence.model.T8CompanyTaxRegistrations;

@Mapper
public interface CompanyQueryMapper {

    @Select("SELECT * FROM t6_companies ORDER BY created_at, id")
    List<T6Companies> selectAllCompanies();

    @Select("""
        SELECT *
        FROM t6_companies
        WHERE primary_fiscal_jurisdiction = #{jurisdiction}
          AND primary_fiscal_id = #{fiscalId}
        """)
    T6Companies selectByFiscalIdentification(
        @Param("jurisdiction") String jurisdiction,
        @Param("fiscalId") String fiscalId
    );

    @Select("SELECT * FROM t7_company_profiles WHERE company_id = #{companyId} ORDER BY valid_from")
    List<T7CompanyProfiles> selectProfiles(Long companyId);

    @Select("SELECT * FROM t8_company_tax_registrations WHERE company_id = #{companyId} ORDER BY valid_from")
    List<T8CompanyTaxRegistrations> selectTaxRegistrations(Long companyId);

    @Delete("DELETE FROM t7_company_profiles WHERE company_id = #{companyId}")
    int deleteProfiles(Long companyId);

    @Delete("DELETE FROM t8_company_tax_registrations WHERE company_id = #{companyId}")
    int deleteTaxRegistrations(Long companyId);
}
