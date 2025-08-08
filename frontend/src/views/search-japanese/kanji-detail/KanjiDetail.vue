<template>
  <DetailPage
    type="kanji"
    :item="kanjiData"
    :itemId="kanjiId"
    mainField="characterName"
    readingField="vietnamesePronunciation"
    meaningField="meaning"
    :showRelated="true"
    :relatedItems="relatedKanji"
    relatedTitle="Hán tự liên quan"
    relatedMainField="characterName"
    relatedKeyField="kanjiId"
    emptyRelatedMessage="Không có hán tự liên quan"
    notFoundMessage="Không tìm thấy Hán tự"
    @relatedItemClick="navigateToKanjiDetail"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import kanjiApi from '@/api/modules/kanjiApi';

export default {
  name: 'KanjiDetail',
  components: {
    DetailPage
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const kanjiId = ref(route.params.id);
    const kanjiData = ref(null);
    const relatedKanji = ref([]);
    const isLoading = ref(false);

    // Function to fetch kanji data
    const fetchKanjiData = async (id) => {
      try {
        isLoading.value = true;
        const response = await kanjiApi.getById(id);
        kanjiData.value = response;
      } catch (error) {
        console.error('Error fetching kanji data:', error);
        console.error('Error details:', error.message, error.response) // More debug info
        kanjiData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    // Function to fetch related kanji
    const fetchRelatedKanji = async (id) => {
      try {
        const response = await kanjiApi.getRelated(id);
        relatedKanji.value = response || [];
      } catch (error) {
        console.error('Error fetching related kanji:', error);
        relatedKanji.value = [];
      }
    };

    // Navigate to kanji detail
    const navigateToKanjiDetail = (item) => {
      router.push({ name: 'KanjiDetail', params: { id: item.kanjiId || item.id } });
    };

    // Navigate to vocabulary detail
    const navigateToVocabularyDetail = (item) => {
      router.push({ name: 'WordDetail', params: { id: item.vocalbId || item.id } });
    };

    // Initial fetch
    onMounted(() => {
      if (kanjiId.value) {
        fetchKanjiData(kanjiId.value);
        fetchRelatedKanji(kanjiId.value);
      }
    });

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          kanjiId.value = newId;
          fetchKanjiData(kanjiId.value);
          fetchRelatedKanji(kanjiId.value);
        }
      }
    );

    return {
      kanjiData,
      relatedKanji,
      kanjiId,
      isLoading,
      navigateToKanjiDetail,
      navigateToVocabularyDetail
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/kanji-detail/KanjiDetail.scss';
</style>