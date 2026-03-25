<template>
  <div class="dashboard">
    <el-row :gutter="16" class="welcome-row">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div>
              <h2>欢迎回来，{{ userStore.user?.realName || userStore.user?.username }}</h2>
              <el-tag :type="roleTagType" size="large">{{ roleLabel }}</el-tag>
            </div>
            <div class="quick-links">
              <el-button v-if="canSeeVital" type="primary" @click="$router.push('/vital-signs')">
                录入体征
              </el-button>
              <el-button v-if="canSeeAssessment" type="success" @click="$router.push('/assessment')">
                风险评估
              </el-button>
              <el-button v-if="isAdmin" type="warning" @click="$router.push('/statistics')">
                数据统计
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 管理员统计卡片 -->
    <template v-if="isAdmin && overview">
      <el-row :gutter="16" class="stat-row">
        <el-col :xs="12" :sm="6">
          <el-card class="stat-card">
            <div class="stat-value">{{ overview.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card class="stat-card elder">
            <div class="stat-value">{{ overview.elderCount || 0 }}</div>
            <div class="stat-label">老年人数</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card class="stat-card alert">
            <div class="stat-value">{{ overview.unhandledAlerts || 0 }}</div>
            <div class="stat-label">待处理预警</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card class="stat-card today">
            <div class="stat-value">{{ overview.alertsLast24h || 0 }}</div>
            <div class="stat-label">24h预警</div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 最新预警 -->
    <el-row :gutter="16" class="alert-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>最新预警记录</span>
            <el-button type="primary" link @click="$router.push('/alerts')" style="float:right">查看全部</el-button>
          </template>
          <el-empty v-if="!recentAlerts.length" description="暂无预警记录" />
          <el-table v-else :data="recentAlerts" size="small">
            <el-table-column label="预警类型" prop="alertType" width="130">
              <template #default="{ row }">
                <el-tag :type="alertTypeTag(row.alertType)" size="small">{{ alertTypeLabel(row.alertType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="级别" prop="alertLevel" width="80">
              <template #default="{ row }">
                <el-tag :type="levelTag(row.alertLevel)" size="small">{{ row.alertLevel }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="内容" prop="alertContent" show-overflow-tooltip />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.isHandled ? 'success' : 'danger'" size="small">
                  {{ row.isHandled ? '已处理' : '未处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" prop="createTime" width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { statisticsApi, alertApi } from '@/api/index'

const userStore = useUserStore()
const overview = ref(null)
const recentAlerts = ref([])

const role = computed(() => userStore.user?.role)
const isAdmin = computed(() => role.value === 'ADMIN')
const canSeeVital = computed(() => ['ADMIN', 'ELDER', 'FAMILY', 'DOCTOR'].includes(role.value))
const canSeeAssessment = computed(() => ['ADMIN', 'ELDER', 'FAMILY', 'DOCTOR'].includes(role.value))

const roleLabel = computed(() => {
  const map = { ADMIN: '管理员', ELDER: '老年人', FAMILY: '家属', DOCTOR: '社区医生' }
  return map[role.value] || role.value || ''
})
const roleTagType = computed(() => {
  const map = { ADMIN: 'danger', ELDER: 'success', FAMILY: 'warning', DOCTOR: 'primary' }
  return map[role.value] || ''
})

function alertTypeLabel(type) {
  const map = {
    BLOOD_PRESSURE: '血压异常', BLOOD_SUGAR: '血糖异常',
    HEART_RATE: '心率异常', TEMPERATURE: '体温异常', OXYGEN: '血氧异常'
  }
  return map[type] || type
}
function alertTypeTag(type) {
  const map = { BLOOD_PRESSURE: 'danger', BLOOD_SUGAR: 'warning', HEART_RATE: 'danger', TEMPERATURE: 'warning', OXYGEN: 'info' }
  return map[type] || ''
}
function levelTag(level) {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[level] || ''
}
function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

onMounted(async () => {
  try {
    if (isAdmin.value) {
      const res = await statisticsApi.overview()
      overview.value = res.data
    }
  } catch {}
  try {
    const res = await alertApi.list()
    recentAlerts.value = (res.data || []).slice(0, 5)
  } catch {}
})
</script>

<style scoped>
.dashboard { padding: 0; }
.welcome-row { margin-bottom: 16px; }
.welcome-card { background: linear-gradient(135deg, #e8f4fc 0%, #f0f2f5 100%); }
.welcome-content { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 12px; }
.welcome-content h2 { margin: 0 0 8px; font-size: 20px; }
.quick-links { display: flex; gap: 8px; flex-wrap: wrap; }
.stat-row { margin-bottom: 16px; }
.stat-card { text-align: center; }
.stat-card .stat-value { font-size: 32px; font-weight: 700; color: #409eff; }
.stat-card.elder .stat-value { color: #67c23a; }
.stat-card.alert .stat-value { color: #f56c6c; }
.stat-card.today .stat-value { color: #e6a23c; }
.stat-card .stat-label { color: #909399; font-size: 13px; margin-top: 4px; }
.alert-row { margin-bottom: 16px; }
</style>
