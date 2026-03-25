package com.health.module.appointment.entity;

import com.health.common.enums.AppointmentStatusEnum;
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

import java.time.LocalDateTime;

/**
 * 预约记录
 */
@Getter
@Setter
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "appoint_time", nullable = false)
    private LocalDateTime appointTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED')")
    private AppointmentStatusEnum status = AppointmentStatusEnum.PENDING;

    @Column(name = "symptom_desc", columnDefinition = "TEXT")
    private String symptomDesc;

    @Column(name = "doctor_advice", columnDefinition = "TEXT")
    private String doctorAdvice;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
