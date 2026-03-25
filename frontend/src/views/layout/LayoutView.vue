<template>
  <el-container class="layout">
    <el-aside width="220px">
      <SidebarMenu />
    </el-aside>
    <el-container direction="vertical">
      <HeaderBar />
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { onMounted } from 'vue'
import SidebarMenu from './SidebarMenu.vue'
import HeaderBar from './HeaderBar.vue'
import { fetchMe } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 每次进入布局页面时，强制从服务器重新获取用户信息
// 确保 localStorage 中的乱码缓存被正确数据覆盖
onMounted(async () => {
  try {
    const res = await fetchMe()
    if (res && res.data) {
      userStore.setUser(res.data)
    }
  } catch (e) {
    // 如果获取失败（token过期等），路由守卫会处理跳转
  }
})
</script>

<style scoped>
.layout {
  height: 100vh;
}
.el-main {
  background: #f5f7fa;
}
</style>
