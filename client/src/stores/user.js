import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const isLoggedIn = ref(false)
  const user = ref(null)
  const accessToken = ref(null)
  const refreshToken = ref(null)

  // 로그인 함수
  function login(userData) {
    user.value = userData.userId || null
    accessToken.value = userData.accessToken
    refreshToken.value = userData.refreshToken
    isLoggedIn.value = true
    
    // 토큰 로컬 스토리지에 저장
    localStorage.setItem('accessToken', accessToken.value)
    localStorage.setItem('refreshToken', refreshToken.value)
    
    console.log('로그인 성공:', user.value)
  }

  // 로그아웃 함수
  function logout() {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    isLoggedIn.value = false
    
    // 토큰 로컬 스토리지에서 제거
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    
    console.log('로그아웃 완료')
  }

  // 토큰 갱신 함수
  function refreshUserToken(newToken) {
    accessToken.value = newToken
    localStorage.setItem('accessToken', newToken)
  }

  return {
    user,
    isLoggedIn,
    accessToken,
    refreshToken,
    login,
    logout,
    refreshUserToken
  }
}) 