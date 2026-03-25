import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    accessToken: '',
    refreshToken: '',
    user: null,
  }),
  persist: {
    paths: ['accessToken', 'refreshToken', 'user'],
  },
  actions: {
    setTokens(accessToken, refreshToken) {
      this.accessToken = accessToken
      this.refreshToken = refreshToken
    },
    setUser(user) {
      this.user = user
    },
    logout() {
      this.accessToken = ''
      this.refreshToken = ''
      this.user = null
    },
  },
})
