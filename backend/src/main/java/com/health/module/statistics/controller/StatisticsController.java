package com.health.module.statistics.controller;

import com.health.common.Result;
import com.health.module.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.ok(statisticsService.overview());
    }

    @GetMapping("/alert-trend")
    public Result<List<Map<String, Object>>> alertTrend() {
        return Result.ok(statisticsService.alertTrend());
    }

    @GetMapping("/alert-type-distribution")
    public Result<List<Map<String, Object>>> alertTypeDistribution() {
        return Result.ok(statisticsService.alertTypeDistribution());
    }
}
