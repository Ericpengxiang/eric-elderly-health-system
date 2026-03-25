package com.health.module.assessment.service;

import com.health.module.assessment.entity.RiskAssessment;

import java.util.List;

public interface AssessmentService {

    RiskAssessment runAssessment(Long elderUserId);

    List<RiskAssessment> list(Long elderUserId);
}
