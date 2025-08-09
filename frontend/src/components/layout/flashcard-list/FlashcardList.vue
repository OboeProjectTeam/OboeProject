<template>
  <div class="flashcard-container" :style="containerStyle">
    <button 
      class="flashcard-btn" 
      @click="handleButtonClick"
      :class="{ 'active': isOpen }"
      @mousedown="startDrag"
      @touchstart="startDrag"
    >
      <i class="fas fa-book"></i>
      <span v-if="totalItems > 0" class="item-count">{{ totalItems }}</span>
    </button>

    <!-- Dropdown List -->
    <div v-if="isOpen" class="flashcard-list">
      <div class="list-header">
        <h3>{{ t('flashcard.list') }}</h3>
        <button class="close-btn" @click="toggleList">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="list-tabs">
        <div 
          v-for="tab in tabsWithCount" 
          :key="tab.type"
          class="tab-wrapper"
        >
          <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
          <button 
            class="tab-btn"
            :class="{ 'active': activeTab === tab.type }"
            @click="activeTab = tab.type"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div v-if="filteredItems.length > 0" class="items-list">
        <div v-for="item in filteredItems" :key="item.id" class="flashcard-item">
          <div class="item-content">
            <div class="item-main">{{ getMainText(item) }}</div>
            <div class="item-sub">{{ getSubText(item) }}</div>
          </div>
          <button class="remove-btn" @click="removeFromFlashcard(item)">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
      <div v-else class="empty-message">
        {{ t('flashcard.noItems', { type: getActiveTabLabel }) }}
      </div>

      <div class="list-footer">
        <button class="view-all-btn" @click="goToFlashcardLearn">
          <i class="fas fa-external-link-alt"></i>
          {{ t('flashcard.goToPage') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const store = useStore()
const router = useRouter()
const { t } = useI18n()
const isOpen = ref(false)
const activeTab = ref('word')
const isDragging = ref(false)

// Style binding for container position
const containerStyle = computed(() => ({
  transform: `translateY(${position.value.y}px)`
}))

// Drag functionality
let startY = 0
let startPosY = 0

// Separate click handler
const handleButtonClick = (event) => {
  // Nếu không phải đang kéo thì toggle list
  if (!isDragging.value) {
    event.preventDefault() // Ngăn chặn các hành vi mặc định khác
    event.stopPropagation() // Ngăn chặn sự kiện lan truyền
    toggleList()
  }
}

const startDrag = (event) => {
  // Chỉ ngăn scroll khi thực sự đang kéo
  if (event.target.closest('.flashcard-list')) {
    return
  }
  
  isDragging.value = false
  
  // Get initial positions
  if (event.type === 'mousedown') {
    startY = event.clientY
  } else if (event.type === 'touchstart') {
    startY = event.touches[0].clientY
  }
  startPosY = position.value.y

  // Add event listeners
  if (event.type === 'mousedown') {
    document.addEventListener('mousemove', onDrag)
    document.addEventListener('mouseup', stopDrag)
  } else if (event.type === 'touchstart') {
    document.addEventListener('touchmove', onDrag)
    document.addEventListener('touchend', stopDrag)
  }
}

const onDrag = (event) => {
  let currentY
  if (event.type === 'mousemove') {
    currentY = event.clientY
  } else if (event.type === 'touchmove') {
    currentY = event.touches[0].clientY
  }

  const deltaY = Math.abs(currentY - startY)
  if (deltaY > 10) { // Tăng ngưỡng phát hiện drag
    isDragging.value = true
    event.preventDefault() // Chỉ prevent default khi thực sự đang kéo
  }

  if (isDragging.value) {
    const deltaY = currentY - startY
    let newY = startPosY + deltaY

    // Constrain to viewport bounds
    const maxY = window.innerHeight - 100
    const minY = 100
    newY = Math.max(minY, Math.min(newY, maxY))

    position.value.y = newY
  }
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', stopDrag)
}

// Initialize position to a better starting point
const position = ref({ y: 210 }) // Đặt vị trí ban đầu gần top

// Add better position adjustment on resize
const adjustPositionOnResize = () => {
  const maxY = window.innerHeight - 100
  const minY = 100
  const currentY = position.value.y
  
  // If position is outside bounds, animate to nearest valid position
  if (currentY < minY || currentY > maxY) {
    position.value.y = Math.max(minY, Math.min(currentY, maxY))
  }
}

onMounted(() => {
  window.addEventListener('resize', adjustPositionOnResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', adjustPositionOnResize)
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', stopDrag)
})

const tabs = computed(() => [
  { type: 'word', label: t('common.vocabulary') },
  { type: 'kanji', label: t('common.kanji') },
  { type: 'grammar', label: t('common.grammar') },
  { type: 'sentence', label: t('common.sentences') }
])

const toggleList = () => {
  isOpen.value = !isOpen.value
}

// TODO: Replace with actual store getters
const flashcardItems = computed(() => store.state.flashcard?.items || [])

const filteredItems = computed(() => {
  return flashcardItems.value.filter(item => item.type === activeTab.value)
})

const getActiveTabLabel = computed(() => {
  return tabs.value.find(tab => tab.type === activeTab.value)?.label
})

const getMainText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word':
      // WordDetail uses 'words' as mainField
      return item.words || item.kanji || item.kana || item.word || ''
    case 'kanji':
      // KanjiDetail uses 'characterName' as mainField
      return item.characterName || item.kanji || item.character || ''
    case 'grammar':
      // GrammarDetail uses 'structure' as mainField
      return item.structure || item.kana || item.pattern || ''
    case 'sentence':
      // SentenceDetail uses 'japaneseText' as mainField
      text = item.japaneseText || item.sentence || item.japanese || '';
      return text.length > 20 ? text.substring(0, 20) + '...' : text
    default:
      return ''
  }
}

const getSubText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word':
      // WordDetail uses 'meanning' as meaningField (note the typo in API)
      return item.meanning || item.meaning || ''
    case 'kanji':
      // KanjiDetail uses 'meaning' as meaningField and 'vietnamesePronunciation' as readingField
      return item.meaning || item.vietnamesePronunciation || item.reading || ''
    case 'grammar':
      // GrammarDetail uses 'explanation' as meaningField
      return item.explanation || item.meaning || ''
    case 'sentence':
      // SentenceDetail uses 'vietnameseMeaning' as meaningField
      text = item.vietnameseMeaning || item.translation || item.meaning || '';
      return text.length > 25 ? text.substring(0, 25) + '...' : text
    default:
      return ''
  }
}

