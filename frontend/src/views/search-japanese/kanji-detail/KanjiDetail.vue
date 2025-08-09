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
    :relatedTitle="t('kanjiDetail.relatedKanji')"
    relatedMainField="characterName"
    relatedKeyField="kanjiId"
    :emptyRelatedMessage="t('kanjiDetail.noRelatedKanji')"
    :notFoundMessage="t('kanjiDetail.notFound')"
    @relatedItemClick="navigateToKanjiDetail"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import kanjiApi from '@/api/modules/kanjiApi';

export default {
  name: 'KanjiDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n();
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
      t,
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