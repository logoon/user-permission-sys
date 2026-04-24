<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">权限管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增权限
      </el-button>
    </div>
    <div class="permission-tree-container">
      <div class="permission-tree-left">
        <el-select
          v-model="currentServiceId"
          placeholder="选择服务"
          style="width: 100%; margin-bottom: 15px"
          @change="loadData"
          clearable
        >
          <el-option
            v-for="service in serviceList"
            :key="service.id"
            :label="service.serviceName"
            :value="service.id"
          />
        </el-select>
        <el-select
          v-model="currentPermissionType"
          placeholder="权限类型"
          style="width: 100%; margin-bottom: 15px"
          @change="loadData"
          clearable
        >
          <el-option label="功能权限" value="function" />
          <el-option label="数据权限" value="data" />
        </el-select>
        <el-tree
          :data="treeData"
          :props="treeProps"
          :default-expand-all="true"
          highlight-current
          node-key="id"
          @node-click="handleNodeClick"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>
                <el-icon v-if="data.permissionType === 'function'" color="#409eff"><Key /></el-icon>
                <el-icon v-else color="#e6a23c"><DataAnalysis /></el-icon>
                {{ data.permissionName }}
              </span>
              <span class="tree-node-action">
                <el-button type="primary" link size="small" @click.stop="handleEdit(data)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button type="danger" link size="small" @click.stop="handleDelete(data)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </span>
            </span>
          </template>
        </el-tree>
      </div>
      <div class="permission-tree-right">
        <el-card v-if="currentPermission">
          <template #header>
            <span>权限详情</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="权限编码">
              {{ currentPermission.permissionCode }}
            </el-descriptions-item>
            <el-descriptions-item label="权限名称">
              {{ currentPermission.permissionName }}
            </el-descriptions-item>
            <el-descriptions-item label="权限类型">
              <el-tag :type="currentPermission.permissionType === 'function' ? 'primary' : 'warning'">
                {{ currentPermission.permissionType === 'function' ? '功能权限' : '数据权限' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="层级">
              {{ currentPermission.level }}级
            </el-descriptions-item>
            <el-descriptions-item label="资源类型" :span="2">
              {{ currentPermission.resourceType || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="资源路径" :span="2">
              {{ currentPermission.resourcePath || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="currentPermission.status === 1 ? 'success' : 'danger'">
                {{ currentPermission.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ currentPermission.createTime }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-empty v-else description="请选择一个权限节点" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属服务" prop="serviceId">
          <el-select v-model="form.serviceId" placeholder="请选择所属服务" style="width: 100%">
            <el-option
              v-for="service in serviceList"
              :key="service.id"
              :label="service.serviceName"
              :value="service.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="treeProps"
            check-strictly
            placeholder="请选择上级权限（不选则为一级权限）"
            style="width: 100%"
            :disabled="isEdit"
            clearable
          />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-radio-group v-model="form.permissionType">
            <el-radio value="function">功能权限</el-radio>
            <el-radio value="data">数据权限</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select v-model="form.resourceType" placeholder="请选择资源类型" clearable style="width: 100%">
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="接口" value="api" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源路径" prop="resourcePath">
          <el-input v-model="form.resourcePath" placeholder="请输入资源路径" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionTree, createPermission, updatePermission, deletePermission, getServiceList } from '@/api'

const currentServiceId = ref(null)
const currentPermissionType = ref(null)
const currentPermission = ref(null)
const serviceList = ref([])
const treeData = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const treeProps = {
  children: 'children',
  label: 'permissionName'
}

const form = reactive({
  id: null,
  serviceId: null,
  parentId: null,
  permissionCode: '',
  permissionName: '',
  permissionType: 'function',
  resourceType: '',
  resourcePath: '',
  status: 1,
  sort: 0
})

const rules = {
  serviceId: [
    { required: true, message: '请选择所属服务', trigger: 'change' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ],
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

const dialogTitle = ref('新增权限')

const loadServices = async () => {
  try {
    const res = await getServiceList()
    serviceList.value = res.data || []
  } catch (e) {
    console.error('加载服务列表失败', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (currentServiceId.value) {
      params.serviceId = currentServiceId.value
    }
    if (currentPermissionType.value) {
      params.permissionType = currentPermissionType.value
    }
    const res = await getPermissionTree(params)
    treeData.value = res.data || []
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

const handleNodeClick = (data) => {
  currentPermission.value = data
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增权限'
  form.id = null
  form.serviceId = currentServiceId.value || (serviceList.value[0]?.id || null)
  form.parentId = null
  form.permissionCode = ''
  form.permissionName = ''
  form.permissionType = 'function'
  form.resourceType = ''
  form.resourcePath = ''
  form.status = 1
  form.sort = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑权限'
  form.id = row.id
  form.serviceId = row.serviceId
  form.parentId = row.parentId
  form.permissionCode = row.permissionCode
  form.permissionName = row.permissionName
  form.permissionType = row.permissionType
  form.resourceType = row.resourceType
  form.resourcePath = row.resourcePath
  form.status = row.status
  form.sort = row.sort
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    currentPermission.value = null
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updatePermission(form)
    } else {
      await createPermission(form)
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
  loadServices()
  loadData()
})
</script>

<style scoped>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.tree-node-action {
  visibility: hidden;
}

.custom-tree-node:hover .tree-node-action {
  visibility: visible;
}
</style>
