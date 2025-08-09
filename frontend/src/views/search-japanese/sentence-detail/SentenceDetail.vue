<template>
  <DetailPage
    type="sentence"
    :item="sentenceData"
    :itemId="sentenceId"
    mainField="japaneseText"
    readingField=""
    meaningField="vietnameseMeaning"
    :notFoundMessage="t('sentenceDetail.notFound')"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import sampleSentenceApi from '@/api/modules/sampleSentenceApi';

export default {
  name: 'SentenceDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const sentenceId = ref(route.params.id);
    const sentenceData = ref(null);
    const isLoading = ref(false);

    // Function to fetch sentence data
    const fetchSentenceData = async (id) => {
      try {
        isLoading.value = true;
        const response = await sampleSentenceApi.getById(id);
        sentenceData.value = response;
      } catch (error) {
        console.error('Error fetching sentence data:', error);
        sentenceData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    // Initial fetch
    onMounted(() => {
      if (sentenceId.value) {
        fetchSentenceData(sentenceId.value);
      }
    });

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          sentenceId.value = newId;
          fetchSentenceData(sentenceId.value);
        }
      }
    );

    return {
      t,
      sentenceData,
      sentenceId,
      isLoading
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/sentence-detail/SentenceDetail.scss';
</style>