package com.health.module.statistics.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    Map<String, Object> overview();

    /** 近7天预警趋势（按日期分组） */
    List<Map<String, Object>> alertTrend();

    /** 预警类型分布 */
    List<Map<String, Object>> alertTypeDistribution();
}
