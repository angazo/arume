package com.angazo.arume.es.persistence.mapper.custom;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.es.persistence.model.Es1InvoiceSeries;

@Mapper
public interface InvoiceSeriesMapper {

    @Select("SELECT * FROM es1_invoice_series WHERE company_id = #{companyId} ORDER BY code")
    List<Es1InvoiceSeries> selectByCompanyId(Long companyId);

    @Select("""
        SELECT COUNT(*) > 0
        FROM es1_invoice_series
        WHERE company_id = #{companyId} AND code = #{code}
        """)
    boolean existsByCompanyAndCode(@Param("companyId") Long companyId, @Param("code") String code);

}
