<template>
  <div>
    <el-card>
      <template #header>
        <span>健康档案</span>
        <el-button v-if="!editing" type="primary" size="small" style="float:right" @click="editing=true">编辑</el-button>
        <div v-else style="float:right;display:flex;gap:8px">
          <el-button type="success" size="small" :loading="saving" @click="save">保存</el-button>
          <el-button size="small" @click="cancelEdit">取消</el-button>
        </div>
      </template>

      <el-empty v-if="!profile && !editing" description="暂无健康档案，请点击编辑填写">
        <el-button type="primary" @click="editing=true">立即填写</el-button>
      </el-empty>

      <el-form v-if="editing || profile" ref="formRef" :model="form" label-width="110px" :disabled="!editing">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="form.idCard" maxlength="18" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker v-model="form.birthDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血型">
              <el-select v-model="form.bloodType" style="width:100%">
                <el-option label="A型" value="A" />
                <el-option label="B型" value="B" />
                <el-option label="AB型" value="AB" />
                <el-option label="O型" value="O" />
                <el-option label="未知" value="未知" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="居住地址">
              <el-input v-model="form.address" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人">
              <el-input v-model="form.emergencyContact" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话">
              <el-input v-model="form.emergencyPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="既往病史">
              <el-input v-model="form.medicalHistory" type="textarea" :rows="3"
                placeholder="如：高血压、糖尿病、心脏病等" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过敏史">
              <el-input v-model="form.allergyHistory" type="textarea" :rows="2"
                placeholder="如：青霉素过敏、花粉过敏等" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 查看模式下展示 -->
      <el-descriptions v-if="profile && !editing" :column="2" border>
        <el-descriptions-item label="身份证号">{{ profile.idCard || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ profile.birthDate || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ profile.gender === 1 ? '男' : profile.gender === 2 ? '女' : '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="血型">{{ profile.bloodType || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="居住地址" :span="2">{{ profile.address || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ profile.emergencyContact || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话">{{ profile.emergencyPhone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2">
          <el-tag v-if="profile.medicalHistory" type="warning">{{ profile.medicalHistory }}</el-tag>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">
          <el-tag v-if="profile.allergyHistory" type="danger">{{ profile.allergyHistory }}</el-tag>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { profileApi } from '@/api/index'

const profile = ref(null)
const editing = ref(false)
const saving = ref(false)
const formRef = ref()
const form = ref({
  idCard: '', birthDate: '', gender: 1, bloodType: '',
  address: '', emergencyContact: '', emergencyPhone: '',
  medicalHistory: '', allergyHistory: ''
})

async function load() {
  try {
    const res = await profileApi.get()
    profile.value = res.data
    if (profile.value) {
      Object.assign(form.value, profile.value)
    }
  } catch {}
}

function cancelEdit() {
  editing.value = false
  if (profile.value) {
    Object.assign(form.value, profile.value)
  }
}

async function save() {
  saving.value = true
  try {
    const res = await profileApi.save(form.value)
    profile.value = res.data
    editing.value = false
    ElMessage.success('保存成功')
  } catch {} finally {
    saving.value = false
  }
}

onMounted(load)
</script>
