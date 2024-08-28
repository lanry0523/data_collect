package com.iwhalecloud.data.collect.util;
import com.iwhalecloud.data.collect.service.BusDataService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduledTasks {
    @Resource
    private BusDataService busDataService;
    @Scheduled(cron = "0 0/5 * * * ?") // 每10分钟执行一次
    public void reportCurrentTime() {
        busDataService.syncBusDataInfo();
        System.out.println("The time is now " + new java.util.Date());
    }
}

