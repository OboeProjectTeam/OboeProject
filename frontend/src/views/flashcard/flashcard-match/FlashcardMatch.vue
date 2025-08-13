<template>
  <div class="flashcard-match">
    <div class="match-header">
      <div class="header-top-line">
        <h1>{{ $t('flashcardMatch.title') }}</h1>
        <div class="header-actions">
          <div class="timer">
            <i class="fas fa-clock"></i>
            <span>{{ formattedTime }}</span>
          </div>
          <ExitTestButton @exit="confirmExitToLearn" />
        </div>
      </div>
      <p class="cards-count">{{ gridItems.length / 2 }} {{ $t('flashcardMatch.cardPairs') }}</p>
    </div>

    <div class="match-grid" :class="gridLayoutClass">
      <template v-for="loopItem in gridItems" :key="loopItem.id">
        <div v-if="loopItem" class="match-cell" :class="{
          selected: loopItem.id === selectedCell1?.id || loopItem.id === selectedCell2?.id,
          matched: loopItem.isMatched,
          error: loopItem.isError
        }" @click="handleCellClick(loopItem)">
          <div class="cell-content">
            {{ loopItem.content }}
          </div>
        </div>
      </template>
    </div>

    <div v-if="isGameWon" class="game-won-popup">
      <div class="popup-overlay"></div>
      <div class="popup-content">
        <h2>{{ $t('flashcardMatch.congratulations') }}</h2>
        <p>{{ $t('flashcardMatch.gameCompleted') }} {{ finalTime }}.</p>
        <div class="popup-actions">
            <button @click="playAgain" class="primary-button">{{ $t('flashcardMatch.playAgain') }}</button>
            <button @click="goBackToLearn" class="secondary-button">{{ $t('flashcardMatch.backToLearn') }}</button>
          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import ExitTestButton from '@/components/layout/button-exit/ExitTestButton.vue';

const store = useStore();
const router = useRouter();

const allFlashcards = ref([]);
const gridItems = ref([]); // Array of { id, content, cardId, type, isMatched, isVisible, isError }
const selectedCell1 = ref(null);
const selectedCell2 = ref(null);

const timeElapsed = ref(0); // in seconds
const timerInterval = ref(null);
const isGameWon = ref(false);
const finalTime = ref('');

const MAX_PAIRS = 6; // Max 6 pairs, so 12 cells

const formattedTime = computed(() => {
  const minutes = Math.floor(timeElapsed.value / 60);
  const seconds = timeElapsed.value % 60;
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
});

const gridLayoutClass = computed(() => {
  const itemCount = gridItems.value.length;
  if (itemCount === 0) return '';

  // Check if we're on mobile
  if (window.innerWidth <= 768) {
    return 'grid-cols-2';
  }

  // Desktop layout
  if (itemCount <= 4) return 'grid-cols-2';
  if (itemCount <= 6) return 'grid-cols-3';
  if (itemCount <= 12) return 'grid-cols-4';
  return 'grid-cols-4';
});

const startGameTimer = () => {
  if (timerInterval.value) clearInterval(timerInterval.value);
  timeElapsed.value = 0;
  timerInterval.value = setInterval(() => {
    timeElapsed.value++;
  }, 1000);
};

const stopGameTimer = () => {
  if (timerInterval.value) clearInterval(timerInterval.value);
};

const initializeGame = () => {

  isGameWon.value = false;
  selectedCell1.value = null;
  selectedCell2.value = null;

  const storedFlashcards = store.getters['flashcard/getLearningItems'];
  if (!storedFlashcards || storedFlashcards.length === 0) {
    console.error("No flashcards found for Match Game.");
    // Optionally, redirect or show a message
    // router.push({ name: 'flashcardLearn' }); 
    return;
  }
  allFlashcards.value = JSON.parse(JSON.stringify(storedFlashcards));


  let cardsForGame = [];
  if (allFlashcards.value.length > MAX_PAIRS) {
    // Shuffle and pick MAX_PAIRS
    const shuffled = [...allFlashcards.value].sort(() => 0.5 - Math.random());
    cardsForGame = shuffled.slice(0, MAX_PAIRS);
  } else {
    cardsForGame = [...allFlashcards.value];
  }

  if (cardsForGame.length === 0) {
    console.error("Not enough cards to start the game after filtering.");
    // Handle this case, e.g., show a message or prevent game start
    return;
  }



  let tempGridItems = [];
  cardsForGame.forEach((card, index) => {
    const originalCardId = card.id || `card-${index}`;
    const frontContent = card.front || card.content || `Front ${index + 1}`;
    const backContent = card.back || card.backcontent || card.meaning || `Back ${index + 1}`;

    tempGridItems.push({
      id: `${originalCardId}_front`,
      content: frontContent,
      cardId: originalCardId,
      type: 'front',
      isMatched: false,
      isError: false,
    });
    tempGridItems.push({
      id: `${originalCardId}_back`,
      content: backContent,
      cardId: originalCardId,
      type: 'back',
      isMatched: false,
      isError: false,
    });
  });

  gridItems.value = tempGridItems.sort(() => Math.random() - 0.5);


  startGameTimer();
};

const handleCellClick = (clickedItem) => {
  if (isGameWon.value || clickedItem.isMatched || clickedItem.isError || selectedCell2.value) {
    // Ignore clicks if game is won, cell is matched, currently in error state, or two cells already selected
    return;
  }



  if (!selectedCell1.value) {
    selectedCell1.value = clickedItem;

  } else if (selectedCell1.value && selectedCell1.value.id !== clickedItem.id) {
    selectedCell2.value = clickedItem;


    // Check for match
    if (selectedCell1.value.cardId === selectedCell2.value.cardId) {
      // Match found!

      selectedCell1.value.isMatched = true;
      selectedCell2.value.isMatched = true;

      // Make them disappear after a short delay for effect
      setTimeout(() => {
        selectedCell1.value = null;
        selectedCell2.value = null;
        checkGameWin();
      }, 500); // 0.5 second delay for disappearance

    } else {
      // No match

      selectedCell1.value.isError = true;
      selectedCell2.value.isError = true;

      setTimeout(() => {
        if (selectedCell1.value) selectedCell1.value.isError = false;
        if (selectedCell2.value) selectedCell2.value.isError = false;
        // Reset selection
        selectedCell1.value = null;
        selectedCell2.value = null;
      }, 800); // 0.8 second for error display
    }
  }
};

const checkGameWin = () => {
  const allMatched = gridItems.value.every(item => item.isMatched);
  if (allMatched && gridItems.value.length > 0) {
    isGameWon.value = true;
    stopGameTimer();
    finalTime.value = formattedTime.value;

  }
};

const playAgain = () => {
  initializeGame();
};

const returnToLearnPage = () => {
  router.push({ name: 'flashcardLearn' });
  // FlashcardLearn component should handle restoring its state
};

const goBackToLearn = () => {
  returnToLearnPage();
};

const confirmExitToLearn = () => {
  returnToLearnPage();
};

onMounted(() => {
  initializeGame();
});

onUnmounted(() => {
  stopGameTimer();
});

</script>

<style lang="scss" scoped>
@use '@/views/flashcard/flashcard-match/FlashcardMatch.scss';
</style>