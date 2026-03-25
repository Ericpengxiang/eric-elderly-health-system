import request from './request'

// ==================== 体征 ====================
// 后端字段：signType(BLOOD_PRESSURE/BLOOD_SUGAR/HEART_RATE/BLOOD_OXYGEN/TEMPERATURE)
// 血压：valueSystolic + valueDiastolic；其他：valueMain；必须有 recordTime
export const vitalApi = {
  add: (data) => request.post('/vital-signs', data),
  list: (elderUserId, signType) =>
    request.get('/vital-signs', { params: { elderUserId, signType } }),
  del: (id) => request.delete(`/vital-signs/${id}`),
}

// ==================== 健康档案 ====================
// GET /profiles?elderUserId=  PUT /profiles?elderUserId= body: ProfileVO
export const profileApi = {
  get: (elderUserId) => request.get('/profiles', { params: elderUserId ? { elderUserId } : {} }),
  save: (data, elderUserId) =>
    request.put('/profiles', data, { params: elderUserId ? { elderUserId } : {} }),
}

// ==================== 预警 ====================
// GET /alerts?elderUserId=
// POST /alerts/{id}/read
// POST /alerts/{id}/handle?remark=
export const alertApi = {
  list: (elderUserId) => request.get('/alerts', { params: elderUserId ? { elderUserId } : {} }),
  listUnhandled: () => request.get('/alerts', { params: { isHandled: 0 } }),
  handle: (id, remark) => request.post(`/alerts/${id}/handle`, null, { params: { remark } }),
  markRead: (id) => request.post(`/alerts/${id}/read`),
}

// ==================== 风险评估 ====================
// POST /assessments/run?elderUserId=
// GET /assessments?elderUserId=
export const assessmentApi = {
  run: (elderUserId) =>
    request.post('/assessments/run', null, { params: elderUserId ? { elderUserId } : {} }),
  history: (elderUserId) =>
    request.get('/assessments', { params: elderUserId ? { elderUserId } : {} }),
}

// ==================== 预约 ====================
// POST /appointments  body: { elderId, doctorId, appointTime, symptomDesc, remark }
// GET /appointments/elder?elderUserId=
// GET /appointments/doctor?doctorId=
// PUT /appointments/{id}/status?status=CONFIRMED|CANCELLED|COMPLETED&doctorAdvice=
export const appointmentApi = {
  create: (data) => request.post('/appointments', data),
  listForElder: (elderUserId) =>
    request.get('/appointments/elder', { params: elderUserId ? { elderUserId } : {} }),
  listForDoctor: (doctorId) =>
    request.get('/appointments/doctor', { params: doctorId ? { doctorId } : {} }),
  updateStatus: (id, status, doctorAdvice) =>
    request.put(`/appointments/${id}/status`, null, { params: { status, doctorAdvice } }),
}

// ==================== 统计 ====================
export const statisticsApi = {
  overview: () => request.get('/statistics/overview'),
  alertTrend: () => request.get('/statistics/alert-trend'),
  alertTypeDistribution: () => request.get('/statistics/alert-type-distribution'),
}

// ==================== 用户管理 ====================
export const userApi = {
  me: () => request.get('/users/me'),
  listAll: () => request.get('/users'),
  listByRole: (role) => request.get('/users/by-role', { params: { role } }),
  updateStatus: (id, status) =>
    request.put(`/users/${id}/status`, null, { params: { status } }),
  updateRole: (id, role) =>
    request.put(`/users/${id}/role`, null, { params: { role } }),
}
