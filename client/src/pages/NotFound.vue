<template>
  <div class="container">
    <svg
      aria-hidden="true"
      class="mtn48 sm:d-none svg-spot spotAlertXL"
      width="196"
      height="196"
      viewBox="0 0 196 196"
    >
      <!-- SVG paths -->
    </svg>
    <div class="text-container">
      <h1>Page not found</h1>
      <h2>요청하신 페이지를 찾을 수 없습니다.</h2>
      <p>
        새로운 상품을 찾고 계신가요?
        <router-link to="/">
          <strong>Home</strong>
        </router-link>
      </p>
      <p>
        카테고리로 찾아보세요.
        <router-link to="/list?categoryName=%EA%B8%B0%ED%83%80">
          <strong>카테고리 목록</strong>
        </router-link>
      </p>
      <p>
        로그인을 하셨나요?
        <router-link to="/login">
          <strong>로그인</strong>
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;
}

.text-container {
  display: flex;
  flex-direction: column;
  margin-left: 30px;
  color: #232629;
}

.text-container h1 {
  font-size: 27px;
  margin-bottom: 10px;
}

.text-container h2 {
  font-size: 19px;
  margin-bottom: 30px;
}

.text-container p {
  font-size: 15px;
  margin-bottom: 20px;
}

.text-container strong {
  color: #0074cc;
  cursor: pointer;
}

.text-container strong:hover {
  color: var(--blue-400);
}
</style>
