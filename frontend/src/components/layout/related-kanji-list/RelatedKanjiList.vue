<template>
  <div class="mt-6">
    <h3 class="text-lg font-semibold mb-3">{{ t('kanji.relatedKanji') }}</h3>
    <div v-if="relatedKanji.length > 0" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div 
        v-for="kanji in relatedKanji" 
        :key="kanji.kanji"
        class="border border-gray-200 bg-white rounded-lg shadow p-4 cursor-pointer hover:bg-gray-50 transition-colors"
        @click="navigateToKanjiDetail(kanji.kanji)"
      >
        <div class="text-3xl font-bold mb-2">{{ kanji.kanji }}</div>
        <div class="text-sm">
          <div><span class="font-medium">{{ t('kanji.onReading') }}:</span> {{ kanji.reading }}</div>
          <div><span class="font-medium">{{ t('kanji.kunReading') }}:</span> {{ kanji.kunyomi }}</div>
        </div>
      </div>
    </div>
    <div v-else class="text-gray-500">
      {{ t('kanji.noRelatedKanji') }}
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
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
    const { t } = useI18n();

    const relatedKanji = computed(() => {
      return store.getters['search/getRelatedKanjiList'](props.word);
    });

    const navigateToKanjiDetail = (kanjiChar) => {
      store.dispatch('search/getKanjiByKanji', kanjiChar);
      router.push({ name: 'KanjiDetail', params: { id: kanjiChar } });
    };

    return {
      relatedKanji,
      navigateToKanjiDetail,
      t
    };
  }
};
</script>