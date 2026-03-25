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
import java.util.HashMap;
import java.util.Map;

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
}
