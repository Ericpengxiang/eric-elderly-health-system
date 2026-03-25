<template>
  <div>
    <el-row :gutter="16">
      <!-- 录入表单 -->
      <el-col :span="8">
        <el-card>
          <template #header>录入体征数据</template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" size="small">
            <el-form-item label="体征类型" prop="signType">
              <el-select v-model="form.signType" placeholder="请选择" style="width:100%" @change="onTypeChange">
                <el-option label="血压" value="BLOOD_PRESSURE" />
                <el-option label="血糖" value="BLOOD_SUGAR" />
                <el-option label="心率" value="HEART_RATE" />
                <el-option label="体温" value="TEMPERATURE" />
                <el-option label="血氧" value="BLOOD_OXYGEN" />
              </el-select>
            </el-form-item>

            <!-- 血压：收缩压 + 舒张压 -->
            <template v-if="form.signType === 'BLOOD_PRESSURE'">
              <el-form-item label="收缩压" prop="valueSystolic">
                <el-input v-model="form.valueSystolic" placeholder="收缩压">
                  <template #append>mmHg</template>
                </el-input>
              </el-form-item>
              <el-form-item label="舒张压" prop="valueDiastolic">
                <el-input v-model="form.valueDiastolic" placeholder="舒张压">
                  <template #append>mmHg</template>
                </el-input>
              </el-form-item>
            </template>

            <!-- 其他：主值 -->
            <el-form-item v-else label="数值" prop="valueMain">
              <el-input v-model="form.valueMain" :placeholder="mainPlaceholder">
                <template #append>{{ mainUnit }}</template>
              </el-input>
            </el-form-item>

            <el-form-item label="记录时间" prop="recordTime">
              <el-date-picker v-model="form.recordTime" type="datetime" style="width:100%"
                value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submit" style="width:100%">
                提交录入
              </el-button>
            </el-form-item>
          </el-form>
          <el-divider>正常参考范围</el-divider>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="血压">90-140 / 60-90 mmHg</el-descriptions-item>
            <el-descriptions-item label="血糖">3.9-7.0 mmol/L</el-descriptions-item>
            <el-descriptions-item label="心率">60-100 bpm</el-descriptions-item>
            <el-descriptions-item label="体温">36.0-37.5 ℃</el-descriptions-item>
            <el-descriptions-item label="血氧">95-100 %</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 历史记录 + 趋势图 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>体征历史记录</span>
            <div style="float:right;display:flex;gap:8px">
              <el-select v-model="filterType" placeholder="筛选类型" clearable size="small" style="width:130px" @change="loadList">
                <el-option label="血压" value="BLOOD_PRESSURE" />
                <el-option label="血糖" value="BLOOD_SUGAR" />
                <el-option label="心率" value="HEART_RATE" />
                <el-option label="体温" value="TEMPERATURE" />
                <el-option label="血氧" value="BLOOD_OXYGEN" />
              </el-select>
            </div>
          </template>
          <el-table :data="list" size="small" max-height="300">
            <el-table-column label="类型" width="90">
              <template #default="{ row }">{{ signTypeLabel(row.signType) }}</template>
            </el-table-column>
            <el-table-column label="数值" width="160">
              <template #default="{ row }">
                <span v-if="row.signType === 'BLOOD_PRESSURE'">
                  {{ row.valueSystolic }}/{{ row.valueDiastolic }} mmHg
                </span>
                <span v-else>{{ row.valueMain }} {{ signUnit(row.signType) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="70">
              <template #default="{ row }">
                <el-tag :type="row.isAbnormal ? 'danger' : 'success'" size="small">
                  {{ row.isAbnormal ? '异常' : '正常' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" show-overflow-tooltip />
            <el-table-column label="时间" width="150">
              <template #default="{ row }">{{ formatTime(row.recordTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 趋势图 -->
        <el-card style="margin-top:16px">
          <template #header>
            <span>体征趋势图</span>
            <el-select v-model="trendType" size="small" style="float:right;width:130px" @change="renderTrend">
              <el-option label="血压(收缩)" value="BLOOD_PRESSURE" />
              <el-option label="血糖" value="BLOOD_SUGAR" />
              <el-option label="心率" value="HEART_RATE" />
              <el-option label="体温" value="TEMPERATURE" />
              <el-option label="血氧" value="BLOOD_OXYGEN" />
            </el-select>
          </template>
          <div ref="chartRef" style="height:220px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { vitalApi } from '@/api/index'

const formRef = ref()
const chartRef = ref()
let chart = null
const submitting = ref(false)
const list = ref([])
const filterType = ref('')
const trendType = ref('BLOOD_PRESSURE')

const now = new Date()
const form = ref({
  signType: 'BLOOD_PRESSURE',
  valueSystolic: '',
  valueDiastolic: '',
  valueMain: '',
  recordTime: now.toISOString().slice(0, 19),
  remark: ''
})

const rules = computed(() => {
  const base = {
    signType: [{ required: true, message: '请选择类型' }],
    recordTime: [{ required: true, message: '请选择时间' }],
  }
  if (form.value.signType === 'BLOOD_PRESSURE') {
    base.valueSystolic = [{ required: true, message: '请输入收缩压' }]
    base.valueDiastolic = [{ required: true, message: '请输入舒张压' }]
  } else {
    base.valueMain = [{ required: true, message: '请输入数值' }]
  }
  return base
})

const mainPlaceholder = computed(() => {
  const map = { BLOOD_SUGAR: '血糖值', HEART_RATE: '心率', TEMPERATURE: '体温', BLOOD_OXYGEN: '血氧饱和度' }
  return map[form.value.signType] || '数值'
})
const mainUnit = computed(() => {
  const map = { BLOOD_SUGAR: 'mmol/L', HEART_RATE: 'bpm', TEMPERATURE: '℃', BLOOD_OXYGEN: '%' }
  return map[form.value.signType] || ''
})

function onTypeChange() {
  form.value.valueSystolic = ''
  form.value.valueDiastolic = ''
  form.value.valueMain = ''
}

function signTypeLabel(t) {
  const map = { BLOOD_PRESSURE: '血压', BLOOD_SUGAR: '血糖', HEART_RATE: '心率', TEMPERATURE: '体温', BLOOD_OXYGEN: '血氧' }
  return map[t] || t
}
function signUnit(t) {
  const map = { BLOOD_SUGAR: 'mmol/L', HEART_RATE: 'bpm', TEMPERATURE: '℃', BLOOD_OXYGEN: '%' }
  return map[t] || ''
}
function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

async function loadList() {
  try {
    const res = await vitalApi.list(null, filterType.value || undefined)
    list.value = res.data || []
    renderTrend()
  } catch {}
}

function renderTrend() {
  const filtered = list.value.filter(v => v.signType === trendType.value)
    .slice().reverse().slice(-20)
  const xData = filtered.map(d => formatTime(d.recordTime))
  const yData = filtered.map(d =>
    trendType.value === 'BLOOD_PRESSURE' ? Number(d.valueSystolic) : Number(d.valueMain)
  )
  if (!chart && chartRef.value) {
    chart = echarts.init(chartRef.value)
  }
  if (!chart) return
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: xData, axisLabel: { rotate: 30, fontSize: 10 } },
    yAxis: { type: 'value' },
    series: [{
      name: signTypeLabel(trendType.value),
      type: 'line',
      data: yData,
      smooth: true,
      itemStyle: { color: '#409eff' },
      areaStyle: { opacity: 0.1 }
    }],
    grid: { left: 40, right: 20, top: 20, bottom: 50 }
  })
}

async function submit() {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return
  submitting.value = true
  try {
    const payload = {
      signType: form.value.signType,
      recordTime: form.value.recordTime,
      remark: form.value.remark,
    }
    if (form.value.signType === 'BLOOD_PRESSURE') {
      payload.valueSystolic = parseFloat(form.value.valueSystolic)
      payload.valueDiastolic = parseFloat(form.value.valueDiastolic)
    } else {
      payload.valueMain = parseFloat(form.value.valueMain)
    }
    await vitalApi.add(payload)
    ElMessage.success('录入成功，系统已自动检测是否异常')
    form.value = { signType: form.value.signType, valueSystolic: '', valueDiastolic: '', valueMain: '', recordTime: new Date().toISOString().slice(0, 19), remark: '' }
    await loadList()
  } catch {} finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadList()
})
</script>
