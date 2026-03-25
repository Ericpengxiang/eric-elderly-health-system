import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function refreshToken(data) {
  return request.post('/auth/refresh', data)
}

export function fetchMe() {
  return request.get('/users/me')
}
