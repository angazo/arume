package com.angazo.arume.db.persistence.mapper.custom;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.db.persistence.model.T9FiscalYears;

@Mapper
public interface FiscalYearQueryMapper {

    @Select("SELECT * FROM t9_fiscal_years WHERE company_id = #{companyId} ORDER BY start_date")
    List<T9FiscalYears> selectByCompanyId(Long companyId);

    @Select("""
        SELECT COUNT(*) > 0
        FROM t9_fiscal_years
        WHERE company_id = #{companyId}
          AND start_date <= #{endDate}
          AND end_date >= #{startDate}
        """)
    boolean existsOverlapping(
        @Param("companyId") Long companyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
