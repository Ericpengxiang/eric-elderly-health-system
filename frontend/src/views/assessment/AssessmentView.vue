<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card>
          <template #header>发起风险评估</template>
          <el-alert type="info" :closable="false" style="margin-bottom:16px">
            系统将根据最新体征数据和健康档案，自动评估健康风险等级。
          </el-alert>
          <el-button type="primary" :loading="running" @click="runAssessment" style="width:100%">
            立即评估
          </el-button>
          <div v-if="latestResult" style="margin-top:16px">
            <el-divider>最新评估结果</el-divider>
            <el-result
              :icon="riskIcon(latestResult.riskLevel)"
              :title="riskLabel(latestResult.riskLevel)"
              :sub-title="latestResult.assessmentContent"
            >
              <template #extra>
                <el-tag :type="riskTagType(latestResult.riskLevel)" size="large">
                  风险等级：{{ riskLabel(latestResult.riskLevel) }}
                </el-tag>
              </template>
            </el-result>
            <el-descriptions :column="1" border size="small" style="margin-top:8px">
              <el-descriptions-item label="评估时间">{{ formatTime(latestResult.assessmentTime) }}</el-descriptions-item>
              <el-descriptions-item label="建议">{{ latestResult.suggestion || '暂无建议' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>历史评估记录</template>
          <el-empty v-if="!history.length" description="暂无评估记录" />
          <el-timeline v-else>
            <el-timeline-item
              v-for="item in history"
              :key="item.id"
              :type="riskTimelineType(item.riskLevel)"
              :timestamp="formatTime(item.assessmentTime)"
              placement="top"
            >
              <el-card size="small">
                <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
                  <el-tag :type="riskTagType(item.riskLevel)">{{ riskLabel(item.riskLevel) }}</el-tag>
                  <span style="font-size:13px;color:#606266">{{ item.assessmentContent }}</span>
                </div>
                <div v-if="item.suggestion" style="font-size:12px;color:#909399">
                  建议：{{ item.suggestion }}
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { assessmentApi } from '@/api/index'

const running = ref(false)
const history = ref([])
const latestResult = ref(null)

function riskLabel(level) {
  const map = { LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险' }
  return map[level] || level || '未知'
}
function riskTagType(level) {
  const map = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' }
  return map[level] || 'info'
}
function riskIcon(level) {
  const map = { LOW: 'success', MEDIUM: 'warning', HIGH: 'error' }
  return map[level] || 'info'
}
function riskTimelineType(level) {
  const map = { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' }
  return map[level] || 'primary'
}
function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

async function loadHistory() {
  try {
    const res = await assessmentApi.history()
    history.value = res.data || []
    if (history.value.length > 0) {
      latestResult.value = history.value[0]
    }
  } catch {}
}

async function runAssessment() {
  running.value = true
  try {
    const res = await assessmentApi.run()
    latestResult.value = res.data
    ElMessage.success('评估完成')
    await loadHistory()
  } catch {} finally {
    running.value = false
  }
}

onMounted(loadHistory)
</script>
