package com.angazo.arume.es.persistence.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.angazo.arume.es.persistence.model.Es2InvoiceSeriesFiscalYear;
import com.angazo.arume.es.persistence.model.typehandler.NumberingModeTypeHandler;

@Mapper
public interface InvoiceSeriesFiscalYearMapper {

    @Select("SELECT * FROM es2_invoice_series_fiscal_year WHERE series_id = #{seriesId} ORDER BY fiscal_year_id")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "series_id", property = "seriesId"),
        @Result(column = "fiscal_year_id", property = "fiscalYearId"),
        @Result(column = "numbering_mode", property = "numberingMode", typeHandler = NumberingModeTypeHandler.class),
        @Result(column = "active", property = "active"),
        @Result(column = "last_assigned_number", property = "lastAssignedNumber")
    })
    List<Es2InvoiceSeriesFiscalYear> selectStates(Long seriesId);

    @Delete("DELETE FROM es2_invoice_series_fiscal_year WHERE series_id = #{seriesId}")
    int deleteStates(Long seriesId);
}
