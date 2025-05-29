<template>
  <DetailPage
    type="kanji"
    :item="kanji"
    :itemId="kanjiChar"
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
import DetailPage from '@/components/detail-search/DetailSearch.vue';
export default {
  name: 'KanjiDetail',
  components: {
    DetailPage
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const kanjiChar = ref(route.params.kanji);

    // Function to fetch kanji data
    const fetchKanjiData = (char) => {
      store.dispatch('search/getKanjiByKanji', char);
    };

    // Initial fetch
    fetchKanjiData(kanjiChar.value);

    // Watch for route changes
    watch(
      () => route.params.kanji,
      (newKanji) => {
        if (newKanji) {
          kanjiChar.value = newKanji;
          fetchKanjiData(newKanji);
        }
      }
    );

    const kanji = computed(() => store.getters['search/selectedKanji']);

    return {
      kanji,
      kanjiChar
    };
  }
};
</script>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 0px 4px rgba(0, 0, 0, 0.4);
}

.kanji-header {
  text-align: center;
  margin-bottom: 2rem;
}

.kanji-char {
  font-size: 5rem;
  margin-bottom: 1rem;
}

.kanji-info h2 {
  color: #333;
  font-size: 2rem;
  margin-bottom: 1.5rem;
}

.readings {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem;
}

.reading-section {
  flex: 1;
  max-width: 300px;
}

.reading-section h3 {
  color: #666;
  margin-bottom: 0.5rem;
}

.reading-section p {
  font-size: 1.2rem;
  color: #333;
}

.not-found {
  text-align: center;
  margin: 4rem auto;
  color: #666;
}
</style>