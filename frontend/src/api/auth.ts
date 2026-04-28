import apiClient from './client'

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: { id: number; email: string; name: string; role: string }
}

export const authApi = {
  register: (data: { email: string; password: string; name: string; phone: string }) =>
    apiClient.post<AuthResponse>('/auth/register', data).then((r) => r.data),

  login: (data: { email: string; password: string }) =>
    apiClient.post<AuthResponse>('/auth/login', data).then((r) => r.data),

  me: () =>
    apiClient.get<AuthResponse['user']>('/auth/me').then((r) => r.data),
}
