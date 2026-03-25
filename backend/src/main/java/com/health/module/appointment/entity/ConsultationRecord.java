package com.health.module.appointment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 问诊记录
 */
@Getter
@Setter
@Entity
@Table(name = "consultation_record")
public class ConsultationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
