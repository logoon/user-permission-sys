<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">用户管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增用户
      </el-button>
    </div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="用户名" class="search-form-item">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="昵称" class="search-form-item">
        <el-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="状态" class="search-form-item">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item class="search-form-item">
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :class="row.status === 1 ? 'status-tag-active' : 'status-tag-inactive'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isAdmin" label="管理员" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isAdmin === 1 ? 'success' : 'info'">
            {{ row.isAdmin === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link @click="handleAssignRole(row)">分配角色</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #suffix>
              <span style="color: #909399; font-size: 12px">{{ isEdit ? '留空表示不修改' : '必填' }}</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否管理员" prop="isAdmin">
          <el-radio-group v-model="form.isAdmin">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px" destroy-on-close>
      <el-checkbox-group v-model="checkedRoleIds">
        <el-checkbox
          v-for="role in roleList"
          :key="role.id"
          :label="role.id"
          style="display: block; margin: 10px 0"
        >
          <span>{{ role.roleName }}</span>
          <el-tag
            :type="role.roleType === 'function' ? 'primary' : 'warning'"
            size="small"
            style="margin-left: 8px"
          >
            {{ role.roleType === 'function' ? '功能角色' : '数据角色' }}
          </el-tag>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSaveRoles">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserPage, createUser, updateUser, deleteUser,
  getUserRoleIds, assignUserRoles, getRoleList
} from '@/api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const tableData = ref([])
const roleList = ref([])
const checkedRoleIds = ref([])
const currentUserId = ref(null)

const searchForm = reactive({
  username: '',
  nickname: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  isAdmin: 0
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: !isEdit.value, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ]
}

const dialogTitle = ref('新增用户')

const loadRoles = async () => {
  try {
    const res = await getRoleList()
    roleList.value = res.data || []
  } catch (e) {
    console.error('加载角色列表失败', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.nickname = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  form.id = null
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.email = ''
  form.phone = ''
  form.status = 1
  form.isAdmin = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  form.id = row.id
  form.username = row.username
  form.password = ''
  form.nickname = row.nickname
  form.email = row.email
  form.phone = row.phone
  form.status = row.status
  form.isAdmin = row.isAdmin === 1 ? 1 : 0
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleAssignRole = async (row) => {
  currentUserId.value = row.id
  
  try {
    const res = await getUserRoleIds(row.id)
    checkedRoleIds.value = res.data || []
  } catch (e) {
    checkedRoleIds.value = []
  }
  
  roleDialogVisible.value = true
}

const handleSaveRoles = async () => {
  submitLoading.value = true
  try {
    await assignUserRoles(currentUserId.value, checkedRoleIds.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } catch (e) {
    console.error('保存角色失败', e)
  } finally {
    submitLoading.value = false
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      const updateData = { ...form }
      if (!updateData.password) {
        delete updateData.password
      }
      await updateUser(updateData)
    } else {
      await createUser(form)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('提交失败', e)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
  loadRoles()
})
</script>
