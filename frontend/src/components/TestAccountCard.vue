<template>
  <el-card class="test-account-card" shadow="never">
    <template #header>
      <span>🔑 测试账号（点击快速填入）</span>
    </template>
    <div
      v-for="account in accounts"
      :key="account.username"
      class="account-item"
      @click="$emit('pick', account)"
    >
      <el-tag :type="account.tagType" size="small">{{ account.roleLabel }}</el-tag>
      <span class="u">{{ account.username }}</span>
      <span class="pwd">{{ account.password }}</span>
    </div>
  </el-card>
</template>

<script setup>
defineEmits(['pick'])

const accounts = [
  { roleLabel: '管理员', username: 'admin', password: 'admin123', tagType: 'danger' },
  { roleLabel: '老年人', username: 'zhangsan', password: 'zhang123', tagType: 'success' },
  { roleLabel: '家　属', username: 'lisi', password: 'lisi123', tagType: 'warning' },
  { roleLabel: '医　生', username: 'wangwu', password: 'wang123', tagType: 'primary' },
]
</script>

<style scoped>
.test-account-card {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
}
.account-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
  cursor: pointer;
  font-size: 13px;
}
.account-item:hover {
  opacity: 0.85;
}
.pwd {
  color: #909399;
  font-family: monospace;
}
.u {
  flex: 1;
}
</style>
