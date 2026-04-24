<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">欢迎使用权限管理系统</span>
    </div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon :size="40" color="#409eff">
              <Avatar />
            </el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">
              {{ stats.userCount }}
            </div>
            <div style="color: #909399; margin-top: 5px">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon :size="40" color="#67c23a">
              <UserFilled />
            </el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">
              {{ stats.roleCount }}
            </div>
            <div style="color: #909399; margin-top: 5px">角色总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon :size="40" color="#e6a23c">
              <Key />
            </el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">
              {{ stats.permissionCount }}
            </div>
            <div style="color: #909399; margin-top: 5px">权限总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <el-icon :size="40" color="#f56c6c">
              <Grid />
            </el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold">
              {{ stats.serviceCount }}
            </div>
            <div style="color: #909399; margin-top: 5px">服务总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top: 20px">
      <template #header>
        <span>系统说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="技术栈">
          前端：Vue 3 + Element Plus + Vite
        </el-descriptions-item>
        <el-descriptions-item label="后端">
          Spring Boot 3.2 + MyBatis-Plus + Spring Security + JWT
        </el-descriptions-item>
        <el-descriptions-item label="数据库">
          MySQL + Redis
        </el-descriptions-item>
        <el-descriptions-item label="核心功能">
          服务管理、权限管理（3层树形结构）、角色管理（功能角色/数据角色）、用户管理
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getRoleList, getServiceList, getUserPage, getPermissionList } from '@/api'

const stats = reactive({
  userCount: 0,
  roleCount: 0,
  permissionCount: 0,
  serviceCount: 0
})

const loadStats = async () => {
  try {
    const [userRes, roleRes, permRes, serviceRes] = await Promise.all([
      getUserPage({ pageNum: 1, pageSize: 1 }),
      getRoleList(),
      getPermissionList({}),
      getServiceList()
    ])
    stats.userCount = userRes.data?.total || 0
    stats.roleCount = roleRes.data?.length || 0
    stats.permissionCount = permRes.data?.length || 0
    stats.serviceCount = serviceRes.data?.length || 0
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

onMounted(() => {
  loadStats()
})
</script>
