package com.health.module.profile.controller;

import com.health.common.Result;
import com.health.module.profile.entity.PhysicalExam;
import com.health.module.profile.repository.PhysicalExamRepository;
import com.health.security.ElderAccessService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/physical-exams")
@RequiredArgsConstructor
public class PhysicalExamController {

    private final PhysicalExamRepository physicalExamRepository;
    private final ElderAccessService elderAccessService;

    @GetMapping
    public Result<List<PhysicalExam>> list(@RequestParam(required = false) Long elderUserId) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderUserId != null ? elderUserId : login.getUserId();
        elderAccessService.assertCanAccessElder(login, uid);
        return Result.ok(physicalExamRepository.findByUserIdOrderByExamDateDesc(uid));
    }
}
