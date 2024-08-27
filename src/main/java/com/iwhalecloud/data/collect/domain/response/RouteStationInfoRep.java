package com.iwhalecloud.data.collect.domain.response;


import com.iwhalecloud.data.collect.domain.SegMentInfo;
import lombok.Data;

import java.util.List;


@Data
public class RouteStationInfoRep {
    /**
     * 线路ID
     */
    private Integer RouteID;

    /**
     * 线路名称
     */
    private String RouteName;

    /**
     * 线路类型
     */
    private String RouteType;

    /**
     * 是否经过合并（1：是，0：否）
     */
    private String RsHaveSubRouteCombine;

    /**
     * 线路名称扩展
     */
    private String RouteNameExt;

    /**
     * 线路备注
     */
    private String RouteMemo;

    /**
     * 是否主子线（1 是，2 无法合并的子线，0 可以合并的子线）
     */
    private String Ismainsub;

    /**
     * 是否快车
     */
    private String IsBRT;

    private List<SegMentInfoRep> SegmentList;
}