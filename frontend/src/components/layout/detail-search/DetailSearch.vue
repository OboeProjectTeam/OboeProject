<template>
  <div v-if="item" class="detail-page" :class="{ 'has-back-button': showBackButton }">
    <!-- Back Button -->
    <button v-if="showBackButton" @click="goBack" class="back-button">
      <i class="fas fa-arrow-left"></i>
      {{ t('detail.backToLibrary') }}
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
        <div v-if="readingField && item[readingField]" class="reading-text">{{ item[readingField] }}</div>
        <div v-if="item.readings && item.readings.length > 0" class="reading-text">
          <span v-for="(reading, index) in item.readings" :key="index">
            {{ reading }}{{ index < item.readings.length - 1 ? ', ' : '' }}
          </span>
        </div>
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
                <div><span class="label">{{ t('kanji.onReading') }}:</span> {{ relatedItem.reading }}</div>
                <div><span class="label">{{ t('kanji.kunReading') }}:</span> {{ relatedItem.kunyomi }}</div>
              </template>
              <template v-else-if="type === 'sentence'">
                <div class="related-kana">{{ relatedItem.kana }}</div>
                <div class="related-meaning">{{ relatedItem.meaning }}</div>
              </template>
            </div>
          </div>
        </div>
        <div v-else class="empty-message">
          {{ emptyRelatedMessageText }}
        </div>
      </div>

      <!-- Examples Section for Grammar -->
      <div v-if="type === 'grammar' && item.examples" class="examples-section">
        <h3 class="section-title">{{ t('detail.example') }}</h3>
        <div v-if="item.examples.length > 0" class="examples-list">
          <div v-for="(example, index) in item.examples" :key="index" class="example-item">
            <div class="japanese">{{ example.japanese }}</div>
            <div class="meaning">{{ example.meaning }}</div>
          </div>
        </div>
        <div v-else class="empty-message">
          {{ t('detail.noExample') }}
        </div>
      </div>

      <!-- Comments Section -->
      <div class="comments-section">
        <CommentSection :type="type" :itemId="itemId" />
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    {{ notFoundMessageText }}
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
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
      default: ''
    },
    notFoundMessage: {
      type: String,
      default: ''
    }
  },
  emits: ['relatedItemClick', 'toggleFavorite'],
  setup(props, { emit }) {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const { t } = useI18n();

    const showBackButton = computed(() => route.query.from === 'library');

    const goBack = () => {
      router.push({ path: '/library', query: { tab: 'favorites', favoriteTab: route.query.favoriteTab } });
    };

    // Computed properties for i18n text messages
    const emptyRelatedMessageText = computed(() => {
      return props.emptyRelatedMessage || t('detail.noRelatedData');
    });

    const notFoundMessageText = computed(() => {
      return props.notFoundMessage || t('detail.noDataFound');
    });

    const favoriteType = computed(() => {
      if (props.type === 'word') return 'vocabulary';
      if (props.type === 'sentence') return 'sentences';
      return props.type;
    });

    const isFavorite = computed(() => {
        const currentId = route.params.id;
        if (!currentId) return false;
        
        return store.state.user.favoriteItems.some(fav => {
          switch (props.type) {
            case 'word':
              return fav.vocabularyId === currentId;
            case 'grammar':
              return fav.grammaId === currentId; // Note: API uses 'grammaId' not 'grammarId'
            case 'kanji':
              return fav.kanjiId === currentId;
            case 'sentence':
              return fav.sampleSentenceId === currentId;
            default:
              return false;
          }
        });
    });
    
    const isInFlashcards = computed(() => {
      // Always use route params ID for consistency
      const currentId = route.params.id;
      if (!currentId) return false;
      
      return store.getters['flashcard/isInFlashcard'](props.type, currentId);
    });

    const onRelatedItemClick = (item) => {
      emit('relatedItemClick', item);
    };

    const toggleFavorite = () => {
      // Get ID from route params instead of item object
      const currentId = route.params.id;
      if (!currentId) {
        console.error('No ID found in route params');
        return;
      }
      
      store.dispatch('user/toggleFavorite', { 
        type: props.type, // Use original type, not favoriteType
        itemId: currentId // Pass ID directly from URL
      });
    };

    const toggleFlashcard = () => {
      // Always use route params ID for consistency
      const currentId = route.params.id;
      if (!currentId) {
        console.error('No ID found in route params');
        return;
      }
      
      if (isInFlashcards.value) {
        store.dispatch('flashcard/removeItem', {
          type: props.type,
          id: currentId,
          ...props.item
        });
      } else {
        store.dispatch('flashcard/addItem', {
          type: props.type,
          id: currentId,
          ...props.item
        });
      }
    };

    // Load favorites when component mounts
    onMounted(() => {
      store.dispatch('user/fetchFavorites');
    });

    return {
      onRelatedItemClick,
      isFavorite,
      isInFlashcards,
      toggleFavorite,
      toggleFlashcard,
      showBackButton,
      goBack,
      emptyRelatedMessageText,
      notFoundMessageText,
      t
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/components/layout/detail-search/DetailSearch.scss";
</style>