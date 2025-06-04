<template>
  <div class="create-flashcard">
    <div class="header-section">
      <button v-if="fromLearningPage" class="back-btn" @click="goBackToLearning">
        <i class="fas fa-arrow-left"></i>
        Quay lại Trang Học
      </button>
      <h1>{{ isEditing ? 'Chỉnh sửa Bộ Thẻ Ghi Nhớ' : 'Tạo Bộ Thẻ Ghi Nhớ Mới' }}</h1>
    </div>
    <div class="form-container">
      <div class="form-group">
        <label>Tên bộ thẻ <span class="required">*</span></label>
        <input 
          v-model="title" 
          type="text" 
          placeholder="Nhập tên bộ thẻ..." 
          :class="{ 'error': showError && !title }"
        />
        <span v-if="showError && !title" class="error-message">
          Vui lòng nhập tên bộ thẻ
        </span>
      </div>
      <div class="form-group">
        <label>Mô tả</label>
        <textarea v-model="description" placeholder="Nhập mô tả về bộ thẻ..."></textarea>
      </div>

      <div class="import-section" v-if="showImport">
        <h2>Nhập dữ liệu</h2>
        <p class="import-instruction">Chép và dán dữ liệu ở đây (từ Word, Excel, Google Docs, v.v)</p>
        <div class="separator-options">
          <div class="separator-group">
            <label>Giữa thuật ngữ và định nghĩa</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="termSeparator" value="tab">
                Tab
              </label>
              <label class="radio-label">
                <input type="radio" v-model="termSeparator" value="comma">
                Phẩy
              </label>
              <label class="radio-label">
                <input type="radio" v-model="termSeparator" value="custom">
                Tùy chỉnh
              </label>
              <input 
                v-if="termSeparator === 'custom'"
                v-model="customTermSeparator"
                type="text"
                class="custom-separator-input"
                placeholder="Nhập ký tự phân cách..."
              />
            </div>
          </div>
          <div class="separator-group">
            <label>Giữa các thẻ</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="cardSeparator" value="newline">
                Dòng mới
              </label>
              <label class="radio-label">
                <input type="radio" v-model="cardSeparator" value="custom">
                Tùy chỉnh
              </label>
              <input 
                v-if="cardSeparator === 'custom'"
                v-model="customCardSeparator"
                type="text"
                class="custom-separator-input"
                placeholder="Nhập ký tự phân cách..."
              />
            </div>
          </div>
        </div>
        <textarea 
          v-model="importText" 
          class="import-textarea"
          placeholder="Từ 1&#9;Định nghĩa 1&#10;Từ 2&#9;Định nghĩa 2&#10;Từ 3&#9;Định nghĩa 3"
        ></textarea>
        <div class="preview-section" v-if="importText">
          <h3>Xem trước</h3>
          <div class="preview-cards">
            <div v-for="(preview, index) in previewCards" :key="index" class="preview-card">
              <div class="preview-content">
                <div class="preview-front">{{ preview.front }}</div>
                <div class="preview-back">{{ preview.back }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="import-actions">
          <button @click="processImport" class="import-btn" :disabled="!importText">Nhập</button>
          <button @click="cancelImport" class="cancel-btn">Hủy nhập</button>
        </div>
      </div>

      <div class="cards-container" v-else>
        <div class="cards-header">
          <h2>Thẻ ghi nhớ</h2>
          <button @click="showImportSection" class="import-toggle-btn">
            <i class="fas fa-file-import"></i>
            Nhập dữ liệu
          </button>
        </div>
        <div v-for="(card, index) in cards" :key="index" class="card-item">
          <div class="card-header">
            <span>Thẻ {{ index + 1 }}</span>
            <button @click="removeCard(index)" class="remove-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <div class="card-content">
            <input v-model="card.front" type="text" placeholder="Mặt trước..." />
            <input v-model="card.back" type="text" placeholder="Mặt sau..." />
          </div>
        </div>
        <button @click="addCard" class="add-card-btn">
          <i class="fas fa-plus"></i>
          Thêm thẻ
        </button>
      </div>
      <div class="form-actions">
        <button @click="saveFlashcard" class="save-btn">Lưu bộ thẻ</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const route = useRoute()
const store = useStore()

const STORAGE_KEY = 'flashcard_draft'
const AUTO_SAVE_DELAY = 1000 // 1 second

const title = ref('')
const description = ref('')
const cards = ref([
  { front: '', back: '' }
])
const showImport = ref(false)
const importText = ref('')
const termSeparator = ref('tab')
const cardSeparator = ref('newline')
const customTermSeparator = ref('')
const customCardSeparator = ref('')
const showError = ref(false)

// Add new refs for handling learning page data
const fromLearningPage = ref(false)
const isEditing = ref(false)
const originalDeckId = ref(null)

// Load saved state and check if coming from learning page
onMounted(() => {
  // Check if coming from learning page
  fromLearningPage.value = route.query.fromLearn === 'true'
  originalDeckId.value = route.query.deckId
  
  // Try to load learning page state first
  const learningState = localStorage.getItem('flashcardLearnState')
  if (learningState) {
    try {
      const state = JSON.parse(learningState)
      // Convert learning items to cards format
      cards.value = state.items.map(item => ({
        front: item.content || item.kanji || '',
        back: item.backcontent || item.meaning || ''
      }))
      isEditing.value = true
    } catch (error) {
      console.error('Error loading learning state:', error)
    }
  } else {
    // If no learning state, try loading draft
    const savedState = localStorage.getItem(STORAGE_KEY)
    if (savedState) {
      const state = JSON.parse(savedState)
      title.value = state.title
      description.value = state.description
      cards.value = state.cards
    }
  }
})

// Auto-save functionality
let autoSaveTimeout = null

const autoSave = () => {
  if (autoSaveTimeout) {
    clearTimeout(autoSaveTimeout)
  }
  
  autoSaveTimeout = setTimeout(() => {
    const state = {
      title: title.value,
      description: description.value,
      cards: cards.value
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
  }, AUTO_SAVE_DELAY)
}

// Watch for changes and trigger auto-save
watch([title, description, cards], () => {
  autoSave()
}, { deep: true })

// Clean up
onBeforeUnmount(() => {
  if (autoSaveTimeout) {
    clearTimeout(autoSaveTimeout)
  }
})

const validateForm = () => {
  showError.value = false
  
  if (!title.value.trim()) {
    showError.value = true
    return false
  }
  
  return true
}

const showImportSection = () => {
  showImport.value = true
}

const cancelImport = () => {
  showImport.value = false
  importText.value = ''
}

const previewCards = computed(() => {
  if (!importText.value) return []
  
  const separator = termSeparator.value === 'tab' ? '\t' : 
                   termSeparator.value === 'comma' ? ',' : 
                   termSeparator.value === 'custom' ? customTermSeparator.value : '\t'
  const lineSeparator = cardSeparator.value === 'newline' ? '\n' : 
                       cardSeparator.value === 'custom' ? customCardSeparator.value : '\n'
  
  if (termSeparator.value === 'custom' && !customTermSeparator.value) return []
  if (cardSeparator.value === 'custom' && !customCardSeparator.value) return []
  
  return importText.value.split(lineSeparator)
    .filter(line => line.trim())
    .map(line => {
      const [front, back] = line.split(separator)
      return { front: front?.trim(), back: back?.trim() }
    })
    .filter(card => card.front && card.back)
})

const processImport = () => {
  if (previewCards.value.length > 0) {
    cards.value = previewCards.value
    showImport.value = false
    importText.value = ''
  }
}

const addCard = () => {
  cards.value.push({ front: '', back: '' })
}

const removeCard = (index) => {
  cards.value.splice(index, 1)
}

const goBackToLearning = () => {
  // Navigate back to learning page with the same parameters
  router.push({
    name: 'flashcardLearn',
    query: {
      deckId: originalDeckId.value,
      source: route.query.source,
      title: title.value
    }
  })
}

const saveFlashcard = async () => {
  if (!validateForm()) {
    return
  }

  try {
    const flashcardData = {
      title: title.value.trim(),
      description: description.value.trim(),
      cards: cards.value.filter(card => card.front.trim() && card.back.trim()),
      cardCount: cards.value.filter(card => card.front.trim() && card.back.trim()).length
    }
    
    if (isEditing.value && originalDeckId.value) {
      // Update existing flashcard set
      await store.dispatch('flashcard/updateFlashcardSet', {
        id: originalDeckId.value,
        set: flashcardData
      })
    } else {
      // Create new flashcard set
      await store.dispatch('flashcard/createFlashcardSet', flashcardData)
    }
    
    // Clean up storage
    localStorage.removeItem(STORAGE_KEY)
    localStorage.removeItem('flashcardLearnState')
    
    // Navigate based on source
    if (fromLearningPage.value) {
      goBackToLearning()
    } else {
      router.push('/library')
    }
  } catch (error) {
    console.error('Error saving flashcard:', error)
  }
}
</script>

<style lang="scss" scoped>
@use "sass:color";

.create-flashcard {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;

  h1 {
    color: #333;
    margin-bottom: 30px;
  }
}

.form-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 20px;

  label {
    display: block;
    margin-bottom: 8px;
    color: #555;
    font-weight: 600;
  }

  input, textarea {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;

    &:focus {
      outline: none;
      border-color: #E94560;
    }
  }

  textarea {
    height: 100px;
    resize: vertical;
  }
}

.cards-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h2 {
    color: #444;
    margin: 0;
  }
}

