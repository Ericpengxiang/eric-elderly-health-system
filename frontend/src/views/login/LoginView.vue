<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>社区老年人健康智能管理系统</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent="onSubmit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" clearable>
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-button type="primary" native-type="submit" class="btn" :loading="loading">登录</el-button>
      </el-form>
      <TestAccountCard @pick="fill" />
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import TestAccountCard from '@/components/TestAccountCard.vue'
import { login, fetchMe } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

function fill(acc) {
  form.username = acc.username
  form.password = acc.password
}

async function onSubmit() {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return
  loading.value = true
  try {
    const res = await login({ username: form.username, password: form.password })
    const data = res.data
    userStore.setTokens(data.accessToken, data.refreshToken)
    userStore.setUser(data.user)
    const me = await fetchMe()
    userStore.setUser(me.data)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.replace(redirect)
  } catch (e) {
    /* 已在拦截器提示 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f4fc 0%, #f0f2f5 100%);
}
.login-card {
  width: 400px;
}
.login-card h2 {
  text-align: center;
  margin: 0 0 20px;
  font-weight: 600;
  color: #303133;
}
.btn {
  width: 100%;
  margin-bottom: 16px;
}
</style>
