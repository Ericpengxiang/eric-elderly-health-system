package com.health.module.family.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 家属与老年人绑定关系
 */
@Getter
@Setter
@Entity
@Table(name = "family_bind")
public class FamilyBind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(length = 30)
    private String relation;

    @Column(name = "bind_status", nullable = false)
    private Integer bindStatus = 1;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
