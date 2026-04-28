import { create } from 'zustand'

interface User {
  id: number
  email: string
  name: string
  role: string
}

interface AuthStore {
  user: User | null
  isAuthenticated: boolean
  setUser: (user: User, token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthStore>((set) => ({
  user: null,
  isAuthenticated: !!localStorage.getItem('accessToken'),
  setUser: (user, token) => {
    localStorage.setItem('accessToken', token)
    set({ user, isAuthenticated: true })
  },
  logout: () => {
    localStorage.removeItem('accessToken')
    set({ user: null, isAuthenticated: false })
  },
}))
