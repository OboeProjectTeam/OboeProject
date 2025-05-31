<template>
  <div v-if="item" class="detail-page">
    <div class="detail-card">
      <!-- Action Buttons -->
      <div class="action-buttons">
        <button 
          class="action-btn" 
          :class="{ 'active': isFavorite }"
          @click="toggleFavorite"
        >
          <i class="fas fa-star"></i>
        </button>
        <button 
          class="action-btn"
          :class="{ 'active': isInFlashcards }"
          @click="toggleFlashcard"
        >
          <i class="fas fa-book"></i>
        </button>
      </div>

      <!-- Main Content -->
      <div class="main-info">
        <h1 class="main-text">{{ item[mainField] }}</h1>
        <div v-if="item[readingField]" class="reading-text">{{ item[readingField] }}</div>
        <div class="meaning-text">{{ item[meaningField] }}</div>
      </div>

      <!-- Related Items Section -->
      <div v-if="showRelated" class="related-section">
        <h3 class="section-title">{{ relatedTitle }}</h3>
        <div v-if="relatedItems.length > 0" class="related-grid">
          <div 
            v-for="relatedItem in relatedItems" 
            :key="relatedItem[relatedKeyField]"
            class="related-item"
            @click="onRelatedItemClick(relatedItem)"
          >
            <div class="related-main">{{ relatedItem[relatedMainField] }}</div>
            <div class="related-info">
              <template v-if="type === 'word'">
                <div><span class="label">Âm on:</span> {{ relatedItem.reading }}</div>
                <div><span class="label">Âm kun:</span> {{ relatedItem.kunyomi }}</div>
              </template>
              <template v-else-if="type === 'sentence'">
                <div class="related-kana">{{ relatedItem.kana }}</div>
                <div class="related-meaning">{{ relatedItem.meaning }}</div>
              </template>
            </div>
          </div>
        </div>
        <div v-else class="empty-message">
          {{ emptyRelatedMessage }}
        </div>
      </div>

      <!-- Examples Section for Grammar -->
      <div v-if="type === 'grammar' && item.examples" class="examples-section">
        <h3 class="section-title">Ví dụ</h3>
        <div v-if="item.examples.length > 0" class="examples-list">
          <div v-for="(example, index) in item.examples" :key="index" class="example-item">
            <div class="japanese">{{ example.japanese }}</div>
            <div class="meaning">{{ example.meaning }}</div>
          </div>
        </div>
        <div v-else class="empty-message">
          Không có ví dụ
        </div>
      </div>

      <!-- Comments Section -->
      <div class="comments-section">
        <CommentSection :type="type" :itemId="itemId" />
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    {{ notFoundMessage }}
  </div>
</template>

<script>
import { defineComponent, ref, computed } from 'vue';
import { useStore } from 'vuex';
import CommentSection from '@/components/comment/CommentSection.vue';

export default defineComponent({
  name: 'DetailPage',
  components: {
    CommentSection
  },
  props: {
    type: {
      type: String,
      required: true,
      validator: (value) => ['word', 'kanji', 'grammar', 'sentence'].includes(value)
    },
    item: {
      type: Object,
      default: null
    },
    itemId: {
      type: [String, Number],
      required: true
    },
    mainField: {
      type: String,
      default: 'kanji'
    },
    readingField: {
      type: String,
      default: 'kana'
    },
    meaningField: {
      type: String,
      default: 'meaning'
    },
    showRelated: {
      type: Boolean,
      default: false
    },
    relatedItems: {
      type: Array,
      default: () => []
    },
    relatedTitle: {
      type: String,
      default: ''
    },
    relatedMainField: {
      type: String,
      default: 'kanji'
    },
    relatedKeyField: {
      type: String,
      default: 'id'
    },
    emptyRelatedMessage: {
      type: String,
      default: 'Không có dữ liệu liên quan'
    },
    notFoundMessage: {
      type: String,
      default: 'Không tìm thấy dữ liệu'
    }
  },
  emits: ['relatedItemClick', 'toggleFavorite'],
  setup(props, { emit }) {
    const store = useStore();
    const isFavorite = ref(false); // TODO: Get from store
    
    const isInFlashcards = computed(() => 
      store.getters['flashcard/isInFlashcard'](
        props.type, 
        props.type === 'kanji' ? props.item.kanji : props.itemId
      )
    );

    const onRelatedItemClick = (item) => {
      emit('relatedItemClick', item);
    };

    const toggleFavorite = () => {
      isFavorite.value = !isFavorite.value;
      emit('toggleFavorite', {
        type: props.type,
        item: props.item,
        isFavorite: isFavorite.value
      });
    };

    const toggleFlashcard = () => {
      if (isInFlashcards.value) {
        store.dispatch('flashcard/removeItem', {
          type: props.type,
          id: props.type === 'kanji' ? props.item.kanji : props.itemId,
          ...props.item
        });
      } else {
        store.dispatch('flashcard/addItem', {
          type: props.type,
          id: props.type === 'kanji' ? props.item.kanji : props.itemId,
          ...props.item
        });
      }
    };

    return {
      onRelatedItemClick,
      isFavorite,
      isInFlashcards,
      toggleFavorite,
      toggleFlashcard
    };
  }
});
</script>

<style scoped>
.detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.detail-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 0px 4px rgba(0, 0, 0, 0.4);
  padding: 24px;
  position: relative;
}

.action-buttons {
  position: absolute;
  top: 24px;
  right: 24px;
  display: flex;
  gap: 12px;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  font-size: 1.5rem;
  color: #666;
  transition: all 0.2s ease;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background-color: #f0f0f0;
  color: #333;
}

.action-btn.active {
  color: #E94560;
}

.action-btn.active:hover {
  background-color: #fde8ec;
}

.main-info {
  margin-bottom: 40px;
  text-align: center;
  padding: 40px 0;
}

.main-text {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 24px;
  line-height: 1.4;
  color: v-bind("type === 'kanji' || type === 'word' ? '#cc382c' : '#333'");
}

.reading-text {
  font-size: 1.5rem;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.4;
}

.meaning-text {
  font-size: 1.25rem;
  color: #444;
  max-width: 800px;
  margin: 0 auto;
  line-height: 1.6;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 16px;
}

.related-section {
  margin-top: 24px;
  border-top: 1px solid #eee;
  padding-top: 24px;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.related-item {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.related-item:hover {
  background-color: #f0f0f0;
}

.related-main {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 8px;
}

.related-info {
  font-size: 0.875rem;
}

.label {
  font-weight: 500;
}

.examples-section {
  margin-top: 24px;
  border-top: 1px solid #eee;
  padding-top: 24px;
}

.examples-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.example-item {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 16px;
}

.example-item .japanese {
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.example-item .meaning {
  color: #666;
}

.empty-message {
  color: #666;
}

.not-found {
  text-align: center;
  color: #666;
  padding: 24px;
  font-size: 1.25rem;
}

.related-kana {
  color: #666;
  margin-bottom: 4px;
}

.related-meaning {
  color: #444;
}
</style> 