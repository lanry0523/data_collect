package com.iwhalecloud.data.collect.dao;


import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RouteStationInfoDao {

    int insertRecord(@Param("item") RouteStationInfo routeStationInfo);
}
