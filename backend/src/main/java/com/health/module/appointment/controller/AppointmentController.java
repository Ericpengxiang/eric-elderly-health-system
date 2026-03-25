package com.health.module.appointment.controller;

import com.health.common.Result;
import com.health.common.enums.AppointmentStatusEnum;
import com.health.module.appointment.entity.Appointment;
import com.health.module.appointment.service.AppointmentService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public Result<Appointment> create(@RequestBody CreateAppointmentReq req) {
        return Result.ok(appointmentService.create(
                req.getElderId(), req.getDoctorId(), req.getAppointTime(),
                req.getSymptomDesc(), req.getRemark()));
    }

    @GetMapping("/elder")
    public Result<List<Appointment>> listElder(@RequestParam(required = false) Long elderUserId) {
        return Result.ok(appointmentService.listForElder(elderUserId));
    }

    @GetMapping("/doctor")
    public Result<List<Appointment>> listDoctor(@RequestParam(required = false) Long doctorId) {
        return Result.ok(appointmentService.listForDoctor(doctorId));
    }

    @PutMapping("/{id}/status")
    public Result<Void> status(@PathVariable Long id,
                               @RequestParam AppointmentStatusEnum status,
                               @RequestParam(required = false) String doctorAdvice) {
        appointmentService.updateStatus(id, status, doctorAdvice);
        return Result.ok(null);
    }

    @Data
    public static class CreateAppointmentReq {
        private Long elderId;
        private Long doctorId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime appointTime;
        private String symptomDesc;
        private String remark;
    }
}
