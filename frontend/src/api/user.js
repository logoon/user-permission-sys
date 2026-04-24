import request from '@/utils/request'

export function getUserPage(params) {
  return request.get('/users/page', { params })
}

export function getUserById(id) {
  return request.get(`/users/${id}`)
}

export function createUser(data) {
  return request.post('/users', data)
}

export function updateUser(data) {
  return request.put('/users', data)
}

export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

export function getUserRoles(userId) {
  return request.get(`/users/${userId}/roles`)
}

export function getUserRoleIds(userId) {
  return request.get(`/users/${userId}/role-ids`)
}

export function assignUserRoles(userId, roleIds) {
  return request.post(`/users/${userId}/assign-roles`, roleIds)
}
