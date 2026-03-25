<template>
  <el-menu :default-active="route.path" router class="menu">
    <div class="brand">健康管理系统</div>

    <el-menu-item index="/">
      <el-icon><House /></el-icon>
      <span>首页</span>
    </el-menu-item>

    <el-menu-item v-if="canSeeVital" index="/vital-signs">
      <el-icon><Monitor /></el-icon>
      <span>体征管理</span>
    </el-menu-item>

    <el-menu-item v-if="canSeeProfile" index="/profile">
      <el-icon><User /></el-icon>
      <span>健康档案</span>
    </el-menu-item>

    <el-menu-item v-if="canSeeAssessment" index="/assessment">
      <el-icon><DataAnalysis /></el-icon>
      <span>风险评估</span>
    </el-menu-item>

    <el-menu-item index="/alerts">
      <el-icon><Bell /></el-icon>
      <span>预警管理
        <el-badge v-if="unreadCount > 0" :value="unreadCount" class="badge" />
      </span>
    </el-menu-item>

    <el-menu-item v-if="canSeeAppointment" index="/appointments">
      <el-icon><Calendar /></el-icon>
      <span>预约挂号</span>
    </el-menu-item>

    <el-menu-item v-if="isAdmin" index="/statistics">
      <el-icon><TrendCharts /></el-icon>
      <span>数据统计</span>
    </el-menu-item>

    <el-menu-item v-if="isAdmin" index="/users">
      <el-icon><UserFilled /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { alertApi } from '@/api/index'
import {
  House, Monitor, User, DataAnalysis, Bell,
  Calendar, TrendCharts, UserFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()
const unreadCount = ref(0)

const role = computed(() => userStore.user?.role)
const isAdmin = computed(() => role.value === 'ADMIN')
const canSeeVital = computed(() => ['ADMIN', 'ELDER', 'FAMILY', 'DOCTOR'].includes(role.value))
const canSeeProfile = computed(() => ['ADMIN', 'ELDER', 'FAMILY', 'DOCTOR'].includes(role.value))
const canSeeAssessment = computed(() => ['ADMIN', 'ELDER', 'FAMILY', 'DOCTOR'].includes(role.value))
const canSeeAppointment = computed(() => ['ELDER', 'FAMILY', 'DOCTOR'].includes(role.value))

async function loadUnread() {
  try {
    const res = await alertApi.list()
    const list = res.data || []
    unreadCount.value = list.filter(a => a.isRead === 0).length
  } catch {}
}

onMounted(() => {
  loadUnread()
})
</script>

<style scoped>
.menu {
  height: 100%;
  border-right: none;
}
.brand {
  padding: 16px;
  font-weight: 600;
  font-size: 15px;
  color: #409eff;
  border-bottom: 1px solid #ebeef5;
  text-align: center;
}
.badge {
  margin-left: 4px;
}
</style>
