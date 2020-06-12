import request from '@/utils/request'

export function listConfig(params) {
  return request({
    url: '/config/list',
    method: 'get',
    params
  })
}

export function writeConfig(data) {
  return request({
    url: '/config/write',
    method: 'post',
    data
  })
}

export function deleteConfig(params) {
  return request({
    url: '/config/delete',
    method: 'get',
    params
  })
}
