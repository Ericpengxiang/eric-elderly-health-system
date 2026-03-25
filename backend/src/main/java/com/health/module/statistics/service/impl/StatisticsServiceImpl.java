package com.health.module.statistics.service.impl;

import com.health.common.enums.RoleEnum;
import com.health.module.alert.repository.AlertRecordRepository;
import com.health.module.user.repository.UserRepository;
import com.health.module.statistics.service.StatisticsService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final AlertRecordRepository alertRecordRepository;

    @Override
    public Map<String, Object> overview() {
        LoginUser login = SecurityUtils.requireLogin();
        Map<String, Object> m = new HashMap<>();
        LocalDateTime dayStart = LocalDate.now().atStartOfDay();
        if (login.getRole() == RoleEnum.ADMIN) {
            m.put("totalUsers", userRepository.count());
            m.put("elderCount", userRepository.countByRole(RoleEnum.ELDER));
            m.put("doctorCount", userRepository.countByRole(RoleEnum.DOCTOR));
            m.put("familyCount", userRepository.countByRole(RoleEnum.FAMILY));
            m.put("todayNewUsers", userRepository.countByCreateTimeAfter(dayStart));
            m.put("alertTotal", alertRecordRepository.count());
            m.put("unhandledAlerts", alertRecordRepository.countUnhandled());
        } else {
            m.put("message", "非管理员仅展示个人相关统计占位");
            m.put("role", login.getRole().name());
        }
        m.put("alertsLast24h", alertRecordRepository.countByCreateTimeAfter(LocalDateTime.now().minusHours(24)));
        return m;
    }

    @Override
    public List<Map<String, Object>> alertTrend() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        List<Object[]> rows = alertRecordRepository.countByDateAfter(from);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, Long> dateMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            String d = LocalDate.now().minusDays(i).format(fmt);
            dateMap.put(d, 0L);
        }
        for (Object[] row : rows) {
            if (row[0] != null) {
                try {
                    String fullDate = row[0].toString();
                    LocalDate ld = LocalDate.parse(fullDate.substring(0, 10));
                    String key = ld.format(fmt);
                    if (dateMap.containsKey(key)) {
                        dateMap.put(key, ((Number) row[1]).longValue());
                    }
                } catch (Exception ignored) {}
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        dateMap.forEach((date, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("count", count);
            result.add(item);
        });
        return result;
    }

    @Override
    public List<Map<String, Object>> alertTypeDistribution() {
        List<Object[]> rows = alertRecordRepository.countByAlertType();
        Map<String, String> typeLabels = new HashMap<>();
        typeLabels.put("BLOOD_PRESSURE", "血压异常");
        typeLabels.put("BLOOD_SUGAR", "血糖异常");
        typeLabels.put("HEART_RATE", "心率异常");
        typeLabels.put("TEMPERATURE", "体温异常");
        typeLabels.put("OXYGEN", "血氧异常");
        typeLabels.put("OTHER", "其他");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            String type = row[0] != null ? row[0].toString() : "OTHER";
            item.put("type", type);
            item.put("name", typeLabels.getOrDefault(type, type));
            item.put("value", ((Number) row[1]).longValue());
            result.add(item);
        }
        return result;
    }
}
