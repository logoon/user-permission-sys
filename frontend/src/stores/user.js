import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getUserInfo, register } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})

  const isLoggedIn = computed(() => !!token.value)

  async function handleLogin(loginForm) {
    const res = await login(loginForm)
    if (res.code === 200) {
      token.value = res.data.token
      userInfo.value = res.data.user
      localStorage.setItem('token', res.data.token)
      return res
    }
    throw new Error(res.message)
  }

  async function handleRegister(registerForm) {
    const res = await register(registerForm)
    if (res.code === 200) {
      return res
    }
    throw new Error(res.message)
  }

  async function handleLogout() {
    try {
      await logout()
    } catch (e) {
      console.error('Logout error:', e)
    } finally {
      token.value = ''
      userInfo.value = {}
      localStorage.removeItem('token')
    }
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
    }
    return res
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    handleLogin,
    handleRegister,
    handleLogout,
    fetchUserInfo
  }
})
