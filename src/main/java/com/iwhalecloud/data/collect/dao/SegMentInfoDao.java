package com.iwhalecloud.data.collect.dao;

import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import com.iwhalecloud.data.collect.domain.SegMentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SegMentInfoDao {

    int insertRecord(@Param("item") SegMentInfo segMentInfo);
    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    int batchInsert(List<SegMentInfo> list);
}
