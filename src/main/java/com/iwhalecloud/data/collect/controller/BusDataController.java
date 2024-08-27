package com.iwhalecloud.data.collect.controller;

import com.iwhalecloud.data.collect.domain.response.ResultResponse;
import com.iwhalecloud.data.collect.service.BusDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController("/")
public class BusDataController {
    @Resource
    private BusDataService busDataService;
    @ResponseBody
    @PostMapping("bus/getDataList")
    public ResultResponse getDataList() {
        log.info("调用getDataList请求");
        try {
            busDataService.syncBusDataInfo();
            return ResultResponse.succResult(1);
        } catch (Exception e) {
            log.info("异常", e);
            return ResultResponse.systemErrorResult("系统异常");
        }
    }
}
