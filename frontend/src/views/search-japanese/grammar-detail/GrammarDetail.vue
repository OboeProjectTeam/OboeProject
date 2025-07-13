<template>
  <DetailPage
    type="grammar"
    :item="selectedGrammar"
    :itemId="grammarId"
    mainField="kana"
    readingField="romaji"
    meaningField="meaning"
    notFoundMessage="Không tìm thấy ngữ pháp"
  />
</template>

<script>
import { computed, watch, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';

export default {
  name: 'GrammarDetail',
  components: {
    DetailPage
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const grammarId = ref(parseInt(route.params.id));

    // Function to fetch grammar data
    const fetchGrammarData = (id) => {
      store.dispatch('search/getGrammarById', parseInt(id));
    };

    // Initial fetch
    fetchGrammarData(grammarId.value);

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          grammarId.value = parseInt(newId);
          fetchGrammarData(grammarId.value);
        }
      }
    );

    const selectedGrammar = computed(() => store.getters['search/selectedGrammar']);

    return {
      selectedGrammar,
      grammarId
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/grammar-detail/GrammarDetail.scss';
</style>