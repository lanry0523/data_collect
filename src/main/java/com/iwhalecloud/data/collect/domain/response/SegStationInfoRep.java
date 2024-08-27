package com.iwhalecloud.data.collect.domain.response;

import com.iwhalecloud.data.collect.domain.StationPostion;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 线路表
 * @TableName seg_station_info
 */
@Data
public class SegStationInfoRep {
    /**
     * 站点唯一编号
     */
    private String StationID;

    /**
     * 站点名称
     */
    private String StationName;

    /**
     * 单程 ID
     */
    private Integer SegmentId;

    /**
     * 站点序号
     */
    private Integer StationNO;

    private StationPostion StationPostion;

    /**
     * 站点描述信息
     */
    private String StationMEMO;

    /**
     * 速度
     */
    private String Speed;

    /**
     * 双程号
     */
    private Integer DualSerial;

    /**
     * 站点排序
     */
    private Integer StationSort;


}