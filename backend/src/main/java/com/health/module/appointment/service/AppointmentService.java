package com.health.module.appointment.service;

import com.health.common.enums.AppointmentStatusEnum;
import com.health.module.appointment.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment create(Long elderId, Long doctorId, java.time.LocalDateTime appointTime,
                       String symptomDesc, String remark);

    List<Appointment> listForElder(Long elderId);

    List<Appointment> listForDoctor(Long doctorId);

    void updateStatus(Long id, AppointmentStatusEnum status, String doctorAdvice);
}
