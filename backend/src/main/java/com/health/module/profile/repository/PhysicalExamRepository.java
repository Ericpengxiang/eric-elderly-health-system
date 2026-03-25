package com.health.module.profile.repository;

import com.health.module.profile.entity.PhysicalExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhysicalExamRepository extends JpaRepository<PhysicalExam, Long> {

    List<PhysicalExam> findByUserIdOrderByExamDateDesc(Long userId);
}
