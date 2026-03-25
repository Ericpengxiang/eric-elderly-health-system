package com.health.module.family.repository;

import com.health.module.family.entity.FamilyBind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyBindRepository extends JpaRepository<FamilyBind, Long> {

    List<FamilyBind> findByFamilyIdAndBindStatus(Long familyId, Integer bindStatus);

    List<FamilyBind> findByElderIdAndBindStatus(Long elderId, Integer bindStatus);

    Optional<FamilyBind> findByElderIdAndFamilyIdAndBindStatus(Long elderId, Long familyId, Integer bindStatus);

    boolean existsByElderIdAndFamilyIdAndBindStatus(Long elderId, Long familyId, Integer bindStatus);
}
