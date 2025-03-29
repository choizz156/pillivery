<template>
  <div>
    <!-- <MainCaroucel /> -->
    <template>
      <MainSection
        :items="bestItems"
        :section-title="sectionTitles[0]"
      />
      <MainSection
        :items="saleItems"
        :section-title="sectionTitles[1]"
      />
      <MainSection
        :items="newItems"
        :section-title="sectionTitles[2]"
      />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 존재하지 않는 파일은 임포트 주석 처리
// import MainCaroucel from '@/components/Caroucel/MainCaroucel.js'
import MainSection from './MainSection.jsx'
// LoadingSpinner도 주석 처리
// import { LoadingSpinner } from '@/components/Etc/LoadingSpinner.js'
import { useGet } from '@/hooks/useFetch'

const route = useRoute()
const userStore = useUserStore()

const sectionTitles = [
  ['Best', '인기 많은 상품만 모았어요!'],
  ['On Sale', '할인 중인 상품만 모았어요!'],
  ['New Arrival', '새로운 영양제를 만나보세요!']
]

const isLoading = ref(false) // 로딩 상태를 false로 설정
const bestItems = ref([])
const saleItems = ref([])
const newItems = ref([])

onMounted(async () => {
  const url = new URL(window.location.href)
  const accessToken = url.searchParams.get('access_token')
  const refreshToken = url.searchParams.get('refresh_token')
  const userId = url.searchParams.get('userId')

  if (accessToken) {
    userStore.login({ accessToken, refreshToken, isSocial: true, userId })
  }

  try {
    const { data } = await useGet('/main', route.path)
    bestItems.value = data?.data?.bestItem?.data || []
    saleItems.value = data?.data?.saleItem?.data || []
    newItems.value = data?.data?.mdPickItem?.data || []
    isLoading.value = false
  } catch (error) {
    console.error('Failed to fetch main page data:', error)
  }
})
</script>

<style scoped>
.home-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}
.section {
  margin-bottom: 2rem;
  padding: 1rem;
  border-radius: 8px;
  background-color: #f8f8f8;
}
</style>