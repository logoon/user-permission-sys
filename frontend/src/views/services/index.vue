<template>
  <div class="page-container">
    <div class="page-header">
      <span class="page-title">服务管理</span>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增服务
      </el-button>
    </div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="服务名称" class="search-form-item">
        <el-input v-model="searchForm.serviceName" placeholder="请输入服务名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="服务编码" class="search-form-item">
        <el-input v-model="searchForm.serviceCode" placeholder="请输入服务编码" clearable @keyup.enter="handleSearch" />
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
      <el-table-column prop="serviceCode" label="服务编码" width="150" />
      <el-table-column prop="serviceName" label="服务名称" width="180" />
      <el-table-column prop="serviceUrl" label="服务地址" />
      <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :class="row.status === 1 ? 'status-tag-active' : 'status-tag-inactive'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="服务编码" prop="serviceCode">
          <el-input v-model="form.serviceCode" placeholder="请输入服务编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="服务名称" prop="serviceName">
          <el-input v-model="form.serviceName" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务地址" prop="serviceUrl">
          <el-input v-model="form.serviceUrl" placeholder="请输入服务地址" />
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
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getServicePage, createService, updateService, deleteService } from '@/api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  serviceName: '',
  serviceCode: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  serviceCode: '',
  serviceName: '',
  serviceUrl: '',
  status: 1,
  description: ''
})

const rules = {
  serviceCode: [
    { required: true, message: '请输入服务编码', trigger: 'blur' }
  ],
  serviceName: [
    { required: true, message: '请输入服务名称', trigger: 'blur' }
  ]
}

const dialogTitle = ref('新增服务')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getServicePage({
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
  searchForm.serviceName = ''
  searchForm.serviceCode = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增服务'
  form.id = null
  form.serviceCode = ''
  form.serviceName = ''
  form.serviceUrl = ''
  form.status = 1
  form.description = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑服务'
  form.id = row.id
  form.serviceCode = row.serviceCode
  form.serviceName = row.serviceName
  form.serviceUrl = row.serviceUrl
  form.status = row.status
  form.description = row.description
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该服务吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteService(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateService(form)
    } else {
      await createService(form)
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
})
</script>
