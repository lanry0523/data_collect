package com.iwhalecloud.data.collect.dao;

import com.iwhalecloud.data.collect.domain.RequestLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RequestLogDao {
    int insertRecord(@Param("requestLog") RequestLog requestLog);
}
