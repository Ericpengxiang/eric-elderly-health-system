package com.health.module.assessment.controller;

import com.health.common.Result;
import com.health.module.assessment.entity.RiskAssessment;
import com.health.module.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping("/run")
    public Result<RiskAssessment> run(@RequestParam(required = false) Long elderUserId) {
        return Result.ok(assessmentService.runAssessment(elderUserId));
    }

    @GetMapping
    public Result<List<RiskAssessment>> list(@RequestParam(required = false) Long elderUserId) {
        return Result.ok(assessmentService.list(elderUserId));
    }
}
