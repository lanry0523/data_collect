package com.iwhalecloud.data.collect.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StationPostion {
    /**
     * 经度
     */
    private BigDecimal Longitude;

    /**
     * 纬度
     */
    private BigDecimal Latitude;
}
