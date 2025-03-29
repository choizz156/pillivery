<template>
  <div class="container" :data-pathname="firstPathname">
    <div class="top-container">
      <LeftNav v-if="!hide" />
      <main class="main-container" :class="{ 'no-margin': hide }">
        <slot />
      </main>
      <RightNav v-if="!hide" />
    </div>
    <Footer v-if="!hide" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import LeftNav from './LeftNav.jsx'
import RightNav from './RightNav.jsx'
import Footer from './Footer.jsx'

const route = useRoute()
const hideURL = ['/login', '/signup']
const hide = hideURL.includes(route.path)
const firstPathname = route.path.split('/')[1]
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: fit-content;
  min-height: 100vh;
}

.container[data-pathname="cart"],
.container[data-pathname="pay"] {
  background-color: var(--gray-100);
}

.container[data-pathname="mypage"] {
  background-image: linear-gradient(
    to bottom,
    white 283px,
    var(--gray-100) 0
  );
}

.container[data-pathname=""] {
  background-image: linear-gradient(
    to bottom,
    white 770px,
    var(--gray-100) 0
  );
}

.top-container {
  display: flex;
  width: 100%;
  height: 100%;
  justify-content: space-between;
}

.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 120px;
  padding-bottom: 180px;
  max-width: 1240px;
  width: 100%;
}

.main-container.no-margin {
  margin: 0;
  max-width: none;
  padding-bottom: 0;
}
</style>