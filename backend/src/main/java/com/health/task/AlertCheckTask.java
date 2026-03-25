package com.health.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务占位：可扩展为批量扫描未处理体征、重试通知等
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertCheckTask {

    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyHealthCheck() {
        log.debug("定时健康检查任务执行（当前为占位，不修改业务数据）");
    }
}
