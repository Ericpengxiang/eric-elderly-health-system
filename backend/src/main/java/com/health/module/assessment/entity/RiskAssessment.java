package com.health.module.assessment.entity;

import com.health.common.enums.RiskLevelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 慢性病风险评估
 */
@Getter
@Setter
@Entity
@Table(name = "risk_assessment")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "assessment_time", nullable = false)
    private LocalDateTime assessmentTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, columnDefinition = "ENUM('LOW','MEDIUM','HIGH')")
    private RiskLevelEnum riskLevel;

    @Column(name = "disease_risks", columnDefinition = "TEXT")
    private String diseaseRisks;

    @Column(name = "health_advice", columnDefinition = "TEXT")
    private String healthAdvice;

    @Column(name = "diet_advice", columnDefinition = "TEXT")
    private String dietAdvice;

    @Column(name = "exercise_advice", columnDefinition = "TEXT")
    private String exerciseAdvice;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "accuracy_score", precision = 4, scale = 2)
    private BigDecimal accuracyScore;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
