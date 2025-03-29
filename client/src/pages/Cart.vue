<template>
  <div class="container">
    <PageTitle title="장바구니" />
    <RouterView />
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PageTitle from '../components/Etc/PageTitle.jsx';

const route = useRoute();
const router = useRouter();

onMounted(() => {
  if (route.path === '/cart') {
    router.replace('/cart/normal');
  }
});
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
</style>
