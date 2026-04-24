<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="layout-header-left">
        <el-icon :size="30" color="#fff">
          <Promotion />
        </el-icon>
        <span class="layout-logo">权限管理系统</span>
      </div>
      <div class="layout-header-right">
        <span class="layout-user-info">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
        <el-button type="danger" text @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出
        </el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          class="layout-menu"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/services">
            <el-icon><Grid /></el-icon>
            <span>服务管理</span>
          </el-menu-item>
          <el-menu-item index="/permissions">
            <el-icon><Key /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><Avatar /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

onMounted(() => {
  if (userStore.token && !userStore.userInfo.id) {
    userStore.fetchUserInfo()
  }
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.handleLogout()
    router.push('/login')
  }).catch(() => {})
}
</script>
