package com.hcp.common.core.utils;
import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Name: CustomStringTypeHandler
 * @Desc: 自定义mybatis处理类，将null返回为空串（‘’）
 * @Author: Administrator
 * @author dhr
 * @desc CustomStringTypeHandler
 * @date 2020/11/05
 */
@MappedTypes({String.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CustomStringTypeHandler extends BaseTypeHandler<String> {


    /**
     * 获取结果集中的字符串值，如果为 null 则返回空字符串。
     *
     * @param rs 结果集。
     * @param columnName 列名。
     * @return 结果字符串。
     * @throws ResultMapException 如果获取结果失败。
     */
    @Override
    public String getResult(ResultSet rs, String columnName) {
        String result;
        try {
            result = getNullableResult(rs, columnName);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
        }
        return result;
    }

    /**
     * 设置非空参数到 PreparedStatement。
     *
     * @param ps PreparedStatement 对象。
     * @param i 参数索引。
     * @param parameter 参数值。
     * @param jdbcType JDBC 类型。
     * @throws SQLException 如果 SQL 操作失败。
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter);
    }

    /**
     * 从结果集中获取可能为 null 的字符串结果，如果是 null 则返回空字符串。
     *
     * @param rs 结果集。
     * @param columnName 列名。
     * @return 结果字符串 (String)，不会为 null。
     * @throws SQLException 如果 SQL 操作失败。
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return rs.getString(columnName) == null? "" : rs.getString(columnName);
    }

    /**
     * 从结果集中获取可能为 null 的字符串结果，如果是 null 则返回空字符串。
     *
     * @param rs 结果集。
     * @param columnIndex 列索引。
     * @return 结果字符串 (String)，不会为 null。
     * @throws SQLException 如果 SQL 操作失败。
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return rs.getString(columnIndex) == null? "" : rs.getString(columnIndex);
    }

    /**
     * 从 CallableStatement 中获取可能为 null 的字符串结果，如果是 null 则返回空字符串。
     *
     * @param cs CallableStatement 对象。
     * @param columnIndex 列索引。
     * @return 结果字符串 (String)，不会为 null。
     * @throws SQLException 如果 SQL 操作失败。
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return cs.getString(columnIndex) == null? "" : cs.getString(columnIndex);
    }
}

