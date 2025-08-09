<template>
  <DetailPage
    type="word"
    :item="wordData"
    :itemId="wordId"
    mainField="words"
    readingField="vietnamese_pronunciation"
    meaningField="meanning"
    :notFoundMessage="t('wordDetail.notFound')"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import vocabularyApi from '@/api/modules/vocabularyApi';

export default {
  name: 'WordDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const router = useRouter();
    const wordId = ref(route.params.id);
    const wordData = ref(null);
    const isLoading = ref(false);

    // Function to fetch word data
    const fetchWordData = async (id) => {
      try {
        isLoading.value = true;
        const response = await vocabularyApi.getById(id);
        wordData.value = response;
      } catch (error) {
        console.error('Error fetching word data:', error);
        wordData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    // Initial fetch
    onMounted(() => {
      if (wordId.value) {
        fetchWordData(wordId.value);
      }
    });

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          wordId.value = newId;
          fetchWordData(wordId.value);
        }
      }
    );

    return {
      t,
      wordData,
      wordId,
      isLoading
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/word-detail/WordDetail.scss';
</style>