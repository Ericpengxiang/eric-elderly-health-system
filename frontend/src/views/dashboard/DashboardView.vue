<template>
  <el-card>
    <h3>欢迎，{{ userStore.user?.realName || userStore.user?.username }}</h3>
    <p class="tip">当前角色：<strong>{{ roleLabel }}</strong></p>
    <p class="muted">业务菜单与图表页面可在后续迭代中按角色继续扩展；后端 API 已就绪，可联调体征、预警、预约、评估等模块。</p>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const roleLabel = computed(() => {
  const r = userStore.user?.role
  const map = { ADMIN: '管理员', ELDER: '老年人', FAMILY: '家属', DOCTOR: '社区医生' }
  return map[r] || r || ''
})
</script>

<style scoped>
.tip {
  margin: 12px 0;
}
.muted {
  color: #909399;
  line-height: 1.6;
}
</style>
