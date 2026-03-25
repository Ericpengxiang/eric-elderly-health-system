package com.health.module.vitalsign.repository;

import com.health.common.enums.SignTypeEnum;
import com.health.module.vitalsign.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    List<VitalSign> findByUserIdOrderByRecordTimeDesc(Long userId);

    @Query("SELECT v FROM VitalSign v WHERE v.userId = :uid AND v.recordTime >= :from ORDER BY v.recordTime ASC")
    List<VitalSign> findByUserIdAndRecordTimeAfter(@Param("uid") Long userId, @Param("from") LocalDateTime from);

    List<VitalSign> findByUserIdAndSignTypeOrderByRecordTimeDesc(Long userId, SignTypeEnum signType);

    long countByUserIdAndIsAbnormalAndRecordTimeAfter(Long userId, Integer abnormal, LocalDateTime from);
}
