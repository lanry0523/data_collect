package com.iwhalecloud.data.collect.domain.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 线路表
 * @TableName seg_ment_info
 */
@Data
public class SegMentInfoRep {
    /**
     * 单程ID
     */
    private Integer SegmentID;

    /**
     * 单程名称
     */
    private String SegmentName;

    /**
     * 线路ID
     */
    private Integer RouteID;

    /**
     * 首班时间
     */
    private Date FirstTime;

    /**
     * 末班时间
     */
    private Date LastTime;

    /**
     * 票价
     */
    private Integer RoutePrice;

    /**
     * 平峰发车间隔
     */
    private Integer NormalTimeSpan;

    /**
     * 高峰时段发车间隔
     */
    private Integer PeakTimeSpan;

    /**
     * 首末班描述
     */
    private String FirtLastShiftInfo;

    /**
     * 首末班描述2
     */
    private String FirtLastShiftInfo2;

    /**
     * 备注
     */
    private String Memos;

    /**
     * 运行方向
     */
    private String RunDirection;

    /**
     * 线路 ID（百度地图）
     */
    private String Baidumapid;

    /**
     * 线路 ID（高德地图）
     */
    private String Amapid;

    /**
     * 线 路 绘 制 方 式0：公交数据 1：地图数据
     */
    private String DrawType;

    private List<SegStationInfoRep> StationList;
}