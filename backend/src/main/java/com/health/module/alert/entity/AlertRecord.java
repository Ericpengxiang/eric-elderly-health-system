package com.health.module.alert.entity;

import com.health.common.enums.AlertLevelEnum;
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
 * 预警记录
 */
@Getter
@Setter
@Entity
@Table(name = "alert_record")
public class AlertRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vital_sign_id")
    private Long vitalSignId;

    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_level", nullable = false, columnDefinition = "ENUM('LOW','MEDIUM','HIGH')")
    private AlertLevelEnum alertLevel;

    @Column(name = "alert_content", nullable = false, columnDefinition = "TEXT")
    private String alertContent;

    @Column(name = "notify_user_ids", columnDefinition = "TEXT")
    private String notifyUserIds;

    @Column(name = "is_read", nullable = false)
    private Integer isRead = 0;

    @Column(name = "is_handled", nullable = false)
    private Integer isHandled = 0;

    @Column(name = "handler_id")
    private Long handlerId;

    @Column(name = "handle_remark", columnDefinition = "TEXT")
    private String handleRemark;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
