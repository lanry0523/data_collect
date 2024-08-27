package com.iwhalecloud.data.collect.dao;


import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RouteStationInfoDao {

    int insertRecord(@Param("item") RouteStationInfo routeStationInfo);
    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    int batchInsert(List<RouteStationInfo> list);
}
