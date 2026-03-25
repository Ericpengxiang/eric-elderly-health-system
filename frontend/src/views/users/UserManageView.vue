<template>
  <div>
    <el-card>
      <template #header>
        <span>用户管理</span>
        <div style="float:right;display:flex;gap:8px">
          <el-select v-model="filterRole" placeholder="角色筛选" clearable size="small" style="width:120px" @change="loadUsers">
            <el-option label="老年人" value="ELDER" />
            <el-option label="社区医生" value="DOCTOR" />
            <el-option label="家属" value="FAMILY" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
          <el-input v-model="search" placeholder="搜索用户名/姓名" size="small" style="width:160px" clearable @input="filterUsers" />
          <el-button size="small" @click="loadUsers">刷新</el-button>
        </div>
      </template>

      <el-table :data="filteredUsers" size="small" v-loading="loading">
        <el-table-column label="ID" prop="id" width="60" />
        <el-table-column label="用户名" prop="username" width="120" />
        <el-table-column label="姓名" prop="realName" width="100" />
        <el-table-column label="手机" prop="phone" width="120" />
        <el-table-column label="邮箱" prop="email" show-overflow-tooltip />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="roleTag(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="primary" link size="small" @click="openRoleEdit(row)">
              修改角色
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 修改角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="修改用户角色" width="360px">
      <el-form label-width="80px">
        <el-form-item label="用户">{{ currentUser?.realName || currentUser?.username }}</el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="newRole" style="width:100%">
            <el-option label="老年人" value="ELDER" />
            <el-option label="社区医生" value="DOCTOR" />
            <el-option label="家属" value="FAMILY" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/index'

const loading = ref(false)
const users = ref([])
const filterRole = ref('')
const search = ref('')
const roleDialogVisible = ref(false)
const currentUser = ref(null)
const newRole = ref('')
const saving = ref(false)

const filteredUsers = computed(() => {
  let list = users.value
  if (search.value) {
    const s = search.value.toLowerCase()
    list = list.filter(u => (u.username || '').toLowerCase().includes(s) || (u.realName || '').toLowerCase().includes(s))
  }
  return list
})

function roleLabel(r) {
  const map = { ADMIN: '管理员', ELDER: '老年人', FAMILY: '家属', DOCTOR: '社区医生' }
  return map[r] || r
}
function roleTag(r) {
  const map = { ADMIN: 'danger', ELDER: 'success', FAMILY: 'warning', DOCTOR: 'primary' }
  return map[r] || ''
}

function filterUsers() {}

async function loadUsers() {
  loading.value = true
  try {
    let res
    if (filterRole.value) {
      res = await userApi.listByRole(filterRole.value)
    } else {
      res = await userApi.listAll()
    }
    users.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确认${action}用户 ${row.realName || row.username}？`, '提示', { type: 'warning' })
    await userApi.updateStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch {}
}

function openRoleEdit(row) {
  currentUser.value = row
  newRole.value = row.role
  roleDialogVisible.value = true
}

async function saveRole() {
  saving.value = true
  try {
    await userApi.updateRole(currentUser.value.id, newRole.value)
    currentUser.value.role = newRole.value
    roleDialogVisible.value = false
    ElMessage.success('角色修改成功')
  } catch {} finally {
    saving.value = false
  }
}

onMounted(loadUsers)
</script>
