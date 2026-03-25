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

    @Query("SELECT DATE(a.createTime) as date, COUNT(a) as count FROM AlertRecord a WHERE a.createTime >= :from GROUP BY DATE(a.createTime) ORDER BY DATE(a.createTime)")
    List<Object[]> countByDateAfter(@org.springframework.data.repository.query.Param("from") LocalDateTime from);

    @Query("SELECT a.alertType, COUNT(a) as count FROM AlertRecord a GROUP BY a.alertType")
    List<Object[]> countByAlertType();
}