.import-toggle-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 6px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f1f3f5;
    border-color: #adb5bd;
  }

  i {
    font-size: 14px;
  }
}

.import-section {
  h2 {
    color: #444;
    margin-bottom: 16px;
  }
}

.import-instruction {
  color: #666;
  margin-bottom: 16px;
  font-size: 14px;
}

.separator-options {
  display: flex;
  gap: 32px;
  margin-bottom: 16px;
}

.separator-group {
  flex: 1;

  label {
    display: block;
    margin-bottom: 8px;
    color: #555;
    font-weight: 600;
  }
}

.radio-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  cursor: pointer;

  input {
    cursor: pointer;
  }
}

.import-textarea {
  width: 100%;
  height: 200px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  margin-bottom: 16px;

  &:focus {
    outline: none;
    border-color: #E94560;
  }
}

.preview-section {
  margin-top: 24px;

  h3 {
    color: #444;
    margin-bottom: 16px;
  }
}

.preview-cards {
  display: grid;
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.preview-card {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 12px;
}

.preview-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.preview-front, .preview-back {
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.import-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.import-btn, .cancel-btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.import-btn {
  background: #E94560;
  color: white;
  border: none;

  &:hover {
    background: #d13651;
  }

  &:disabled {
    background: #ffa8a8;
    cursor: not-allowed;
  }
}

.cancel-btn {
  background: white;
  color: #666;
  border: 1px solid #ddd;

  &:hover {
    background: #f8f9fa;
    border-color: #adb5bd;
  }
}

.cards-container {
  margin-top: 30px;

  h2 {
    color: #444;
    margin-bottom: 20px;
  }
}

.card-item {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  span {
    font-weight: 600;
    color: #666;
  }
}

.card-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;

    &:focus {
      outline: none;
      border-color: #E94560;
    }
  }
}

.remove-btn {
  background: none;
  border: none;
  color: #ff4757;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;

  &:hover {
    background: #ffebee;
  }
}

.add-card-btn {
  width: 100%;
  padding: 12px;
  background: #f1f1f1;
  border: 2px dashed #ddd;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;

  &:hover {
    background: #e9e9e9;
    border-color: #ccc;
  }
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}

.save-btn {
  background: #E94560;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background: #d13651;
  }
}

.custom-separator-input {
  width: 200px;
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  margin-left: 8px;

  &:focus {
    outline: none;
    border-color: #E94560;
  }
}

.required {
  color: #E94560;
  margin-left: 4px;
}

.error {
  border-color: #E94560 !important;
}

.error-message {
  color: #E94560;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}

.header-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 30px;

  h1 {
    margin: 0;
    color: #333;
  }
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;

  &:hover {
    background: #fff1f3;
    border-color: #E94560;
    color: #E94560;
  }

  i {
    font-size: 14px;
  }
}
</style> 