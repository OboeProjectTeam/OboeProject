<template>
  <div class="search-bar">
    <input
      type="text"
      v-model="searchQuery"
      @input="onSearch"
      :placeholder="placeholder"
    />
    <ul v-if="filteredSuggestions.length && showSuggestions" class="suggestions">
      <li
        v-for="(item, index) in filteredSuggestions"
        :key="index"
        class="suggestion-item"
        @click="selectSuggestion(item)"
      >
        <div v-if="isWord">
          <strong>{{ item.kanji }}</strong>
          <span class="kana">{{ item.kana }}</span>
        </div>
        <div v-else-if="isSentence">
          <span>{{ item.sentence }}</span>
        </div>
        <div v-else-if="isKanji">
          <strong>{{ item.kanji }}</strong>
          <span class="kana">{{ item.kunyomi }}</span>
        </div>
        <div v-else-if="isGrammar">
          <strong>{{ item.kana }}</strong>
        </div>
        <div class="meaning">{{ getMeaning(item) }}</div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'

const props = defineProps({
  placeholder: {
    type: String,
    default: 'Tìm kiếm...'
  }
})

const emit = defineEmits(['search'])
const searchQuery = ref('')
const showSuggestions = ref(false)

const store = useStore()
const activeIndex = computed(() => store.getters['header/activeIndex'])

const wordList = computed(() => store.getters['search/wordList'] || [])
const sentenceList = computed(() => store.state.search.sentenceList || [])
const kanjiList = computed(() => store.state.search.KanjiList || [])
const grammarList = computed(() => store.state.search.grammar || [])

const isWord = computed(() => activeIndex.value === 0)
const isKanji = computed(() => activeIndex.value === 1)
const isGrammar = computed(() => activeIndex.value === 2)
const isSentence = computed(() => activeIndex.value === 3)

const getMeaning = (item) => {
  if (isWord.value || isGrammar.value) return item.meaning
  if (isSentence.value) return item.translation
  if (isKanji.value) return `${item.reading}`
  return ''
}

const filteredSuggestions = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  if (!query) return []

  if (isWord.value) {
    return wordList.value.filter(
      (word) =>
        word.kana.includes(query) ||
        word.kanji.includes(query) ||
        word.romaji.toLowerCase().includes(query) ||
        word.meaning.toLowerCase().includes(query)
    )
  } else if (isSentence.value) {
    return sentenceList.value.filter(
      (sentence) =>
        sentence.sentence.includes(query) ||
        sentence.translation.toLowerCase().includes(query)
    )
  } else if (isKanji.value) {
    return kanjiList.value.filter(
      (kanji) =>
        kanji.kanji.includes(query) ||
        kanji.kunyomi.includes(query) ||
        kanji.reading.includes(query) ||
        kanji.kanjiname.toLowerCase().includes(query)
    )
  } else if (isGrammar.value) {
    return grammarList.value.filter(
      (grammar) =>
        grammar.kana.includes(query) ||
        grammar.romaji.toLowerCase().includes(query) ||
        grammar.meaning.toLowerCase().includes(query)
    )
  }
  return []
})

const onSearch = () => {
  showSuggestions.value = searchQuery.value.length > 0
  emit('search', searchQuery.value)
}

const selectSuggestion = (item) => {
  searchQuery.value = isSentence.value ? item.sentence : item.kanji || item.kana || ''
  showSuggestions.value = false
  emit('search', searchQuery.value)
}
</script>

<style scoped>
.search-bar {
  width: 525px;
  position: relative;
}

input {
  width: 100%;
  padding: 8px;
  font-size: 1rem;
  border: none;
  border-bottom: 1px solid black;
}

input:focus {
  border: none;
  border-bottom: 2px solid black;
  outline: none;
  box-shadow: none;
}

.suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ccc;
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
  padding: 0;
  margin: 0;
  list-style: none;
}

.suggestion-item {
  padding: 10px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.suggestion-item:hover {
  background-color: #f0f0f0;
}

.kana {
  margin-left: 8px;
  color: #555;
}

.meaning {
  font-size: 13px;
  color: #666;
}
</style>