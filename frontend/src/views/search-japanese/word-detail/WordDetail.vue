<template>
  <DetailPage
    type="word"
    :item="selectedWord"
    :itemId="wordId"
    mainField="kanji"
    readingField="kana"
    meaningField="meaning"
    :showRelated="true"
    :relatedItems="relatedKanji"
    relatedTitle="Hán tự liên quan"
    relatedMainField="kanji"
    relatedKeyField="kanji"
    emptyRelatedMessage="Không có Hán tự liên quan"
    notFoundMessage="Không tìm thấy từ vựng"
    @relatedItemClick="navigateToKanjiDetail"
  />
</template>

<script>
import { computed, watch, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';

export default {
  name: 'WordDetail',
  components: {
    DetailPage
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const wordId = ref(parseInt(route.params.id));

    // Function to fetch word data
    const fetchWordData = (id) => {
      store.dispatch('search/getWordById', parseInt(id));
    };

    // Initial fetch
    fetchWordData(wordId.value);

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          wordId.value = parseInt(newId);
          fetchWordData(wordId.value);
        }
      }
    );

    const selectedWord = computed(() => store.getters['search/selectedWord']);
    const relatedKanji = computed(() => {
      return store.getters['search/getRelatedKanjiList'](selectedWord.value);
    });

    const navigateToKanjiDetail = (kanji) => {
      store.dispatch('search/getKanjiByKanji', kanji.kanji);
      router.push({ name: 'KanjiDetail', params: { kanji: kanji.kanji } });
    };

    return {
      selectedWord,
      relatedKanji,
      navigateToKanjiDetail,
      wordId
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/word-detail/WordDetail.scss';
</style>