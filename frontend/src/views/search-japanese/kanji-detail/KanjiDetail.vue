<template>
  <DetailPage
    type="kanji"
    :item="kanji"
    :itemId="kanjiId"
    mainField="kanji"
    readingField="reading"
    meaningField="kanjiname"
    notFoundMessage="Không tìm thấy Hán tự"
  />
</template>

<script>
import { computed, watch, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
export default {
  name: 'KanjiDetail',
  components: {
    DetailPage
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const kanjiId = ref(parseInt(route.params.id));

    // Function to fetch kanji data
    const fetchKanjiData = (id) => {
      store.dispatch('search/getKanjiById', parseInt(id));
    };

    // Initial fetch
    fetchKanjiData(kanjiId.value);

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          kanjiId.value = parseInt(newId);
          fetchKanjiData(kanjiId.value);
        }
      }
    );

    const kanji = computed(() => store.getters['search/selectedKanji']);

    return {
      kanji,
      kanjiId
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/kanji-detail/KanjiDetail.scss';
</style>