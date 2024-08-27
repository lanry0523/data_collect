package com.iwhalecloud.data.collect.domain;

import lombok.Data;

@Data
public class StationRouteCorrelation {
    /**
     * 线路ID
     */
    private Integer routeId;
    /**
     * 单程ID
     */
    private Integer segmentId;
    /**
     * 站点唯一编号
     */
    private String stationId;

    /**
     * 站点排序
     */
    private Integer stationSort;
}
