<template>
  <div class="create-flashcard">
    <div class="header-section">
      <button v-if="fromLearningPage" class="back-btn" @click="goBackToLearning">
        <i class="fas fa-arrow-left"></i>
        {{ t('createFlashcard.backToLearning') }}
      </button>
      <div class="flex-jsb">
        <h1>{{ isEditing ? t('createFlashcard.editTitle') : t('createFlashcard.createTitle') }}</h1>
      </div>
    </div>
    <div class="form-container">
      <div class="form-group">
        <label>{{ t('createFlashcard.nameLabel') }} <span class="required">{{ t('createFlashcard.required') }}</span></label>
        <input v-model="title" type="text" :placeholder="t('createFlashcard.namePlaceholder')" :class="{ 'error': showError && !title }" />
        <span v-if="showError && !title" class="error-message">
          {{ t('createFlashcard.nameRequired') }}
        </span>
      </div>
      <div class="form-group">
        <label>{{ t('createFlashcard.descriptionLabel') }}</label>
        <textarea v-model="description" :placeholder="t('createFlashcard.descriptionPlaceholder')"></textarea>
      </div>

      <div class="import-section" v-if="showImport">
        <h2>{{ t('createFlashcard.importData') }}</h2>
        <p class="import-instruction">{{ t('createFlashcard.importInstruction') }}</p>
        <div class="separator-options">
          <div class="separator-group">
            <label>{{ t('createFlashcard.termSeparator') }}</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="termSeparator" value="tab">
                {{ t('createFlashcard.tab') }}
              </label>
              <label class="radio-label">
                <input type="radio" v-model="termSeparator" value="comma">
                {{ t('createFlashcard.comma') }}
              </label>
              <label class="radio-label">
                <input type="radio" v-model="termSeparator" value="custom">
                {{ t('createFlashcard.custom') }}
              </label>
              <input v-if="termSeparator === 'custom'" v-model="customTermSeparator" type="text"
                class="custom-separator-input" :placeholder="t('createFlashcard.customSeparatorPlaceholder')" />
            </div>
          </div>
          <div class="separator-group">
            <label>{{ t('createFlashcard.cardSeparator') }}</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="cardSeparator" value="newline">
                {{ t('createFlashcard.newline') }}
              </label>
              <label class="radio-label">
                <input type="radio" v-model="cardSeparator" value="custom">
                {{ t('createFlashcard.custom') }}
              </label>
              <input v-if="cardSeparator === 'custom'" v-model="customCardSeparator" type="text"
                class="custom-separator-input" :placeholder="t('createFlashcard.customSeparatorPlaceholder')" />
            </div>
          </div>
        </div>
        <textarea v-model="importText" class="import-textarea"
          placeholder="Từ 1&#9;Định nghĩa 1&#10;Từ 2&#9;Định nghĩa 2&#10;Từ 3&#9;Định nghĩa 3"></textarea>
        <div class="preview-section" v-if="importText">
          <h3>{{ t('createFlashcard.preview') }}</h3>
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
          <button @click="processImport" class="import-btn" :disabled="!importText">{{ t('createFlashcard.import') }}</button>
          <button @click="cancelImport" class="cancel-btn">{{ t('createFlashcard.cancelImport') }}</button>
        </div>
      </div>

      <div class="cards-container" v-else>
        <div class="cards-header">
          <h2>{{ t('createFlashcard.flashcards') }}</h2>
          <button @click="showImportSection" class="import-toggle-btn">
            <i class="fas fa-file-import"></i>
            {{ t('createFlashcard.importData') }}
          </button>
        </div>
        <div v-for="(card, index) in cards" :key="index" class="card-item">
          <div class="card-header">
            <span>{{ t('createFlashcard.card') }} {{ index + 1 }}</span>
            <button @click="removeCard(index)" class="remove-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <div class="card-content">
            <input v-model="card.front" type="text" :placeholder="t('createFlashcard.frontPlaceholder')" />
            <input v-model="card.back" type="text" :placeholder="t('createFlashcard.backPlaceholder')" />
          </div>
        </div>
        <button @click="addCard" class="add-card-btn">
          <i class="fas fa-plus"></i>
          {{ t('createFlashcard.addCard') }}
        </button>
      </div>
      <div class="form-actions">
        <button @click="saveFlashcard" class="save-btn">{{ t('createFlashcard.saveFlashcard') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import flashcardApi from '@/api/modules/flashcardApi'

const router = useRouter()
const route = useRoute()
const store = useStore()
const { t } = useI18n()

const STORAGE_KEY = 'flashcard_draft'
const AUTO_SAVE_DELAY = 1000 // 1 second

const title = ref('')
const description = ref('')
const cards = ref([
  { front: '', back: '', type: 'word' }
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

  // If coming from learning page, load data
  if (fromLearningPage.value) {
    // Set title and description from query params
    title.value = route.query.title || ''
    description.value = route.query.description || ''
    
    // Try to load learning page state
    const learningState = localStorage.getItem('flashcardLearnState')
    if (learningState) {
      try {
        const state = JSON.parse(learningState)
        // Load title and description from state if available (override query params)
        if (state.title) {
          title.value = state.title
        }
        if (state.description) {
          description.value = state.description
        }
        
        // Convert learning items to cards format
        if (state.items && Array.isArray(state.items) && state.items.length > 0) {
          cards.value = state.items.map(item => {
            // Handle different item types like in FlashcardLearn getItemContent
            let frontContent, backContent;
            
            switch (item.type) {
              case 'kanji':
                frontContent = item.kanji || item.content || '';
                backContent = item.kanjiname || item.backcontent || item.meaning || '';
                break;
              case 'grammar':
                frontContent = item.kana || item.content || '';
                backContent = item.meaning || item.backcontent || '';
                break;
              case 'sentence':
                frontContent = item.sentence || item.content || '';
                backContent = item.translation || item.backcontent || '';
                break;
              case 'word':
              default:
                frontContent = item.content || item.kanji || item.front || '';
                backContent = item.backcontent || item.meaning || item.back || '';
            }
            
            return {
               front: frontContent,
               back: backContent,
               type: item.type || 'word' // Preserve original type
             };
          })
        } else {
          cards.value = [{ front: '', back: '', type: 'word' }]
        }
        
        isEditing.value = true
      } catch (error) {
        console.error('Error loading learning state:', error)
        // Fallback to empty card on error
        cards.value = [{ front: '', back: '', type: 'word' }]
      }
    } else {
      // Set default empty card if no state found
      cards.value = [{ front: '', back: '' }]
    }
  } else {
    // If not from learning page, try loading draft
    const savedState = localStorage.getItem(STORAGE_KEY)
    if (savedState) {
      try {
        const state = JSON.parse(savedState)
        title.value = state.title || ''
        description.value = state.description || ''
        cards.value = state.cards || [{ front: '', back: '', type: 'word' }]
      } catch (error) {
        console.error('Error loading draft state:', error)
        cards.value = [{ front: '', back: '', type: 'word' }]
      }
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
  cards.value.push({ front: '', back: '', type: 'word' })
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
      title: title.value,
      description: description.value,
      // Preserve other important query params if they exist
      creatorName: route.query.creatorName,
      creatorAvatar: route.query.creatorAvatar,
      createdAt: route.query.createdAt
    }
  })
}

const saveFlashcard = async () => {
  if (!validateForm()) {
    return;
  }

  // Validate cards
  const validCards = cards.value.filter(card => card.front.trim() && card.back.trim());
  if (validCards.length === 0) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createFlashcard.addCardError')
    });
    return;
  }

  try {
    const flashcardData = {
      term: title.value.trim(),
      description: description.value.trim(),
      cardItems: validCards.map(card => ({
        word: card.front.trim(),
        meaning: card.back.trim()
      }))
    };



    let response;
    if (isEditing.value && originalDeckId.value) {
      // Update existing flashcard set

      response = await flashcardApi.update(originalDeckId.value, flashcardData);
    } else {
      // Create new flashcard set

      response = await flashcardApi.create(flashcardData);
    }



    // Show success message
    store.dispatch('message/showMessage', {
      type: 'success',
      text: isEditing.value ? t('createFlashcard.updateSuccess') : t('createFlashcard.createSuccess')
    });

    // Clean up storage
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem('flashcardLearnState');

    // Navigate based on source
    if (fromLearningPage.value) {
      // Convert updated flashcard response to format expected by FlashcardLearn
      const updatedItems = response.cardItems ? response.cardItems.map((item, index) => {
        // Try to preserve original type from cards if available
        const originalCard = cards.value[index];
        const itemType = originalCard?.type || 'word';
        
        // Map back to proper format based on type
        let mappedItem = {
          id: `item-${index}-${item.word}`,
          type: itemType,
          status: 'learning'
        };
        
        switch (itemType) {
          case 'kanji':
            mappedItem.kanji = item.word;
            mappedItem.kanjiname = item.meaning;
            mappedItem.content = item.word;
            mappedItem.backcontent = item.meaning;
            break;
          case 'grammar':
            mappedItem.kana = item.word;
            mappedItem.meaning = item.meaning;
            mappedItem.content = item.word;
            mappedItem.backcontent = item.meaning;
            break;
          case 'sentence':
            mappedItem.sentence = item.word;
            mappedItem.translation = item.meaning;
            mappedItem.content = item.word;
            mappedItem.backcontent = item.meaning;
            break;
          case 'word':
          default:
            mappedItem.content = item.word;
            mappedItem.backcontent = item.meaning;
            mappedItem.kanji = item.word;
            mappedItem.meaning = item.meaning;
        }
        
        return mappedItem;
      }) : [];

      // Update store with new items
      await store.dispatch('flashcard/setLearningItems', updatedItems);
      
      // Navigate back to learning page
      await router.push({
        name: 'flashcardLearn',
        query: {
          deckId: originalDeckId.value || response.flashcardID,
          source: route.query.source || 'library',
          title: response.term || title.value,
          description: response.description || description.value,
          updated: 'true' // Flag to indicate data was updated
        }
      });
    } else {
      router.push('/library');
    }
  } catch (error) {
    console.error('Error saving flashcard:', error);
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createFlashcard.saveError') + error.message
    });
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/create-learn/create-flashcard/CreateFlashcard.scss';
</style>