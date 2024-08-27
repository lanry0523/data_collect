package com.iwhalecloud.data.collect.dao;

import com.iwhalecloud.data.collect.domain.SegMentInfo;
import com.iwhalecloud.data.collect.domain.SegStationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SegStationInfoDao {

    int insertRecord(@Param("item") SegStationInfo segStationInfo);
    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    int batchInsert(List<SegStationInfo> list);
}
