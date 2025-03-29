<template>
  <Box>
    <PageTitle
      :title="processedCategory"
    />
    <Brand>
      <BrandsWindow />
    </Brand>
    <ItemListBox>
      <div v-for="(page, index) in data?.pages" :key="index">
        <SmallListCards
          v-for="item in page.data"
          :key="item.itemId"
          :item="item"
        />
      </div>
    </ItemListBox>
    <LoadingSpinner v-if="isFetchingNextPage" />
    <div ref="scrollTarget" />
  </Box>
</template>

<script setup>
import { ref, watchEffect, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import PageTitle from '../components/Etc/PageTitle.vue';
import BrandsWindow from '../components/Etc/BrandsWindow.vue';
import SmallListCards from '../components/Lists/SmallListCards.vue';
import LoadingSpinner from '../components/Etc/LoadingSpinner.vue';
import { useGetList } from '../hooks/useGetList';
import { useInView } from '@vueuse/core';
import { storeToRefs } from 'pinia';
import { useFilterStore } from '../stores/filter';
import paramsMaker from '../utils/paramsMaker';

const route = useRoute();
const { sort, price, brand, onSale } = storeToRefs(useFilterStore());
const { path: filterPath, query: filterQuery } = paramsMaker(sort.value, price.value, brand.value, onSale.value);
const category = ref(route.query.categoryName || 'all');

const scrollTarget = ref(null);
const { stop } = useInView(scrollTarget, async (inView) => {
  if (inView) await fetchNextPage();
});

const { 
  data, 
  status, 
  fetchNextPage, 
  isFetchingNextPage, 
  refetch 
} = useGetList(route.path, category.value, filterPath, filterQuery);

const processedCategory = computed(() => {
  if (category.value === '관절_뼈_건강') return '관절/뼈 건강';
  return category.value.split('_').join(' ');
});

const handleFilterClear = async () => {
  await useFilterStore().setClear();
  window.scrollTo({ top: 0, behavior: 'auto' });
  await refetch();
};

watchEffect(() => {
  category.value = route.query.categoryName || 'all';
  handleFilterClear();
});

watch([price, sort, brand, onSale], () => {
  refetch();
});
</script>

<style scoped>
.Box {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.Brand {
  border: none;
  background-color: #f2f2f2;
  width: 1115px;
  height: 138px;
  display: flex;
  align-items: center;
}

.ItemListBox {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  justify-content: center;
  margin-top: 100px;
}
</style>
