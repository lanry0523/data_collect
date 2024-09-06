package com.iwhalecloud.data.collect.dao;

import com.iwhalecloud.data.collect.domain.ConvertAmapCoordinate;
import com.iwhalecloud.data.collect.domain.SegMentInfo;
import com.iwhalecloud.data.collect.domain.SegStationInfo;
import com.iwhalecloud.data.collect.domain.StationRouteCorrelation;
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
    int batchInsert(@Param("list") List<SegStationInfo> list);
    /**
     * 批量新增
     *
     * @param listItm
     * @return
     */
    int batchInsertStation(List<StationRouteCorrelation> listItm);
    int batchInsertAmap(@Param("list") List<ConvertAmapCoordinate> list);
    int insertAmap(@Param("item") ConvertAmapCoordinate map);
    int selectCheckStation();
    int batchDelete();
    int batchDeleteCt();
    List<SegStationInfo> selectCheckList();

    List<ConvertAmapCoordinate> queryById(@Param("stationId") String stationId);
    List<ConvertAmapCoordinate> selectStationList();
    int selectStationCount();
    int batchDeleteAmapList();
}
