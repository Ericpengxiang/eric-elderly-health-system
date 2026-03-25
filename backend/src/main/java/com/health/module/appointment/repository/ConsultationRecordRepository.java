package com.health.module.appointment.repository;

import com.health.module.appointment.entity.ConsultationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationRecordRepository extends JpaRepository<ConsultationRecord, Long> {

    Optional<ConsultationRecord> findByAppointmentId(Long appointmentId);

    List<ConsultationRecord> findByDoctorIdOrderByCreateTimeDesc(Long doctorId);

    List<ConsultationRecord> findByElderIdOrderByCreateTimeDesc(Long elderId);
}
