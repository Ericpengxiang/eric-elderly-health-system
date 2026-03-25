package com.health.module.appointment.repository;

import com.health.common.enums.AppointmentStatusEnum;
import com.health.module.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByElderIdOrderByAppointTimeDesc(Long elderId);

    List<Appointment> findByDoctorIdOrderByAppointTimeDesc(Long doctorId);

    List<Appointment> findByDoctorIdAndStatusAndAppointTimeBetweenOrderByAppointTimeAsc(
            Long doctorId, AppointmentStatusEnum status, LocalDateTime start, LocalDateTime end);

    long countByDoctorIdAndStatusAndAppointTimeBetween(
            Long doctorId, AppointmentStatusEnum status, LocalDateTime start, LocalDateTime end);
}
