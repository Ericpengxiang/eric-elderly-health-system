<template>
  <div>
    <!-- 概览卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ overview.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color:#67c23a">{{ overview.elderCount || 0 }}</div>
          <div class="stat-label">老年人数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color:#f56c6c">{{ overview.unhandledAlerts || 0 }}</div>
          <div class="stat-label">待处理预警</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color:#e6a23c">{{ overview.alertsLast24h || 0 }}</div>
          <div class="stat-label">24h预警数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 预警趋势折线图 -->
      <el-col :span="14">
        <el-card>
          <template #header>近7天预警趋势</template>
          <div ref="trendChartRef" style="height:280px"></div>
        </el-card>
      </el-col>

      <!-- 预警类型分布饼图 -->
      <el-col :span="10">
        <el-card>
          <template #header>预警类型分布</template>
          <div ref="pieChartRef" style="height:280px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户角色分布 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="24">
        <el-card>
          <template #header>用户角色分布</template>
          <el-row :gutter="16">
            <el-col :span="6">
              <el-statistic title="老年人" :value="overview.elderCount || 0" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="社区医生" :value="overview.doctorCount || 0" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="家属" :value="overview.familyCount || 0" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="今日新增" :value="overview.todayNewUsers || 0" />
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api/index'

const trendChartRef = ref()
const pieChartRef = ref()
const overview = ref({})

async function loadOverview() {
  try {
    const res = await statisticsApi.overview()
    overview.value = res.data || {}
  } catch {}
}

async function loadTrendChart() {
  try {
    const res = await statisticsApi.alertTrend()
    const data = res.data || []
    const chart = echarts.init(trendChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.map(d => d.date) },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{
        name: '预警数',
        type: 'line',
        data: data.map(d => d.count),
        smooth: true,
        itemStyle: { color: '#f56c6c' },
        areaStyle: { color: 'rgba(245,108,108,0.1)' }
      }],
      grid: { left: 40, right: 20, top: 20, bottom: 30 }
    })
  } catch {}
}

async function loadPieChart() {
  try {
    const res = await statisticsApi.alertTypeDistribution()
    const data = res.data || []
    const chart = echarts.init(pieChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', left: 'left', top: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: data.length > 0 ? data : [{ name: '暂无数据', value: 1 }],
        label: { show: false }
      }]
    })
  } catch {}
}

onMounted(async () => {
  await loadOverview()
  await loadTrendChart()
  await loadPieChart()
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 32px; font-weight: 700; color: #409eff; }
.stat-label { color: #909399; font-size: 13px; margin-top: 4px; }
</style>
