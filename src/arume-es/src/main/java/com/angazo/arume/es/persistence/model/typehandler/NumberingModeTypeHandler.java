package com.angazo.arume.es.persistence.model.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.angazo.arume.es.logic.invoice.series.NumberingMode;

@MappedTypes(NumberingMode.class)
@MappedJdbcTypes(value = JdbcType.SMALLINT, includeNullJdbcType = true)
public class NumberingModeTypeHandler extends BaseTypeHandler<NumberingMode> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NumberingMode parameter, JdbcType jdbcType)
        throws SQLException {
        ps.setShort(i, parameter.code());
    }

    @Override
    public NumberingMode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        var value = rs.getShort(columnName);
        return rs.wasNull() ? null : NumberingMode.fromCode(value);
    }

    @Override
    public NumberingMode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        var value = rs.getShort(columnIndex);
        return rs.wasNull() ? null : NumberingMode.fromCode(value);
    }

    @Override
    public NumberingMode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        var value = cs.getShort(columnIndex);
        return cs.wasNull() ? null : NumberingMode.fromCode(value);
    }
}
