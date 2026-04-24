import request from '@/utils/request'

export function getServicePage(params) {
  return request.get('/services/page', { params })
}

export function getServiceList() {
  return request.get('/services/list')
}

export function getServiceById(id) {
  return request.get(`/services/${id}`)
}

export function createService(data) {
  return request.post('/services', data)
}

export function updateService(data) {
  return request.put('/services', data)
}

export function deleteService(id) {
  return request.delete(`/services/${id}`)
}
