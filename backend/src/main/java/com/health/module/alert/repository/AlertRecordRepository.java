package com.health.module.alert.repository;

import com.health.module.alert.entity.AlertRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {

    List<AlertRecord> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<AlertRecord> findByIsHandledOrderByCreateTimeDesc(Integer isHandled);

    @Query("SELECT COUNT(a) FROM AlertRecord a WHERE a.isHandled = 0 AND a.handlerId IS NULL")
    long countUnhandled();

    long countByCreateTimeAfter(LocalDateTime from);
}
