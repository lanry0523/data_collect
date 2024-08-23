package com.iwhalecloud.data.collect.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;


@Data
public class RouteStationInfo {
    /**
     * 线路ID
     */
    private Integer routeId;

    /**
     * 线路名称
     */
    private String routeName;

    /**
     * 线路类型
     */
    private String routeType;

    /**
     * 是否经过合并（1：是，0：否）
     */
    private String isHaveSubRouteCombine;

    /**
     * 线路名称扩展
     */
    private String routeNameExt;

    /**
     * 线路备注
     */
    private String routeMemo;

    /**
     * 是否主子线（1 是，2 无法合并的子线，0 可以合并的子线）
     */
    private String isMainSub;

    /**
     * 是否快车
     */
    private String isBrt;


}