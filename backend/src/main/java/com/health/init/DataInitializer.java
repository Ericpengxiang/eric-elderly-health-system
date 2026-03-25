package com.health.init;

import com.health.common.enums.AlertLevelEnum;
import com.health.common.enums.AppointmentStatusEnum;
import com.health.common.enums.RoleEnum;
import com.health.common.enums.SignTypeEnum;
import com.health.module.alert.entity.AlertRecord;
import com.health.module.alert.repository.AlertRecordRepository;
import com.health.module.appointment.entity.Appointment;
import com.health.module.appointment.repository.AppointmentRepository;
import com.health.module.assessment.entity.RiskAssessment;
import com.health.module.assessment.repository.RiskAssessmentRepository;
import com.health.module.family.entity.FamilyBind;
import com.health.module.family.repository.FamilyBindRepository;
import com.health.module.profile.entity.HealthProfile;
import com.health.module.profile.entity.PhysicalExam;
import com.health.module.profile.repository.HealthProfileRepository;
import com.health.module.profile.repository.PhysicalExamRepository;
import com.health.module.user.entity.SysUser;
import com.health.module.user.repository.UserRepository;
import com.health.module.vitalsign.entity.VitalSign;
import com.health.module.vitalsign.repository.VitalSignRepository;
import com.health.common.enums.RiskLevelEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 若数据库中尚无 admin，则插入与 init.sql 一致的演示数据（密码与测试账号说明一致）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HealthProfileRepository healthProfileRepository;
    private final FamilyBindRepository familyBindRepository;
    private final VitalSignRepository vitalSignRepository;
    private final AlertRecordRepository alertRecordRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PhysicalExamRepository physicalExamRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername("admin")) {
            log.info("检测到已有初始化数据，跳过 DataInitializer");
            return;
        }
        log.info("开始写入演示账号与业务数据...");
        LocalDateTime now = LocalDateTime.now();

        SysUser admin = user("admin", "admin123", "系统管理员", "13800000001", "admin@health.com", RoleEnum.ADMIN, now);
        SysUser zhang = user("zhangsan", "zhang123", "张三", "13800000002", "zhangsan@health.com", RoleEnum.ELDER, now);
        SysUser li = user("lisi", "lisi123", "李四", "13800000003", "lisi@health.com", RoleEnum.FAMILY, now);
        SysUser wang = user("wangwu", "wang123", "王五医生", "13800000004", "wangwu@health.com", RoleEnum.DOCTOR, now);
        SysUser zhao = user("zhaoliu", "zhao123", "赵六", "13800000005", "zhaoliu@health.com", RoleEnum.ELDER, now);
        SysUser sun = user("sunqi", "sun123", "孙七", "13800000006", "sunqi@health.com", RoleEnum.FAMILY, now);
        userRepository.save(admin);
        userRepository.save(zhang);
        userRepository.save(li);
        userRepository.save(wang);
        userRepository.save(zhao);
        userRepository.save(sun);

        zhang = userRepository.findByUsername("zhangsan").orElseThrow();
        zhao = userRepository.findByUsername("zhaoliu").orElseThrow();
        li = userRepository.findByUsername("lisi").orElseThrow();
        sun = userRepository.findByUsername("sunqi").orElseThrow();
        wang = userRepository.findByUsername("wangwu").orElseThrow();

        HealthProfile p1 = new HealthProfile();
        p1.setUserId(zhang.getId());
        p1.setIdCard("110101195001011234");
        p1.setBirthDate(LocalDate.of(1950, 1, 1));
        p1.setGender(1);
        p1.setBloodType("A");
        p1.setAddress("北京市朝阳区建国路88号");
        p1.setEmergencyContact("张小明");
        p1.setEmergencyPhone("13900000001");
        p1.setMedicalHistory("[\"高血压\",\"2型糖尿病\"]");
        p1.setAllergyHistory("青霉素过敏");
        p1.setCreateTime(now);
        healthProfileRepository.save(p1);

        HealthProfile p2 = new HealthProfile();
        p2.setUserId(zhao.getId());
        p2.setIdCard("110101194501011235");
        p2.setBirthDate(LocalDate.of(1945, 5, 15));
        p2.setGender(0);
        p2.setBloodType("B");
        p2.setAddress("北京市海淀区中关村大街1号");
        p2.setEmergencyContact("赵晓红");
        p2.setEmergencyPhone("13900000002");
        p2.setMedicalHistory("[\"冠心病\",\"骨质疏松\"]");
        p2.setAllergyHistory("无");
        p2.setCreateTime(now);
        healthProfileRepository.save(p2);

        FamilyBind fb1 = new FamilyBind();
        fb1.setElderId(zhang.getId());
        fb1.setFamilyId(li.getId());
        fb1.setRelation("子女");
        fb1.setBindStatus(1);
        fb1.setCreateTime(now);
        familyBindRepository.save(fb1);

        FamilyBind fb2 = new FamilyBind();
        fb2.setElderId(zhao.getId());
        fb2.setFamilyId(sun.getId());
        fb2.setRelation("配偶");
        fb2.setBindStatus(1);
        fb2.setCreateTime(now);
        familyBindRepository.save(fb2);

        int[][] bp = {{138, 85, 0}, {145, 92, 1}, {132, 80, 0}, {158, 98, 1}, {128, 78, 0},
                {135, 82, 0}, {142, 90, 1}, {130, 79, 0}, {136, 84, 0}};
        int[] bpDays = {29, 27, 25, 22, 20, 15, 10, 5, 1};
        for (int i = 0; i < bp.length; i++) {
            VitalSign v = new VitalSign();
            v.setUserId(zhang.getId());
            v.setSignType(SignTypeEnum.BLOOD_PRESSURE);
            v.setValueSystolic(BigDecimal.valueOf(bp[i][0]));
            v.setValueDiastolic(BigDecimal.valueOf(bp[i][1]));
            v.setUnit("mmHg");
            v.setRecordTime(now.minus(bpDays[i], ChronoUnit.DAYS));
            v.setIsAbnormal(bp[i][2]);
            v.setCreateTime(now);
            vitalSignRepository.save(v);
        }

        double[][] sugars = {{6.8, 0}, {7.5, 1}, {6.2, 0}, {8.1, 1}, {6.5, 0}, {7.2, 1}, {6.0, 0}};
        int[] sgDays = {28, 24, 20, 16, 12, 6, 2};
        for (int i = 0; i < sugars.length; i++) {
            VitalSign v = new VitalSign();
            v.setUserId(zhang.getId());
            v.setSignType(SignTypeEnum.BLOOD_SUGAR);
            v.setValueMain(BigDecimal.valueOf(sugars[i][0]));
            v.setUnit("mmol/L");
            v.setRecordTime(now.minus(sgDays[i], ChronoUnit.DAYS));
            v.setIsAbnormal((int) sugars[i][1]);
            v.setCreateTime(now);
            vitalSignRepository.save(v);
        }

        int[][] hrs = {{72, 0}, {105, 1}, {68, 0}, {78, 0}, {65, 0}, {112, 1}};
        int[] hrDays = {25, 22, 18, 14, 8, 3};
        for (int i = 0; i < hrs.length; i++) {
            VitalSign v = new VitalSign();
            v.setUserId(zhang.getId());
            v.setSignType(SignTypeEnum.HEART_RATE);
            v.setValueMain(BigDecimal.valueOf(hrs[i][0]));
            v.setUnit("bpm");
            v.setRecordTime(now.minus(hrDays[i], ChronoUnit.DAYS));
            v.setIsAbnormal(hrs[i][1]);
            v.setCreateTime(now);
            vitalSignRepository.save(v);
        }

        AlertRecord a1 = new AlertRecord();
        a1.setUserId(zhang.getId());
        a1.setAlertType("BLOOD_PRESSURE");
        a1.setAlertLevel(AlertLevelEnum.HIGH);
        a1.setAlertContent("血压异常：收缩压158mmHg，舒张压98mmHg，超出正常范围，请立即就医或联系家属！");
        a1.setIsRead(1);
        a1.setIsHandled(1);
        a1.setCreateTime(now.minus(22, ChronoUnit.DAYS));
        alertRecordRepository.save(a1);

        AlertRecord a2 = new AlertRecord();
        a2.setUserId(zhang.getId());
        a2.setAlertType("BLOOD_SUGAR");
        a2.setAlertLevel(AlertLevelEnum.MEDIUM);
        a2.setAlertContent("血糖偏高：空腹血糖8.1mmol/L，超出正常范围(3.9-7.0)，建议控制饮食并及时就诊。");
        a2.setIsRead(1);
        a2.setIsHandled(0);
        a2.setCreateTime(now.minus(16, ChronoUnit.DAYS));
        alertRecordRepository.save(a2);

        AlertRecord a3 = new AlertRecord();
        a3.setUserId(zhang.getId());
        a3.setAlertType("HEART_RATE");
        a3.setAlertLevel(AlertLevelEnum.MEDIUM);
        a3.setAlertContent("心率偏快：心率112bpm，超出正常范围(60-100)，请注意休息。");
        a3.setIsRead(0);
        a3.setIsHandled(0);
        a3.setCreateTime(now.minus(3, ChronoUnit.DAYS));
        alertRecordRepository.save(a3);

        RiskAssessment ra = new RiskAssessment();
        ra.setUserId(zhang.getId());
        ra.setAssessmentTime(now.minus(7, ChronoUnit.DAYS));
        ra.setRiskLevel(RiskLevelEnum.HIGH);
        ra.setDiseaseRisks("{\"高血压\":0.85,\"2型糖尿病\":0.78,\"心脑血管疾病\":0.62,\"肾功能损害\":0.45}");
        ra.setHealthAdvice("张三您好，根据您近期的体征数据和健康档案综合评估，您目前处于高风险状态。");
        ra.setDietAdvice("建议每日摄入食盐不超过5克...");
        ra.setExerciseAdvice("建议每天进行30分钟低强度有氧运动...");
        ra.setRawResponse("INIT_SQL_DEMO");
        ra.setAccuracyScore(BigDecimal.valueOf(0.82));
        ra.setCreateTime(now);
        riskAssessmentRepository.save(ra);

        Appointment ap1 = new Appointment();
        ap1.setElderId(zhang.getId());
        ap1.setDoctorId(wang.getId());
        ap1.setAppointTime(now.plus(2, ChronoUnit.DAYS));
        ap1.setStatus(AppointmentStatusEnum.CONFIRMED);
        ap1.setSymptomDesc("最近血压偏高，头晕头痛，需要复诊调整用药");
        ap1.setDoctorAdvice("已确认预约，请按时就诊，携带近期血压记录");
        ap1.setCreateTime(now);
        appointmentRepository.save(ap1);

        Appointment ap2 = new Appointment();
        ap2.setElderId(zhang.getId());
        ap2.setDoctorId(wang.getId());
        ap2.setAppointTime(now.minus(10, ChronoUnit.DAYS));
        ap2.setStatus(AppointmentStatusEnum.COMPLETED);
        ap2.setSymptomDesc("血糖控制不佳，需要复查");
        ap2.setDoctorAdvice("建议调整二甲双胍剂量，一个月后复查糖化血红蛋白");
        ap2.setCreateTime(now);
        appointmentRepository.save(ap2);

        Appointment ap3 = new Appointment();
        ap3.setElderId(zhao.getId());
        ap3.setDoctorId(wang.getId());
        ap3.setAppointTime(now.plus(5, ChronoUnit.DAYS));
        ap3.setStatus(AppointmentStatusEnum.PENDING);
        ap3.setSymptomDesc("膝关节疼痛，行走困难");
        ap3.setCreateTime(now);
        appointmentRepository.save(ap3);

        PhysicalExam e1 = new PhysicalExam();
        e1.setUserId(zhang.getId());
        e1.setExamDate(LocalDate.now().minusDays(90));
        e1.setHeight(BigDecimal.valueOf(168.0));
        e1.setWeight(BigDecimal.valueOf(72.5));
        e1.setBmi(BigDecimal.valueOf(25.7));
        e1.setVisionLeft(BigDecimal.valueOf(0.6));
        e1.setVisionRight(BigDecimal.valueOf(0.8));
        e1.setExamHospital("北京朝阳社区卫生服务中心");
        e1.setRemark("整体状况需关注，血压血糖偏高");
        e1.setCreateTime(now);
        physicalExamRepository.save(e1);

        PhysicalExam e2 = new PhysicalExam();
        e2.setUserId(zhao.getId());
        e2.setExamDate(LocalDate.now().minusDays(60));
        e2.setHeight(BigDecimal.valueOf(155.0));
        e2.setWeight(BigDecimal.valueOf(58.0));
        e2.setBmi(BigDecimal.valueOf(24.1));
        e2.setVisionLeft(BigDecimal.valueOf(0.4));
        e2.setVisionRight(BigDecimal.valueOf(0.5));
        e2.setExamHospital("北京海淀社区卫生服务中心");
        e2.setRemark("骨密度偏低，建议补钙");
        e2.setCreateTime(now);
        physicalExamRepository.save(e2);

        log.info("DataInitializer 完成：已创建 admin/zhangsan/lisi/wangwu/zhaoliu/sunqi 及演示业务数据");
    }

    private SysUser user(String username, String rawPassword, String realName, String phone, String email,
                         RoleEnum role, LocalDateTime now) {
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRealName(realName);
        u.setPhone(phone);
        u.setEmail(email);
        u.setRole(role);
        u.setStatus(1);
        u.setCreateTime(now);
        u.setUpdateTime(now);
        return u;
    }
}
