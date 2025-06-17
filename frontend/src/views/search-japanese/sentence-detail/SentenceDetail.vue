<template>
  <DetailPage
    type="sentence"
    :item="selectedSentence"
    :itemId="sentenceId"
    mainField="sentence"
    readingField="reading"
    meaningField="translation"
    notFoundMessage="Không tìm thấy câu ví dụ"
  />
</template>

<script>
import { computed, watch, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';

export default {
  name: 'SentenceDetail',
  components: {
    DetailPage
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const sentenceId = ref(parseInt(route.params.id));

    // Function to fetch sentence data
    const fetchSentenceData = (id) => {
      store.dispatch('search/getSentenceById', parseInt(id));
    };

    // Initial fetch
    fetchSentenceData(sentenceId.value);

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          sentenceId.value = parseInt(newId);
          fetchSentenceData(sentenceId.value);
        }
      }
    );

    const selectedSentence = computed(() => store.getters['search/selectedSentence']);

    return {
      selectedSentence,
      sentenceId
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/sentence-detail/SentenceDetail.scss';
</style>