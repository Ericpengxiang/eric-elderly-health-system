<template>
  <el-menu :default-active="route.path" router class="menu">
    <div class="brand">健康管理系统</div>
    <el-menu-item index="/">首页</el-menu-item>
  </el-menu>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.menu {
  height: 100%;
}
.brand {
  padding: 16px;
  font-weight: 600;
  color: #409eff;
  border-bottom: 1px solid #ebeef5;
}
</style>
