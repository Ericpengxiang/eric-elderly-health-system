package com.health.module.appointment.service.impl;

import com.health.common.BusinessException;
import com.health.common.enums.AppointmentStatusEnum;
import com.health.common.enums.RoleEnum;
import com.health.module.appointment.entity.Appointment;
import com.health.module.appointment.repository.AppointmentRepository;
import com.health.module.appointment.service.AppointmentService;
import com.health.module.user.repository.UserRepository;
import com.health.security.ElderAccessService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ElderAccessService elderAccessService;

    @Override
    @Transactional
    public Appointment create(Long elderId, Long doctorId, LocalDateTime appointTime,
                              String symptomDesc, String remark) {
        LoginUser login = SecurityUtils.requireLogin();
        if (login.getRole() == RoleEnum.ELDER) {
            elderId = login.getUserId();
        }
        elderAccessService.assertCanAccessElder(login, elderId);
        userRepository.findById(doctorId).filter(u -> u.getRole() == RoleEnum.DOCTOR)
                .orElseThrow(() -> new BusinessException("医生不存在"));
        Appointment a = new Appointment();
        a.setElderId(elderId);
        a.setDoctorId(doctorId);
        a.setAppointTime(appointTime);
        a.setStatus(AppointmentStatusEnum.PENDING);
        a.setSymptomDesc(symptomDesc);
        a.setRemark(remark);
        a.setCreateTime(LocalDateTime.now());
        return appointmentRepository.save(a);
    }

    @Override
    public List<Appointment> listForElder(Long elderId) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderId != null ? elderId : login.getUserId();
        elderAccessService.assertCanAccessElder(login, uid);
        return appointmentRepository.findByElderIdOrderByAppointTimeDesc(uid);
    }

    @Override
    public List<Appointment> listForDoctor(Long doctorId) {
        LoginUser login = SecurityUtils.requireLogin();
        if (login.getRole() != RoleEnum.DOCTOR && login.getRole() != RoleEnum.ADMIN) {
            throw new BusinessException(403, "仅医生可查看医生端预约列表");
        }
        Long did = doctorId != null ? doctorId : login.getUserId();
        if (login.getRole() == RoleEnum.DOCTOR && !Objects.equals(did, login.getUserId())) {
            throw new BusinessException(403, "不能查看其他医生预约");
        }
        return appointmentRepository.findByDoctorIdOrderByAppointTimeDesc(did);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, AppointmentStatusEnum status, String doctorAdvice) {
        LoginUser login = SecurityUtils.requireLogin();
        Appointment a = appointmentRepository.findById(id).orElseThrow(() -> new BusinessException("预约不存在"));
        if (login.getRole() == RoleEnum.DOCTOR && !a.getDoctorId().equals(login.getUserId())) {
            throw new BusinessException(403, "非本人预约");
        }
        if (login.getRole() == RoleEnum.ELDER) {
            throw new BusinessException(403, "老人账号不可修改预约状态");
        }
        a.setStatus(status);
        a.setDoctorAdvice(doctorAdvice);
    }
}
