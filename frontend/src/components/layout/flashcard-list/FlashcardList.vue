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
        <h3>Danh sách Flashcard</h3>
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
        Chưa có item nào trong danh sách {{ getActiveTabLabel }}
      </div>

      <div class="list-footer">
        <router-link to="/flashcard" class="view-all-btn" @click="toggleList">
          <i class="fas fa-external-link-alt"></i>
          Đi tới trang Flashcard
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
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

const tabs = [
  { type: 'word', label: 'Từ vựng' },
  { type: 'kanji', label: 'Hán tự' },
  { type: 'grammar', label: 'Ngữ pháp' },
  { type: 'sentence', label: 'Mẫu câu' }
]

const toggleList = () => {
  isOpen.value = !isOpen.value
}

// TODO: Replace with actual store getters
const flashcardItems = computed(() => store.state.flashcard?.items || [])

const filteredItems = computed(() => {
  return flashcardItems.value.filter(item => item.type === activeTab.value)
})

const getActiveTabLabel = computed(() => {
  return tabs.find(tab => tab.type === activeTab.value)?.label
})

const getMainText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word':
      return item.kanji || item.kana
    case 'kanji':
      return item.kanji
    case 'grammar':
      return item.kana
    case 'sentence':
      text = item.sentence || item.japanese || '';
      return text.length > 20 ? text.substring(0, 20) + '...' : text
    default:
      return ''
  }
}

const getSubText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word':
      return item.meaning
    case 'kanji':
      return item.reading
    case 'grammar':
      return item.meaning
    case 'sentence':
      text = item.translation || item.meaning || '';
      return text.length > 25 ? text.substring(0, 25) + '...' : text
    default:
      return ''
  }
}

const removeFromFlashcard = (item) => {
  store.dispatch('flashcard/removeItem', item)
}

const tabsWithCount = computed(() => {
  return tabs.map(tab => ({
    ...tab,
    count: store.getters['flashcard/getItemsByType'](tab.type).length
  }))
})

const totalItems = computed(() => flashcardItems.value.length)
</script>

<style lang="scss" scoped>
@use '@/components/layout/flashcard-list/FlashcardList.scss';
</style> 