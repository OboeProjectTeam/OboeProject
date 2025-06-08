<template>
  <div v-if="item" class="detail-page" :class="{ 'has-back-button': showBackButton }">
    <!-- Back Button -->
    <button v-if="showBackButton" @click="goBack" class="back-button">
      <i class="fas fa-arrow-left"></i>
      Quay lại Thư viện
    </button>
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
import { useRoute, useRouter } from 'vue-router';
import CommentSection from '@/components/layout/comment/CommentSection.vue';

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
    const route = useRoute();
    const router = useRouter();

    const showBackButton = computed(() => route.query.from === 'library');

    const goBack = () => {
      router.push({ path: '/library', query: { tab: 'favorites', favoriteTab: route.query.favoriteTab } });
    };

    const favoriteType = computed(() => {
      if (props.type === 'word') return 'vocabulary';
      if (props.type === 'sentence') return 'sentences';
      return props.type;
    });

    const isFavorite = computed(() => {
        if (!props.item) return false;
        return store.getters['user/isFavorite'](favoriteType.value, props.item);
    });
    
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
      if (!props.item) return;
      store.dispatch('user/toggleFavorite', { 
        type: favoriteType.value, 
        item: props.item 
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
      toggleFlashcard,
      showBackButton,
      goBack
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/components/layout/detail-search/DetailSearch.scss";
</style> 