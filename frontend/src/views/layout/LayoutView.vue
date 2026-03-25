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
import SidebarMenu from './SidebarMenu.vue'
import HeaderBar from './HeaderBar.vue'
</script>

<style scoped>
.layout {
  height: 100vh;
}
.el-main {
  background: #f5f7fa;
}
</style>
