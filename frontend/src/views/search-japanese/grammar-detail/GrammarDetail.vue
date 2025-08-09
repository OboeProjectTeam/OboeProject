<template>
  <DetailPage
    type="grammar"
    :item="grammarData"
    :itemId="grammarId"
    mainField="structure"
    readingField="vietnamesePronunciation"
    meaningField="explanation"
    :notFoundMessage="t('grammarDetail.notFound')"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import grammarApi from '@/api/modules/grammarApi';

export default {
  name: 'GrammarDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const grammarId = ref(route.params.id);
    const grammarData = ref(null);
    const isLoading = ref(false);

    // Function to fetch grammar data
    const fetchGrammarData = async (id) => {
      try {
        isLoading.value = true;
        const response = await grammarApi.getById(id);
        grammarData.value = response;
      } catch (error) {
        console.error('Error fetching grammar data:', error);
        grammarData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    // Initial fetch
    onMounted(() => {
      if (grammarId.value) {
        fetchGrammarData(grammarId.value);
      }
    });

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          grammarId.value = newId;
          fetchGrammarData(grammarId.value);
        }
      }
    );

    return {
      t,
      grammarData,
      grammarId,
      isLoading
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-japanese/grammar-detail/GrammarDetail.scss';
</style>