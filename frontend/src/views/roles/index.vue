<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">角色管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
    </div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="角色名称" class="search-form-item">
        <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="角色类型" class="search-form-item">
        <el-select v-model="searchForm.roleType" placeholder="请选择角色类型" clearable>
          <el-option label="功能角色" :value="'function'" />
          <el-option label="数据角色" :value="'data'" />
        </el-select>
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
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleType" label="角色类型" width="120">
        <template #default="{ row }">
          <el-tag :class="row.roleType === 'function' ? 'function-role-tag' : 'data-role-tag'">
            {{ row.roleType === 'function' ? '功能角色' : '数据角色' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :class="row.status === 1 ? 'status-tag-active' : 'status-tag-inactive'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link @click="handleAssignPermission(row)">分配权限</el-button>
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
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色类型" prop="roleType">
          <el-radio-group v-model="form.roleType">
            <el-radio :value="'function'">功能角色</el-radio>
            <el-radio :value="'data'">数据角色</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="permissionDialogVisible" title="分配权限" width="600px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="服务过滤">
          <el-select
            v-model="filterServiceId"
            placeholder="请选择服务"
            clearable
            style="width: 200px"
            @change="filterPermissions"
          >
            <el-option
              v-for="service in serviceList"
              :key="service.id"
              :label="service.serviceName"
              :value="service.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select
            v-model="filterPermissionType"
            placeholder="请选择权限类型"
            clearable
            style="width: 200px"
            @change="filterPermissions"
          >
            <el-option label="功能权限" value="function" />
            <el-option label="数据权限" value="data" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限树">
          <el-tree
            ref="permissionTreeRef"
            :data="filteredPermissionTree"
            :props="treeProps"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :default-checked-keys="checkedPermissionIds"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSavePermissions">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRolePage, createRole, updateRole, deleteRole,
  getRolePermissionIds, assignRolePermissions,
  getPermissionTree, getServiceList
} from '@/api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const permissionTreeRef = ref(null)
const tableData = ref([])
const serviceList = ref([])
const allPermissionTree = ref([])
const filterServiceId = ref(null)
const filterPermissionType = ref(null)
const checkedPermissionIds = ref([])
const currentRoleId = ref(null)

const searchForm = reactive({
  roleName: '',
  roleType: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  roleCode: '',
  roleName: '',
  roleType: 'function',
  status: 1,
  description: ''
})

const rules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleType: [
    { required: true, message: '请选择角色类型', trigger: 'change' }
  ]
}

const treeProps = {
  children: 'children',
  label: 'permissionName'
}

const dialogTitle = ref('新增角色')

const filteredPermissionTree = computed(() => {
  let data = JSON.parse(JSON.stringify(allPermissionTree.value || []))
  
  if (filterServiceId.value || filterPermissionType.value) {
    data = data.filter(item => {
      const matchService = filterServiceId.value ? item.serviceId === filterServiceId.value : true
      const matchType = filterPermissionType.value ? item.permissionType === filterPermissionType.value : true
      return matchService && matchType
    })
  }
  
  return data
})

const loadServices = async () => {
  try {
    const res = await getServiceList()
    serviceList.value = res.data || []
  } catch (e) {
    console.error('加载服务列表失败', e)
  }
}

const loadPermissions = async () => {
  try {
    const res = await getPermissionTree({})
    allPermissionTree.value = res.data || []
  } catch (e) {
    console.error('加载权限列表失败', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRolePage({
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

const filterPermissions = () => {}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.roleName = ''
  searchForm.roleType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  form.id = null
  form.roleCode = ''
  form.roleName = ''
  form.roleType = 'function'
  form.status = 1
  form.description = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  form.id = row.id
  form.roleCode = row.roleCode
  form.roleName = row.roleName
  form.roleType = row.roleType
  form.status = row.status
  form.description = row.description
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleAssignPermission = async (row) => {
  currentRoleId.value = row.id
  filterServiceId.value = null
  filterPermissionType.value = null
  
  try {
    const res = await getRolePermissionIds(row.id)
    checkedPermissionIds.value = res.data || []
  } catch (e) {
    checkedPermissionIds.value = []
  }
  
  permissionDialogVisible.value = true
}

const handleSavePermissions = async () => {
  const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
  const allChecked = [...checkedKeys, ...halfCheckedKeys]
  
  submitLoading.value = true
  try {
    await assignRolePermissions(currentRoleId.value, allChecked)
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (e) {
    console.error('保存权限失败', e)
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
      await updateRole(form)
    } else {
      await createRole(form)
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
  loadServices()
  loadPermissions()
})
</script>
