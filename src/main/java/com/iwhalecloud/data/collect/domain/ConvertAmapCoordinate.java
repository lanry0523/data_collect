package com.iwhalecloud.data.collect.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConvertAmapCoordinate {

    /**
     * 站点唯一编号
     */
    private String stationId;
    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;
}
