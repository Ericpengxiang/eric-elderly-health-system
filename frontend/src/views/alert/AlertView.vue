<template>
  <div>
    <el-card>
      <template #header>
        <span>预警管理</span>
        <div style="float:right;display:flex;gap:8px">
          <el-radio-group v-model="filterStatus" size="small" @change="loadAlerts">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="unhandled">未处理</el-radio-button>
            <el-radio-button label="handled">已处理</el-radio-button>
          </el-radio-group>
          <el-button size="small" @click="loadAlerts">刷新</el-button>
        </div>
      </template>

      <el-empty v-if="!displayList.length" description="暂无预警记录" />
      <el-table v-else :data="displayList" size="small" row-key="id">
        <el-table-column label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="alertTypeTag(row.alertType)" size="small">{{ alertTypeLabel(row.alertType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.alertLevel)" size="small">{{ row.alertLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预警内容" prop="alertContent" show-overflow-tooltip />
        <el-table-column label="已读" width="70">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'warning'" size="small">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isHandled ? 'success' : 'danger'" size="small">
              {{ row.isHandled ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理备注" prop="handleRemark" show-overflow-tooltip />
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.isRead" type="primary" link size="small" @click="markRead(row)">标记已读</el-button>
            <el-button v-if="!row.isHandled && isAdminOrDoctor" type="success" link size="small" @click="openHandle(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 处理弹窗 -->
    <el-dialog v-model="handleVisible" title="处理预警" width="400px">
      <el-form label-width="80px">
        <el-form-item label="处理备注">
          <el-input v-model="handleRemark" type="textarea" :rows="3" placeholder="请输入处理意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible=false">取消</el-button>
        <el-button type="primary" :loading="handling" @click="confirmHandle">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { alertApi } from '@/api/index'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const list = ref([])
const filterStatus = ref('')
const handleVisible = ref(false)
const handleRemark = ref('')
const handling = ref(false)
const currentAlert = ref(null)

const isAdminOrDoctor = computed(() => ['ADMIN', 'DOCTOR'].includes(userStore.user?.role))

const displayList = computed(() => {
  if (filterStatus.value === 'unhandled') return list.value.filter(a => !a.isHandled)
  if (filterStatus.value === 'handled') return list.value.filter(a => a.isHandled)
  return list.value
})

function alertTypeLabel(type) {
  const map = { BLOOD_PRESSURE: '血压异常', BLOOD_SUGAR: '血糖异常', HEART_RATE: '心率异常', TEMPERATURE: '体温异常', BLOOD_OXYGEN: '血氧异常' }
  return map[type] || type
}
function alertTypeTag(type) {
  const map = { BLOOD_PRESSURE: 'danger', BLOOD_SUGAR: 'warning', HEART_RATE: 'danger', TEMPERATURE: 'warning', BLOOD_OXYGEN: 'info' }
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

async function loadAlerts() {
  try {
    const res = await alertApi.list()
    list.value = res.data || []
  } catch {}
}

async function markRead(row) {
  try {
    await alertApi.markRead(row.id)
    row.isRead = 1
    ElMessage.success('已标记为已读')
  } catch {}
}

function openHandle(row) {
  currentAlert.value = row
  handleRemark.value = ''
  handleVisible.value = true
}

async function confirmHandle() {
  handling.value = true
  try {
    await alertApi.handle(currentAlert.value.id, handleRemark.value)
    ElMessage.success('处理成功')
    handleVisible.value = false
    await loadAlerts()
  } catch {} finally {
    handling.value = false
  }
}

onMounted(loadAlerts)
</script>
