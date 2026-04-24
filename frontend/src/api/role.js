import request from '@/utils/request'

export function getRolePage(params) {
  return request.get('/roles/page', { params })
}

export function getRoleList() {
  return request.get('/roles/list')
}

export function getRoleListByService(serviceId) {
  return request.get('/roles/list-by-service', { params: { serviceId } })
}

export function getRoleById(id) {
  return request.get(`/roles/${id}`)
}

export function createRole(data) {
  return request.post('/roles', data)
}

export function updateRole(data) {
  return request.put('/roles', data)
}

export function deleteRole(id) {
  return request.delete(`/roles/${id}`)
}

export function getRolePermissions(roleId) {
  return request.get(`/roles/${roleId}/permissions`)
}

export function getRolePermissionIds(roleId) {
  return request.get(`/roles/${roleId}/permission-ids`)
}

export function getAssignablePermissions(roleId) {
  return request.get(`/roles/${roleId}/assignable-permissions`)
}

export function assignRolePermissions(roleId, permissionIds) {
  return request.post(`/roles/${roleId}/assign-permissions`, permissionIds)
}
