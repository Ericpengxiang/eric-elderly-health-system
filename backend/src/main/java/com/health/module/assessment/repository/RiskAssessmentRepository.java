package com.health.module.assessment.repository;

import com.health.module.assessment.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByUserIdOrderByAssessmentTimeDesc(Long userId);

    Optional<RiskAssessment> findFirstByUserIdOrderByAssessmentTimeDesc(Long userId);
}
