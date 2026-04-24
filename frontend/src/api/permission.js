import request from '@/utils/request'

export function getPermissionList(params) {
  return request.get('/permissions/list', { params })
}

export function getPermissionTree(params) {
  return request.get('/permissions/tree', { params })
}

export function getPermissionById(id) {
  return request.get(`/permissions/${id}`)
}

export function createPermission(data) {
  return request.post('/permissions', data)
}

export function updatePermission(data) {
  return request.put('/permissions', data)
}

export function deletePermission(id) {
  return request.delete(`/permissions/${id}`)
}

export function getUserPermissions(userId) {
  return request.get(`/permissions/user/${userId}`)
}

export function getUserPermissionTree(userId) {
  return request.get(`/permissions/user/${userId}/tree`)
}
