package com.health.module.vitalsign.entity;

import com.health.common.enums.SignTypeEnum;
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
 * 体征数据
 */
@Getter
@Setter
@Entity
@Table(name = "vital_sign")
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sign_type", nullable = false,
            columnDefinition = "ENUM('BLOOD_PRESSURE','BLOOD_SUGAR','HEART_RATE','BLOOD_OXYGEN','TEMPERATURE')")
    private SignTypeEnum signType;

    @Column(name = "value_systolic", precision = 6, scale = 2)
    private BigDecimal valueSystolic;

    @Column(name = "value_diastolic", precision = 6, scale = 2)
    private BigDecimal valueDiastolic;

    @Column(name = "value_main", precision = 8, scale = 2)
    private BigDecimal valueMain;

    @Column(length = 20)
    private String unit;

    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime;

    @Column(name = "device_source", length = 50)
    private String deviceSource;

    @Column(length = 255)
    private String remark;

    @Column(name = "is_abnormal", nullable = false)
    private Integer isAbnormal = 0;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
