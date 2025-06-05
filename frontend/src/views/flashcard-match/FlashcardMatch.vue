<template>
  <div class="flashcard-match">
    <div class="match-header">
      <h1>Ghép Thẻ</h1>
      <div class="timer">
        <i class="fas fa-clock"></i>
        <span>{{ formattedTime }}</span>
      </div>
    </div>

    <div class="match-grid" :class="gridLayoutClass">
      <template v-for="loopItem in gridItems" :key="loopItem.id">
        <div
          v-if="loopItem"
          class="match-cell"
          :class="{
            selected: loopItem.id === selectedCell1?.id || loopItem.id === selectedCell2?.id,
            matched: loopItem.isMatched,
            error: loopItem.isError
          }"
          @click="handleCellClick(loopItem)"
        >
          <div class="cell-content">
            {{ loopItem.content }}
          </div>
        </div>
      </template>
    </div>

    <div v-if="isGameWon" class="game-won-popup">
      <div class="popup-overlay"></div>
      <div class="popup-content">
        <h2>Chúc mừng!</h2>
        <p>Bạn đã hoàn thành trò chơi ghép thẻ trong {{ finalTime }}.</p>
        <div class="popup-actions">
          <button @click="playAgain" class="primary-button">Chơi lại</button>
          <button @click="returnToLearnPage" class="secondary-button">Về trang học</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

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
  if (itemCount === 0) return ''; // Handle empty case
  if (itemCount <= 4) return 'grid-cols-2'; // e.g. 2xN
  if (itemCount <= 6) return 'grid-cols-3'; // e.g. 3xN
  if (itemCount <= 8) return 'grid-cols-4'; // e.g. 4xN
  if (itemCount <= 12) return 'grid-cols-4';// Default to 4 columns for up to 12 items
  return 'grid-cols-4'; // Fallback
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
  console.log("Initializing Match Game...");
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
  console.log("Retrieved flashcards for game:", allFlashcards.value);

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

  console.log("Cards selected for this game:", cardsForGame);

  let tempGridItems = [];
  cardsForGame.forEach((card, index) => {
    const originalCardId = card.id || `card-${index}`;
    const frontContent = card.front || card.content || `Front ${index+1}`;
    const backContent = card.back || card.backcontent || card.meaning || `Back ${index+1}`;

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
  console.log("Shuffled grid items:", gridItems.value);
  
  startGameTimer();
};

const handleCellClick = (clickedItem) => {
  if (isGameWon.value || clickedItem.isMatched || clickedItem.isError || selectedCell2.value) {
    // Ignore clicks if game is won, cell is matched, currently in error state, or two cells already selected
    return;
  }

  console.log("Cell clicked:", clickedItem);

  if (!selectedCell1.value) {
    selectedCell1.value = clickedItem;
    console.log("Selected cell 1:", selectedCell1.value);
  } else if (selectedCell1.value && selectedCell1.value.id !== clickedItem.id) {
    selectedCell2.value = clickedItem;
    console.log("Selected cell 2:", selectedCell2.value);

    // Check for match
    if (selectedCell1.value.cardId === selectedCell2.value.cardId) {
      // Match found!
      console.log("Match found!");
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
      console.log("No match.");
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
    console.log("Game Won! Time:", finalTime.value);
  }
};

const playAgain = () => {
  initializeGame();
};

const returnToLearnPage = () => {
  router.push({ name: 'flashcardLearn' });
  // FlashcardLearn component should handle restoring its state
};

onMounted(() => {
  initializeGame();
});

onUnmounted(() => {
  stopGameTimer();
});

</script>

<style lang="scss" scoped>
.flashcard-match {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
  font-family: 'Arial', sans-serif;
}

.match-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;

  h1 {
    font-size: 24px;
    color: #333;
  }

  .timer {
    font-size: 20px;
    color: #E94560;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: bold;
  }
}

.match-grid {
  display: grid;
  gap: 15px;
  /* Default, can be overridden by dynamic classes */
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); 
}

.match-grid.grid-cols-2 {
  grid-template-columns: repeat(2, 1fr);
}
.match-grid.grid-cols-3 {
  grid-template-columns: repeat(3, 1fr);
}
.match-grid.grid-cols-4 {
  grid-template-columns: repeat(4, 1fr);
}

.match-cell {
  aspect-ratio: 3 / 2; // Maintain a card-like aspect ratio
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.3s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  overflow: hidden; // Ensure content fits

  .cell-content {
    padding: 10px;
    text-align: center;
    font-size: 16px; // Adjust as needed
    word-wrap: break-word;
    // Potentially add more styles for text overflow etc.
  }

  &:hover:not(.matched):not(.error) {
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  }

  &.selected {
    border-color: #E94560;
    box-shadow: 0 0 0 3px rgba(#E94560, 0.5);
  }

  &.matched {
    opacity: 0;
    pointer-events: none;
    cursor: default;
    border-color: #28a745;
  }
  
  &.error {
    animation: shakeError 0.5s ease-in-out;
    border-color: red;
    background-color: #ffeeee;
  }
}

@keyframes shakeError {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-5px); }
  40%, 80% { transform: translateX(5px); }
}

.game-won-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .popup-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0,0,0,0.5);
  }

  .popup-content {
    background-color: white;
    padding: 30px;
    border-radius: 12px;
    text-align: center;
    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    z-index: 1001;
    min-width: 300px;

    h2 {
      color: #E94560;
      margin-bottom: 15px;
    }
    p {
      margin-bottom: 25px;
      font-size: 16px;
    }
    .popup-actions {
      display: flex;
      justify-content: center;
      gap: 15px;
      button {
        padding: 10px 20px;
        border-radius: 6px;
        font-size: 16px;
        cursor: pointer;
        border: none;
      }
      .primary-button {
        background-color: #E94560;
        color: white;
        &:hover { background-color: #d13651; }
      }
      .secondary-button {
        background-color: #f0f0f0;
        color: #333;
        border: 1px solid #ddd;
        &:hover { background-color: #e0e0e0; }
      }
    }
  }
}

</style> 