const removeFromFlashcard = (item) => {
  store.dispatch('flashcard/removeItem', item)
}

const tabsWithCount = computed(() => {
  return tabs.value.map(tab => ({
    ...tab,
    count: store.getters['flashcard/getItemsByType'](tab.type).length
  }))
})

const totalItems = computed(() => flashcardItems.value.length)

// Hàm chuyển hướng đến FlashcardLearn với dữ liệu đã format
const goToFlashcardLearn = async () => {
  // Format dữ liệu từ FlashcardList sang format phù hợp với FlashcardLearn
  const formattedItems = flashcardItems.value.map(item => {
    const baseItem = {
      id: item.id || Math.random().toString(36).substr(2, 9),
      type: item.type,
      status: 'learning'
    };
    
    switch (item.type) {
      case 'word':
        return {
          ...baseItem,
          // FlashcardLearn sử dụng: item.front, item.kanji, item.kana, item.back, item.meaning
          front: item.words || item.kanji || item.kana || item.word || '',
          kanji: item.kanji || item.words || '',
          kana: item.kana || item.reading || '',
          back: item.meanning || item.meaning || '',
          meaning: item.meanning || item.meaning || ''
        };
        
      case 'kanji':
        return {
          ...baseItem,
          // FlashcardLearn sử dụng: item.kanji, item.kanjiname, item.kunyomi
          kanji: item.characterName || item.kanji || item.character || '',
          kanjiname: item.meaning || item.vietnamesePronunciation || '',
          kunyomi: item.reading || item.vietnamesePronunciation || '',
          meaning: item.meaning || ''
        };
        
      case 'grammar':
        return {
          ...baseItem,
          // FlashcardLearn sử dụng: item.kana, item.romaji, item.meaning
          kana: item.structure || item.kana || item.pattern || '',
          romaji: item.romaji || '',
          meaning: item.explanation || item.meaning || ''
        };
        
      case 'sentence':
        return {
          ...baseItem,
          // FlashcardLearn sử dụng: item.sentence, item.translation
          sentence: item.japaneseText || item.sentence || item.japanese || '',
          translation: item.vietnameseMeaning || item.translation || item.meaning || ''
        };
      
      default:
        return {
          ...baseItem,
          front: item.front || item.kanji || item.content || '',
          back: item.back || item.meaning || item.backcontent || '',
          kanji: item.kanji || '',
          meaning: item.meaning || ''
        };
    }
  });
  
  // Lưu dữ liệu đã format vào store
  await store.dispatch('flashcard/setLearningItems', formattedItems);
  
  // Đóng list và chuyển hướng
  toggleList();
  router.push({
    name: 'flashcardLearn',
    query: {
      source: 'flashcard-list',
      title: t('flashcard.list'),
      description: t('flashcard.itemsFromList', { count: totalItems.value })
    }
  });
}
</script>

<style lang="scss" scoped>
@use '@/components/layout/flashcard-list/FlashcardList.scss';
</style>