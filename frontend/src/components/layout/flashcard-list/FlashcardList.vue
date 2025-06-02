<template>
  <div class="flashcard-container">
    <button 
      class="flashcard-btn" 
      @click="toggleList"
      :class="{ 'active': isOpen }"
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
import { ref, computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const isOpen = ref(false)
const activeTab = ref('word')

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

<style scoped>
.flashcard-container {
  position: fixed;
  top: 200px; 
  right: 80px;
  z-index: 1000;
}

.flashcard-btn {
  position: relative;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #E94560;
  border: none;
  color: white;
  font-size: 1.25rem;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.flashcard-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.flashcard-btn.active {
  background: #d63d56;
}

.item-count {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #333;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.flashcard-list {
  position: absolute;
  top: 60px;
  right: 0;
  width: 350px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  max-height: 55vh;
}

.list-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
}

.list-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 4px;
  font-size: 1.1rem;
}

.close-btn:hover {
  color: #333;
}

.list-tabs {
  display: flex;
  padding: 12px;
  gap: 8px;
  border-bottom: 1px solid #eee;
  background: white;
}

.tab-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tab-count {
  position: absolute;
  top: -8px;
  right: -3px;
  background: #333;
  color: white;
  font-size: 0.7rem;
  padding: 1px 5px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
  z-index: 1;
}

.tab-btn {
  background: none;
  border: none;
  padding: 8px 12px;
  border-radius: 16px;
  color: #666;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s ease;
  position: relative;
}

.tab-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.tab-btn.active {
  background: #E94560;
  color: white;
}

.items-list {
  padding: 12px;
  overflow-y: auto;
  max-height: calc(50vh - 110px); /* Trừ đi chiều cao của header và tabs */
}

/* Custom scrollbar styles */
.items-list::-webkit-scrollbar {
  width: 8px;
}

.items-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.items-list::-webkit-scrollbar-thumb {
  background: #E94560;
  border-radius: 4px;
}

.items-list::-webkit-scrollbar-thumb:hover {
  background: #d63d56;
}

.flashcard-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-radius: 8px;
  background: #f8f8f8;
  margin-bottom: 8px;
  width: 100%;
  box-sizing: border-box;
}

.item-content {
  flex: 1;
  min-width: 0;
  margin-right: 12px;
}

.item-main {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 4px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.item-sub {
  font-size: 0.9rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.remove-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 8px;
  font-size: 1rem;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background: #fee;
  color: #E94560;
}

.empty-message {
  padding: 24px;
  text-align: center;
  color: #666;
}

.list-footer {
  padding: 16px;
  border-top: 1px solid #eee;
  background: white;
}

.view-all-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px;
  background: #E94560;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.view-all-btn:hover {
  background: #d63d56;
  transform: translateY(-1px);
}

.view-all-btn i {
  font-size: 0.9em;
}
</style> 