<template>
  <DetailPage
    type="grammar"
    :item="grammarData"
    :itemId="grammarId"
    mainField="structure"
    readingField="vietnamesePronunciation"
    meaningField="explanation"
    notFoundMessage="Không tìm thấy ngữ pháp"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import grammarApi from '@/api/modules/grammarApi';

export default {
  name: 'GrammarDetail',
  components: {
    DetailPage
  },
  setup() {
    const route = useRoute();
    const grammarId = ref(route.params.id);
    const grammarData = ref(null);
    const isLoading = ref(false);

    // Function to fetch grammar data
    const fetchGrammarData = async (id) => {
      try {
        console.log('Fetching grammar data for ID:', id);
        isLoading.value = true;
        const response = await grammarApi.getById(id);
        console.log('Grammar API response:', response);
        console.log('Grammar response keys:', Object.keys(response || {}));
        console.log('Grammar ID fields check:', {
          id: response?.id,
          grammarId: response?.grammarId,
          grammaID: response?.grammaID
        });
        grammarData.value = response;
        console.log('Grammar data set:', grammarData.value);
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