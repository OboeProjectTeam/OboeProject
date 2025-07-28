<template>
  <DetailPage
    type="word"
    :item="wordData"
    :itemId="wordId"
    mainField="words"
    readingField=""
    meaningField="meanning"
    notFoundMessage="Không tìm thấy từ vựng"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import vocabularyApi from '@/api/modules/vocabularyApi';

export default {
  name: 'WordDetail',
  components: {
    DetailPage
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const wordId = ref(route.params.id);
    const wordData = ref(null);
    const isLoading = ref(false);

    // Function to fetch word data
    const fetchWordData = async (id) => {
      try {
        isLoading.value = true;
        console.log('Fetching word data for ID:', id); // Debug log
        const response = await vocabularyApi.getById(id);
        console.log('Word API response:', response); // Debug log
        wordData.value = response;
        console.log('Word data set to:', wordData.value); // Debug log
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