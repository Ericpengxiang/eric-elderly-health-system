<template>
  <div>
    <el-row :gutter="16">
      <!-- 发起预约（老年人/家属） -->
      <el-col v-if="canCreate" :span="8">
        <el-card>
          <template #header>发起预约</template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" size="small">
            <el-form-item label="选择医生" prop="doctorId">
              <el-select v-model="form.doctorId" placeholder="请选择医生" style="width:100%">
                <el-option
                  v-for="d in doctors"
                  :key="d.id"
                  :label="d.realName || d.username"
                  :value="d.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="预约时间" prop="appointTime">
              <el-date-picker
                v-model="form.appointTime"
                type="datetime"
                style="width:100%"
                value-format="YYYY-MM-DDTHH:mm:ss"
                :disabled-date="disabledDate"
              />
            </el-form-item>
            <el-form-item label="就诊原因" prop="symptomDesc">
              <el-input v-model="form.symptomDesc" type="textarea" :rows="3" placeholder="请描述症状或就诊原因" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submit" style="width:100%">
                提交预约
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 预约列表 -->
      <el-col :span="canCreate ? 16 : 24">
        <el-card>
          <template #header>
            <span>{{ isDoctor ? '我的接诊预约' : '我的预约记录' }}</span>
            <el-button size="small" style="float:right" @click="loadList">刷新</el-button>
          </template>
          <el-empty v-if="!list.length" description="暂无预约记录" />
          <el-table v-else :data="list" size="small">
            <el-table-column label="预约时间" width="160">
              <template #default="{ row }">{{ formatTime(row.appointTime) }}</template>
            </el-table-column>
            <el-table-column label="就诊原因" prop="symptomDesc" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="医生建议" prop="doctorAdvice" show-overflow-tooltip />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button v-if="isDoctor && row.status === 'PENDING'" type="primary" link size="small" @click="updateStatus(row, 'CONFIRMED')">确认</el-button>
                <el-button v-if="isDoctor && row.status === 'CONFIRMED'" type="success" link size="small" @click="openComplete(row)">完成</el-button>
                <el-button v-if="(row.status === 'PENDING' || row.status === 'CONFIRMED')" type="danger" link size="small" @click="updateStatus(row, 'CANCELLED')">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 完成预约弹窗 -->
    <el-dialog v-model="completeVisible" title="完成预约" width="400px">
      <el-form label-width="90px">
        <el-form-item label="医生建议">
          <el-input v-model="doctorAdvice" type="textarea" :rows="4" placeholder="请输入诊疗建议" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible=false">取消</el-button>
        <el-button type="primary" :loading="completing" @click="confirmComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { appointmentApi, userApi } from '@/api/index'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const role = computed(() => userStore.user?.role)
const isDoctor = computed(() => role.value === 'DOCTOR')
const canCreate = computed(() => ['ELDER', 'FAMILY'].includes(role.value))

const list = ref([])
const doctors = ref([])
const submitting = ref(false)
const completeVisible = ref(false)
const completing = ref(false)
const doctorAdvice = ref('')
const currentAppt = ref(null)
const formRef = ref()

const form = ref({ doctorId: null, appointTime: '', symptomDesc: '', remark: '' })
const rules = {
  doctorId: [{ required: true, message: '请选择医生' }],
  appointTime: [{ required: true, message: '请选择预约时间' }],
  symptomDesc: [{ required: true, message: '请输入就诊原因' }],
}

function disabledDate(d) {
  return d < new Date(new Date().setHours(0, 0, 0, 0))
}
function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}
function statusLabel(s) {
  const map = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[s] || s
}
function statusTag(s) {
  const map = { PENDING: 'warning', CONFIRMED: 'primary', COMPLETED: 'success', CANCELLED: 'info' }
  return map[s] || ''
}

async function loadList() {
  try {
    let res
    if (isDoctor.value) {
      res = await appointmentApi.listForDoctor()
    } else {
      res = await appointmentApi.listForElder()
    }
    list.value = res.data || []
  } catch {}
}

async function loadDoctors() {
  try {
    const res = await userApi.listByRole('DOCTOR')
    doctors.value = res.data || []
  } catch {}
}

async function submit() {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return
  submitting.value = true
  try {
    await appointmentApi.create({
      doctorId: form.value.doctorId,
      appointTime: form.value.appointTime,
      symptomDesc: form.value.symptomDesc,
      remark: form.value.remark,
    })
    ElMessage.success('预约成功，等待医生确认')
    form.value = { doctorId: null, appointTime: '', symptomDesc: '', remark: '' }
    await loadList()
  } catch {} finally {
    submitting.value = false
  }
}

async function updateStatus(row, status) {
  try {
    await appointmentApi.updateStatus(row.id, status)
    ElMessage.success('操作成功')
    await loadList()
  } catch {}
}

function openComplete(row) {
  currentAppt.value = row
  doctorAdvice.value = ''
  completeVisible.value = true
}

async function confirmComplete() {
  completing.value = true
  try {
    await appointmentApi.updateStatus(currentAppt.value.id, 'COMPLETED', doctorAdvice.value)
    ElMessage.success('预约已完成')
    completeVisible.value = false
    await loadList()
  } catch {} finally {
    completing.value = false
  }
}

onMounted(async () => {
  await loadList()
  if (canCreate.value) {
    await loadDoctors()
  }
})
</script>
