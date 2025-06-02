<template>
  <div class="mt-6">
    <h3 class="text-lg font-semibold mb-3">Hán tự liên quan</h3>
    <div v-if="relatedKanji.length > 0" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div 
        v-for="kanji in relatedKanji" 
        :key="kanji.kanji"
        class="border border-gray-200 bg-white rounded-lg shadow p-4 cursor-pointer hover:bg-gray-50 transition-colors"
        @click="navigateToKanjiDetail(kanji.kanji)"
      >
        <div class="text-3xl font-bold mb-2">{{ kanji.kanji }}</div>
        <div class="text-sm">
          <div><span class="font-medium">Âm on:</span> {{ kanji.reading }}</div>
          <div><span class="font-medium">Âm kun:</span> {{ kanji.kunyomi }}</div>
        </div>
      </div>
    </div>
    <div v-else class="text-gray-500">
      Không có Hán tự liên quan
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { computed } from 'vue';

export default {
  name: 'RelatedKanjiList',
  props: {
    word: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const store = useStore();
    const router = useRouter();

    const relatedKanji = computed(() => {
      return store.getters['search/getRelatedKanjiList'](props.word);
    });

    const navigateToKanjiDetail = (kanjiChar) => {
      store.dispatch('search/getKanjiByKanji', kanjiChar);
      router.push({ name: 'KanjiDetail', params: { id: kanjiChar } });
    };

    return {
      relatedKanji,
      navigateToKanjiDetail
    };
  }
};
</script>