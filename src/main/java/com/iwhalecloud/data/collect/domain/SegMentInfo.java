package com.iwhalecloud.data.collect.domain;

import lombok.Data;

import java.util.Date;

/**
 * 线路表
 * @TableName seg_ment_info
 */
@Data
public class SegMentInfo{
    /**
     * 单程ID
     */
    private Integer segmentId;

    /**
     * 单程名称
     */
    private String segmentName;

    /**
     * 线路ID
     */
    private Integer routeId;

    /**
     * 首班时间
     */
    private Date firstTime;

    /**
     * 末班时间
     */
    private Date lastTime;

    /**
     * 票价
     */
    private Integer routePrice;

    /**
     * 平峰发车间隔
     */
    private Integer normalTimeSpan;

    /**
     * 高峰时段发车间隔
     */
    private Integer peakTimeSpan;

    /**
     * 首末班描述
     */
    private String firtLastShiftInfo;

    /**
     * 首末班描述2
     */
    private String firtLastShiftInfo2;

    /**
     * 备注
     */
    private String memos;

    /**
     * 运行方向
     */
    private String runDirection;

    /**
     * 线路 ID（百度地图）
     */
    private String baiduMapId;

    /**
     * 线路 ID（高德地图）
     */
    private String amapId;

    /**
     * 线 路 绘 制 方 式0：公交数据 1：地图数据
     */
    private String drawType;

}