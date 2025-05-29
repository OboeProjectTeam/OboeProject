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
import DetailPage from '@/components/detail-search/DetailSearch.vue';

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

<style scoped>
.sentence-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.sentence-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 0px 4px rgba(0, 0, 0, 0.4);
  padding: 24px;
}

.sentence-info {
  margin-bottom: 40px;
  text-align: center;
  padding: 40px 0;
}

.sentence-japanese {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 24px;
  color: #333;
  line-height: 1.4;
}

.sentence-reading {
  font-size: 1.5rem;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.4;
}

.sentence-meaning {
  font-size: 1.25rem;
  color: #444;
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.6;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 16px;
}
.not-found {
  text-align: center;
  color: #666;
  padding: 24px;
  font-size: 1.25rem;
}
</style>