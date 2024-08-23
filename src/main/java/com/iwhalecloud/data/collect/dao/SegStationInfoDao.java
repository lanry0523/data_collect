package com.iwhalecloud.data.collect.dao;

import com.iwhalecloud.data.collect.domain.SegStationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SegStationInfoDao {

    int insertRecord(@Param("item") SegStationInfo segStationInfo);
}
