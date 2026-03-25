<template>
  <div class="header">
    <span class="title">社区老年人健康智能管理系统</span>
    <div class="right">
      <span v-if="userStore.user" class="name">{{ userStore.user.realName || userStore.user.username }}</span>
      <el-tag v-if="userStore.user" size="small" type="info">{{ roleLabel }}</el-tag>
      <el-button type="primary" link @click="logout">退出</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const roleLabel = computed(() => {
  const r = userStore.user?.role
  const map = { ADMIN: '管理员', ELDER: '老年人', FAMILY: '家属', DOCTOR: '社区医生' }
  return map[r] || r || ''
})

function logout() {
  userStore.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.title {
  font-weight: 600;
}
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.name {
  color: #606266;
}
</style>
