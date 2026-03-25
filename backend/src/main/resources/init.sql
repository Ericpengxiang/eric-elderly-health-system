-- ============================================================
-- 文件：init.sql
-- 路径：backend/src/main/resources/init.sql
-- 说明：完整建表 + 初始化数据（密码为 BCrypt，与 DataInitializer 需保持一致）
-- ============================================================

CREATE DATABASE IF NOT EXISTS elderly_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE elderly_health;

-- 用户表（四类角色）
CREATE TABLE IF NOT EXISTS sys_user (
  id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(50)  UNIQUE NOT NULL          COMMENT '登录用户名',
  password      VARCHAR(255) NOT NULL                 COMMENT 'BCrypt 加密密码',
  real_name     VARCHAR(50)                           COMMENT '真实姓名',
  phone         VARCHAR(20)                           COMMENT '手机号',
  email         VARCHAR(100)                          COMMENT '邮箱',
  role          ENUM('ELDER','FAMILY','DOCTOR','ADMIN') NOT NULL COMMENT '角色',
  avatar        VARCHAR(255)                          COMMENT '头像 URL',
  status        INT          DEFAULT 1                COMMENT '1启用 0禁用',
  create_time   DATETIME     DEFAULT NOW(),
  update_time   DATETIME     DEFAULT NOW() ON UPDATE NOW()
) COMMENT '系统用户表';

