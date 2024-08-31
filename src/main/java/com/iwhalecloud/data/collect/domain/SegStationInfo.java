package com.iwhalecloud.data.collect.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @TableName seg_station_info
 */
@Data
public class SegStationInfo {
    /**
     * 站点唯一编号
     */
    private String stationId;

    /**
     * 站点名称
     */
    private String stationName;

    /**
     * 单程 ID
     */
    private Integer segmentId;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 站点描述信息
     */
    private String stationMemo;

    /**
     * 速度
     */
    private String speed;

    /**
     * 双程号
     */
    private Integer dualSerial;

    /**
     * 单程站点序号排序
     */
    private Integer sngserialId;


}