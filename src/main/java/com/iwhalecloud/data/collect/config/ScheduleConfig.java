package com.iwhalecloud.data.collect.config;

import com.iwhalecloud.data.collect.service.BusDataService;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Data
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Value("${schedule.cron}")
    private String cron;

    @Resource
    private BusDataService busDataService;
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 动态使用cron表达式设置循环间隔
        taskRegistrar.addTriggerTask(() -> busDataService.syncBusDataInfo() ,triggerContext -> {
            // 使用CronTrigger触发器，可动态修改cron表达式来操作循环规则
            CronTrigger cronTrigger = new CronTrigger(cron);
            Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
            return nextExecutionTime;
        });
    }
}
