<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="0">
        <el-form-item prop="username" class="register-form-item">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（3-20个字符）"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password" class="register-form-item">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20个字符）"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword" class="register-form-item">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="nickname" class="register-form-item">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称（选填）"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="email" class="register-form-item">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱（选填）"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        <el-form-item class="register-form-item">
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.handleRegister(registerForm)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>