-- 老年人健康档案表
CREATE TABLE IF NOT EXISTS health_profile (
  id                BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id           BIGINT NOT NULL                    COMMENT '关联老年人用户ID',
  id_card           VARCHAR(18)                        COMMENT '身份证号',
  birth_date        DATE                               COMMENT '出生日期',
  gender            INT                                COMMENT '0女 1男',
  blood_type        VARCHAR(5)                         COMMENT '血型',
  address           TEXT                               COMMENT '家庭住址',
  emergency_contact VARCHAR(50)                        COMMENT '紧急联系人',
  emergency_phone   VARCHAR(20)                        COMMENT '紧急联系电话',
  medical_history   TEXT                               COMMENT '既往病史（JSON数组）',
  allergy_history   TEXT                               COMMENT '过敏史',
  create_time       DATETIME DEFAULT NOW(),
  FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT '健康档案表';

-- 体检记录表
CREATE TABLE IF NOT EXISTS physical_exam (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id        BIGINT NOT NULL,
  exam_date      DATE NOT NULL                         COMMENT '体检日期',
  height         DECIMAL(5,1)                          COMMENT '身高(cm)',
  weight         DECIMAL(5,1)                          COMMENT '体重(kg)',
  bmi            DECIMAL(4,1)                          COMMENT 'BMI',
  vision_left    DECIMAL(4,2)                          COMMENT '左眼视力',
  vision_right   DECIMAL(4,2)                          COMMENT '右眼视力',
  exam_hospital  VARCHAR(100)                          COMMENT '体检医院',
  exam_report    TEXT                                  COMMENT '体检报告描述',
  remark         TEXT                                  COMMENT '备注',
  create_time    DATETIME DEFAULT NOW()
) COMMENT '体检记录表';

-- 体征数据表
CREATE TABLE IF NOT EXISTS vital_sign (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id          BIGINT NOT NULL,
  sign_type        ENUM('BLOOD_PRESSURE','BLOOD_SUGAR','HEART_RATE','BLOOD_OXYGEN','TEMPERATURE') NOT NULL,
  value_systolic   DECIMAL(6,2)                       COMMENT '收缩压(mmHg)',
  value_diastolic  DECIMAL(6,2)                       COMMENT '舒张压(mmHg)',
  value_main       DECIMAL(8,2)                       COMMENT '主值（血糖/心率/血氧/体温）',
  unit             VARCHAR(20)                        COMMENT '单位',
  record_time      DATETIME NOT NULL                  COMMENT '记录时间',
  device_source    VARCHAR(50)                        COMMENT '数据来源设备',
  remark           VARCHAR(255),
  is_abnormal      INT DEFAULT 0                      COMMENT '0正常 1异常',
  create_time      DATETIME DEFAULT NOW()
) COMMENT '体征数据表';

-- 预警记录表
CREATE TABLE IF NOT EXISTS alert_record (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id         BIGINT NOT NULL                     COMMENT '被预警老年人ID',
  vital_sign_id   BIGINT                              COMMENT '触发预警的体征记录ID',
  alert_type      VARCHAR(50) NOT NULL                COMMENT '预警类型',
  alert_level     ENUM('LOW','MEDIUM','HIGH') NOT NULL COMMENT '预警等级',
  alert_content   TEXT NOT NULL                       COMMENT '预警内容',
  notify_user_ids TEXT                                COMMENT '已通知用户ID列表(JSON)',
  is_read         INT DEFAULT 0,
  is_handled      INT DEFAULT 0,
  handler_id      BIGINT,
  handle_remark   TEXT,
  create_time     DATETIME DEFAULT NOW()
) COMMENT '预警记录表';

-- 慢性病风险评估表
CREATE TABLE IF NOT EXISTS risk_assessment (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id          BIGINT NOT NULL,
  assessment_time  DATETIME NOT NULL,
  risk_level       ENUM('LOW','MEDIUM','HIGH') NOT NULL,
  disease_risks    TEXT                               COMMENT '各疾病风险评分(JSON)',
  health_advice    TEXT                               COMMENT '综合健康建议',
  diet_advice      TEXT                               COMMENT '饮食建议',
  exercise_advice  TEXT                               COMMENT '运动建议',
  raw_response     TEXT                               COMMENT '第三方接口原始返回',
  accuracy_score   DECIMAL(4,2)                       COMMENT '评估准确率',
  create_time      DATETIME DEFAULT NOW()
) COMMENT '风险评估表';

-- 家属绑定表
CREATE TABLE IF NOT EXISTS family_bind (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  elder_id     BIGINT NOT NULL,
  family_id    BIGINT NOT NULL,
  relation     VARCHAR(30)                            COMMENT '关系：子女/配偶/兄弟姐妹等',
  bind_status  INT DEFAULT 1                          COMMENT '1绑定 0解绑',
  create_time  DATETIME DEFAULT NOW()
) COMMENT '家属绑定关系表';

-- 预约记录表
CREATE TABLE IF NOT EXISTS appointment (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  elder_id      BIGINT NOT NULL,
  doctor_id     BIGINT NOT NULL,
  appoint_time  DATETIME NOT NULL,
  status        ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
  symptom_desc  TEXT                                  COMMENT '症状描述',
  doctor_advice TEXT                                  COMMENT '医生建议',
  remark        TEXT,
  create_time   DATETIME DEFAULT NOW()
) COMMENT '预约记录表';

-- 问诊记录表
CREATE TABLE IF NOT EXISTS consultation_record (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  appointment_id  BIGINT NOT NULL,
  elder_id        BIGINT NOT NULL,
  doctor_id       BIGINT NOT NULL,
  diagnosis       TEXT                                COMMENT '诊断结果',
  prescription    TEXT                                COMMENT '处方/用药建议',
  follow_up_date  DATE                                COMMENT '复诊日期',
  create_time     DATETIME DEFAULT NOW()
) COMMENT '问诊记录表';

-- ============================================================
-- 初始化测试数据（BCrypt，与统一测试账号对应）
-- admin123 / zhang123 / lisi123 / wang123 / zhao123 / sun123
-- ============================================================

INSERT INTO sys_user (username, password, real_name, phone, email, role, status) VALUES
('admin',    '$2b$10$uVd8EGz213oeUs9gfBb1Megm6BH2wiHD6vDfDZP0mS48yEae0GsO2', '系统管理员', '13800000001', 'admin@health.com',    'ADMIN',  1),
('zhangsan', '$2b$10$3pCHw9I1j1Mkq2uRb55u1.ccasK9UDdulPagCCR/0PpLOMmkFuKAe', '张三',     '13800000002', 'zhangsan@health.com', 'ELDER',  1),
('lisi',     '$2b$10$3S1b4Tz66EEs1bJ2q37ytuEAjfgh3LhQGTCEVPylXU3b.GKrayx3S', '李四',     '13800000003', 'lisi@health.com',     'FAMILY', 1),
('wangwu',   '$2b$10$ZJQ7Dbofd9u.Yk8seh8ATe0oE3xcwMN7hlZclTgJgbouxZfFwQppi', '王五医生', '13800000004', 'wangwu@health.com',   'DOCTOR', 1),
('zhaoliu',  '$2b$10$jcblU1mMgFWsGBo3e7Gky.Fvg.8olr5AmSU8eWknZ42SfSsyfGTNC', '赵六',     '13800000005', 'zhaoliu@health.com',  'ELDER',  1),
('sunqi',    '$2b$10$Z8ifOcQOFdnqc7JfqyqZM.rR.R2PMZgBtsu0mTK0sf134tRHIkU8K', '孙七',     '13800000006', 'sunqi@health.com',    'FAMILY', 1);

INSERT INTO health_profile (user_id, id_card, birth_date, gender, blood_type, address, emergency_contact, emergency_phone, medical_history, allergy_history) VALUES
(2, '110101195001011234', '1950-01-01', 1, 'A', '北京市朝阳区建国路88号', '张小明', '13900000001', '["高血压","2型糖尿病"]', '青霉素过敏'),
(5, '110101194501011235', '1945-05-15', 0, 'B', '北京市海淀区中关村大街1号', '赵晓红', '13900000002', '["冠心病","骨质疏松"]', '无');

INSERT INTO family_bind (elder_id, family_id, relation, bind_status) VALUES
(2, 3, '子女', 1),
(5, 6, '配偶', 1);

INSERT INTO vital_sign (user_id, sign_type, value_systolic, value_diastolic, unit, record_time, is_abnormal) VALUES
(2, 'BLOOD_PRESSURE', 138, 85, 'mmHg', DATE_SUB(NOW(), INTERVAL 29 DAY), 0),
(2, 'BLOOD_PRESSURE', 145, 92, 'mmHg', DATE_SUB(NOW(), INTERVAL 27 DAY), 1),
(2, 'BLOOD_PRESSURE', 132, 80, 'mmHg', DATE_SUB(NOW(), INTERVAL 25 DAY), 0),
(2, 'BLOOD_PRESSURE', 158, 98, 'mmHg', DATE_SUB(NOW(), INTERVAL 22 DAY), 1),
(2, 'BLOOD_PRESSURE', 128, 78, 'mmHg', DATE_SUB(NOW(), INTERVAL 20 DAY), 0),
(2, 'BLOOD_PRESSURE', 135, 82, 'mmHg', DATE_SUB(NOW(), INTERVAL 15 DAY), 0),
(2, 'BLOOD_PRESSURE', 142, 90, 'mmHg', DATE_SUB(NOW(), INTERVAL 10 DAY), 1),
(2, 'BLOOD_PRESSURE', 130, 79, 'mmHg', DATE_SUB(NOW(), INTERVAL 5  DAY), 0),
(2, 'BLOOD_PRESSURE', 136, 84, 'mmHg', DATE_SUB(NOW(), INTERVAL 1  DAY), 0);

INSERT INTO vital_sign (user_id, sign_type, value_main, unit, record_time, is_abnormal) VALUES
(2, 'BLOOD_SUGAR', 6.8, 'mmol/L', DATE_SUB(NOW(), INTERVAL 28 DAY), 0),
(2, 'BLOOD_SUGAR', 7.5, 'mmol/L', DATE_SUB(NOW(), INTERVAL 24 DAY), 1),
(2, 'BLOOD_SUGAR', 6.2, 'mmol/L', DATE_SUB(NOW(), INTERVAL 20 DAY), 0),
(2, 'BLOOD_SUGAR', 8.1, 'mmol/L', DATE_SUB(NOW(), INTERVAL 16 DAY), 1),
(2, 'BLOOD_SUGAR', 6.5, 'mmol/L', DATE_SUB(NOW(), INTERVAL 12 DAY), 0),
(2, 'BLOOD_SUGAR', 7.2, 'mmol/L', DATE_SUB(NOW(), INTERVAL 6  DAY), 1),
(2, 'BLOOD_SUGAR', 6.0, 'mmol/L', DATE_SUB(NOW(), INTERVAL 2  DAY), 0);

INSERT INTO vital_sign (user_id, sign_type, value_main, unit, record_time, is_abnormal) VALUES
(2, 'HEART_RATE', 72, 'bpm', DATE_SUB(NOW(), INTERVAL 25 DAY), 0),
(2, 'HEART_RATE', 105, 'bpm', DATE_SUB(NOW(), INTERVAL 22 DAY), 1),
(2, 'HEART_RATE', 68, 'bpm', DATE_SUB(NOW(), INTERVAL 18 DAY), 0),
(2, 'HEART_RATE', 78, 'bpm', DATE_SUB(NOW(), INTERVAL 14 DAY), 0),
(2, 'HEART_RATE', 65, 'bpm', DATE_SUB(NOW(), INTERVAL 8  DAY), 0),
(2, 'HEART_RATE', 112, 'bpm', DATE_SUB(NOW(), INTERVAL 3  DAY), 1);

INSERT INTO alert_record (user_id, alert_type, alert_level, alert_content, is_read, is_handled, create_time) VALUES
(2, 'BLOOD_PRESSURE', 'HIGH', '血压异常：收缩压158mmHg，舒张压98mmHg，超出正常范围，请立即就医或联系家属！', 1, 1, DATE_SUB(NOW(), INTERVAL 22 DAY)),
(2, 'BLOOD_SUGAR', 'MEDIUM', '血糖偏高：空腹血糖8.1mmol/L，超出正常范围(3.9-7.0)，建议控制饮食并及时就诊。', 1, 0, DATE_SUB(NOW(), INTERVAL 16 DAY)),
(2, 'HEART_RATE', 'MEDIUM', '心率偏快：心率112bpm，超出正常范围(60-100)，请注意休息。', 0, 0, DATE_SUB(NOW(), INTERVAL 3  DAY));

INSERT INTO risk_assessment (user_id, assessment_time, risk_level, disease_risks, health_advice, diet_advice, exercise_advice, accuracy_score) VALUES
(2, DATE_SUB(NOW(), INTERVAL 7 DAY), 'HIGH',
 '{"高血压":0.85,"2型糖尿病":0.78,"心脑血管疾病":0.62,"肾功能损害":0.45}',
 '张三您好，根据您近期的体征数据和健康档案综合评估，您目前处于高风险状态。您的血压和血糖控制情况需要特别关注。建议您按时服药，定期监测，并尽快与您的社区医生预约复诊。',
 '建议每日摄入食盐不超过5克，减少高糖食物摄入，多吃绿叶蔬菜和低糖水果（如苹果、梨），避免油炸食品和动物内脏。每天保证充足饮水（1500-2000ml）。',
 '建议每天进行30分钟低强度有氧运动，如散步、太极拳等，避免剧烈运动和高强度力量训练。运动前后注意监测血压和血糖。',
 0.82);

INSERT INTO appointment (elder_id, doctor_id, appoint_time, status, symptom_desc, doctor_advice) VALUES
(2, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CONFIRMED', '最近血压偏高，头晕头痛，需要复诊调整用药', '已确认预约，请按时就诊，携带近期血压记录'),
(2, 4, DATE_SUB(NOW(), INTERVAL 10 DAY), 'COMPLETED', '血糖控制不佳，需要复查', '建议调整二甲双胍剂量，一个月后复查糖化血红蛋白'),
(5, 4, DATE_ADD(NOW(), INTERVAL 5 DAY), 'PENDING', '膝关节疼痛，行走困难', NULL);

INSERT INTO physical_exam (user_id, exam_date, height, weight, bmi, vision_left, vision_right, exam_hospital, remark) VALUES
(2, DATE_SUB(NOW(), INTERVAL 90 DAY), 168.0, 72.5, 25.7, 0.6, 0.8, '北京朝阳社区卫生服务中心', '整体状况需关注，血压血糖偏高'),
(5, DATE_SUB(NOW(), INTERVAL 60 DAY), 155.0, 58.0, 24.1, 0.4, 0.5, '北京海淀社区卫生服务中心', '骨密度偏低，建议补钙');
