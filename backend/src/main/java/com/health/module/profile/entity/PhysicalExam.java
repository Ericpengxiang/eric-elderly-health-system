package com.health.module.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检记录
 */
@Getter
@Setter
@Entity
@Table(name = "physical_exam")
public class PhysicalExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;

    @Column(name = "vision_left", precision = 4, scale = 2)
    private BigDecimal visionLeft;

    @Column(name = "vision_right", precision = 4, scale = 2)
    private BigDecimal visionRight;

    @Column(name = "exam_hospital", length = 100)
    private String examHospital;

    @Column(name = "exam_report", columnDefinition = "TEXT")
    private String examReport;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
