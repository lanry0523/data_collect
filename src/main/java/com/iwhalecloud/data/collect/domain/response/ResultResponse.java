package com.iwhalecloud.data.collect.domain.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 返回接口实体类
 * @param
 */
@Data
public class ResultResponse {
    @JSONField(name = "resultCode")
    private String resultCode;
    @JSONField(name = "resultMsg")
    private String resultMsg;
    @JSONField(name = "data")
    private Integer data;


    public static ResultResponse succResult(Integer data) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResultCode("200");
        resultResponse.setResultMsg("OK");
        resultResponse.setData(data);
        return resultResponse;
    }

    public static ResultResponse systemErrorResult(String msg) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResultCode("500");
        resultResponse.setResultMsg(msg);
        resultResponse.setData(0);
        return resultResponse;
    }

}
