<template>
  <div class="flashcard-test">
    <!-- Header Section -->
    <div class="test-header">
      <div class="test-info">
        <h2>{{ testTypeTitle }}</h2>
        <p class="progress-text">Câu {{ currentQuestionIndex + 1 }}/{{ totalQuestions }}</p>
      </div>
      <div class="header-actions">
        <div class="timer" v-if="showTimer && !showFinalResults && !isReviewing">
          <i class="fas fa-clock"></i>
          <span>{{ formatTime(timeRemaining) }}</span>
        </div>
        <button class="exit-button secondary-button" @click="requestExitToLearn" v-if="!showFinalResults">
          <i class="fas fa-times-circle"></i>
          Thoát & Ôn Luyện
        </button>
      </div>
    </div>

    <!-- Progress Bar -->
    <div class="progress-bar">
      <div class="progress" :style="{ width: progressPercentage + '%' }"></div>
    </div>

    <!-- Question Section -->
    <div class="question-section">
      <!-- Multiple Choice Question -->
      <div v-if="testType === 'multiple-choice'" class="question-content">
        <div class="question-text">{{ currentQuestion.question }}</div>
        <div class="options-list">
          <button
            v-for="(option, index) in currentQuestion.options"
            :key="index"
            class="option-button"
            :class="{
              selected: selectedAnswer === index,
              correct: showResults && index === currentQuestion.correctAnswer,
              incorrect: showResults && selectedAnswer === index && selectedAnswer !== currentQuestion.correctAnswer
            }"
            @click="selectAnswer(index)"
            :disabled="showResults"
          >
            <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
            <span class="option-text">{{ option }}</span>
          </button>
        </div>
      </div>

      <!-- Written Question -->
      <div v-else-if="testType === 'written'" class="question-content">
        <div class="question-text">{{ currentQuestion.question }}</div>
        <div class="written-answer">
          <textarea
            v-model="writtenAnswer"
            placeholder="Nhập câu trả lời của bạn..."
            :disabled="showResults"
          ></textarea>
        </div>
      </div>

      <!-- True/False Question -->
      <div v-else-if="testType === 'true-false'" class="question-content">
        <div class="question-text">{{ currentQuestion.question }}</div>
        <div class="true-false-options">
          <button
            class="true-false-button"
            :class="{
              selected: selectedAnswer === true,
              correct: showResults && currentQuestion.correctAnswer === true,
              incorrect: showResults && selectedAnswer === true && selectedAnswer !== currentQuestion.correctAnswer
            }"
            @click="selectAnswer(true)"
            :disabled="showResults"
          >
            <i class="fas fa-check"></i>
            Đúng
          </button>
          <button
            class="true-false-button"
            :class="{
              selected: selectedAnswer === false,
              correct: showResults && currentQuestion.correctAnswer === false,
              incorrect: showResults && selectedAnswer === false && selectedAnswer !== currentQuestion.correctAnswer
            }"
            @click="selectAnswer(false)"
            :disabled="showResults"
          >
            <i class="fas fa-times"></i>
            Sai
          </button>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons">
      <button class="secondary-button" @click="previousQuestion" :disabled="currentQuestionIndex === 0">
        <i class="fas fa-arrow-left"></i>
        Câu trước
      </button>
      
      <button v-if="!showResults" class="primary-button" @click="submitAnswer" :disabled="!canSubmit">
        {{ isLastQuestion ? 'Nộp bài' : 'Câu tiếp theo' }}
        <i class="fas" :class="isLastQuestion ? 'fa-check' : 'fa-arrow-right'"></i>
      </button>
      
      <button v-else class="primary-button" @click="nextQuestion" :disabled="isLastQuestion">
        Câu tiếp theo
        <i class="fas fa-arrow-right"></i>
      </button>
    </div>

    <!-- Results Modal -->
    <div v-if="showFinalResults" class="results-modal">
      <div class="modal-overlay"></div>
      <div class="results-content">
        <div class="results-header">
          <img src="@/assets/img/celebration.jpg" alt="Celebration" class="celebration-image" />
          <h2>Chúc mừng! Bạn đã hoàn thành bài kiểm tra</h2>
        </div>

        <div class="results-stats">
          <div class="stat-item">
            <div class="stat-label">Điểm số</div>
            <div class="stat-value">{{ score }}/{{ totalQuestions }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">Thời gian</div>
            <div class="stat-value">{{ formatTime(totalTime) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">Độ chính xác</div>
            <div class="stat-value">{{ accuracy }}%</div>
          </div>
        </div>

        <div class="results-actions">
          <div class="results-actions-row">
            <button class="secondary-button" @click="reviewAnswers">
              <i class="fas fa-search"></i>
              Xem lại đáp án
            </button>
            <button class="secondary-button" @click="retakeTest">
              <i class="fas fa-redo"></i>
              Làm lại kiểm tra 
            </button>
          </div>
          <button class="primary-button full-width-button" @click="returnToLearn">
            <i class="fas fa-graduation-cap"></i>
            Học Lại
          </button>
        </div>
      </div>
    </div>

    <!-- NEW Confirmation Popup for Exiting Test -->
    <div v-if="showConfirmExitPopup" class="confirm-exit-popup results-modal">
      <div class="modal-overlay" @click="cancelExitToLearn"></div>
      <div class="results-content" style="max-width: 400px;">
        <div class="results-header">
          <h3>Xác nhận thoát</h3>
        </div>
        <p style="text-align: center; margin-bottom: 20px;">Bạn có chắc muốn thoát bài kiểm tra hiện tại và quay lại ôn luyện? Tiến trình sẽ không được lưu.</p>
        <div class="results-actions">
          <button class="secondary-button" @click="cancelExitToLearn">
            <i class="fas fa-ban"></i>
            Hủy
          </button>
          <button class="primary-button" @click="confirmExitToLearn">
            <i class="fas fa-check-circle"></i>
            Xác nhận thoát
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';

const route = useRoute();
const router = useRouter();
const store = useStore();

// State
const testType = ref('');
const questions = ref([]);
const currentQuestionIndex = ref(0);
const selectedAnswer = ref(null);
const writtenAnswer = ref('');
const showResults = ref(false);
const showFinalResults = ref(false);
const timeRemaining = ref(0);
const totalTime = ref(0);
const timer = ref(null);
const answers = ref([]);
const isReviewing = ref(false);
const showConfirmExitPopup = ref(false);

// Helper functions
const generateOptions = (correctAnswer, flashcards) => {
  const options = new Set();
  while (options.size < 3) {
    const randomCard = flashcards[Math.floor(Math.random() * flashcards.length)];
    if (randomCard.backcontent !== correctAnswer) {
      options.add(randomCard.backcontent);
    }
  }
  return Array.from(options);
};

const generateWrongAnswer = (correctAnswer, flashcards) => {
  const wrongAnswers = flashcards
    .filter(f => (f.back || f.meaning || f.backcontent) !== correctAnswer)
    .map(f => f.back || f.meaning || f.backcontent);

  if (wrongAnswers.length === 0) {
    return 'No answer available';
  }

  return wrongAnswers[Math.floor(Math.random() * wrongAnswers.length)];
};

const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};

// Helper function to load user's answer for the current question during review
const loadUserAnswerForCurrentQuestion = () => {
  if (!isReviewing.value) return; // Only run in review mode

  const answerRecord = answers.value[currentQuestionIndex.value];
  if (answerRecord) {
    const questionDetails = questions.value[currentQuestionIndex.value];
    if (testType.value === 'written') {
      writtenAnswer.value = answerRecord.userAnswer;
    } else if (testType.value === 'multiple-choice') {
      if (questionDetails && questionDetails.options && Array.isArray(questionDetails.options)) {
        const answerIndex = questionDetails.options.indexOf(answerRecord.userAnswer);
        selectedAnswer.value = answerIndex !== -1 ? answerIndex : null;
      } else {
        selectedAnswer.value = null;
        console.warn('Could not load multiple-choice answer: options not found for question', currentQuestionIndex.value);
      }
    } else if (testType.value === 'true-false') {
      selectedAnswer.value = answerRecord.userAnswer === 'Đúng';
    }
    console.log(`Review Mode: Loaded answer for Q${currentQuestionIndex.value + 1}: '${answerRecord.userAnswer}', Selected: '${selectedAnswer.value}'`);
  } else {
    selectedAnswer.value = null;
    writtenAnswer.value = '';
    console.log(`Review Mode: No answer record for Q${currentQuestionIndex.value + 1}.`);
  }
};

// Methods
const initializeTest = () => {
  isReviewing.value = false; // NEW: Ensure not in review mode
  console.log('Initializing test with type:', testType.value);
  const flashcards = store.getters['flashcard/getLearningItems'];
  console.log('Retrieved flashcards:', flashcards);
  
  if (!flashcards || flashcards.length === 0) {
    console.error('No flashcards found in store');
    router.push({ name: 'flashcardLearn' });
    return;
  }

  if (!testType.value) {
    console.error('No test type specified');
    router.push({ name: 'flashcardLearn' });
    return;
  }

  // Map flashcards to questions based on test type
  questions.value = flashcards.map(card => {
    const front = card.front || card.content || '';
    const back = card.back || card.backcontent || '';

    if (!front || !back) {
      console.error('Invalid card data:', card);
      return null;
    }

    const baseQuestion = {
      question: front,
      correctAnswer: back
    };

    switch (testType.value) {
      case 'multiple-choice': {
        // Generate wrong answers from other flashcards
        const wrongAnswers = flashcards
          .filter(f => {
            const fBack = f.back || f.backcontent || '';
            return fBack && fBack !== back;
          })
          .map(f => f.back || f.backcontent)
          .filter(Boolean);

        // If we don't have enough wrong answers, create variations
        let attempts = 0;
        const maxAttempts = flashcards.length * 5 + 10; // Set a reasonable attempt limit

        while (wrongAnswers.length < 3 && attempts < maxAttempts) {
          const randomCard = flashcards[Math.floor(Math.random() * flashcards.length)];
          const wrongAnswerCandidate = randomCard.back || randomCard.backcontent;
          if (wrongAnswerCandidate && wrongAnswerCandidate !== back && !wrongAnswers.includes(wrongAnswerCandidate)) {
            wrongAnswers.push(wrongAnswerCandidate);
          }
          attempts++;
        }
        if(attempts >= maxAttempts){
            console.warn('Max attempts reached while generating wrong answers for multiple choice. Options might be less than ideal.');
        }

        // Take up to 3 random wrong answers (or fewer if not enough generated)
        const shuffledWrong = wrongAnswers
          .sort(() => Math.random() - 0.5)
          .slice(0, 3);
        
        const options = [...shuffledWrong];
        // Insert correct answer at random position only if it's not already effectively there
        // (e.g. if shuffledWrong is empty, options is empty, then we add it)
        // Or, more simply, ensure it's present, then shuffle all.
        
        // Ensure the correct answer string `back` is one of the options.
        if (!options.includes(back)) {
            if (options.length < 4) {
                options.push(back);
            } else {
                // If options is already full (e.g. 3 wrong + 1 placeholder from a previous logic that was removed)
                // replace a random one. Or, ensure `back` is included before filling up to 4.
                // For this version of code (reverted by user), we expect `options` to be at most 3 from `shuffledWrong`.
                options.push(back); 
            }
        }
        
        // Shuffle all options together
        options.sort(() => Math.random() - 0.5);
        
        // The correctAnswer for the question object should be the INDEX of the correct answer string.
        const correctIndex = options.indexOf(back);

        return {
          ...baseQuestion,
          options: options,
          correctAnswer: correctIndex
        };
      }

      case 'true-false': {
        const isTrue = Math.random() > 0.5;
        let wrongAnswer = '';
        
        // Get a wrong answer that's different from the correct answer
        const otherAnswers = flashcards
          .filter(f => {
            const fBack = f.back || f.backcontent || '';
            return fBack && fBack !== back;
          })
          .map(f => f.back || f.backcontent)
          .filter(Boolean);

        if (otherAnswers.length > 0) {
          wrongAnswer = otherAnswers[Math.floor(Math.random() * otherAnswers.length)];
        } else {
          wrongAnswer = 'Không có đáp án';
        }

        return {
          ...baseQuestion,
          correctAnswer: isTrue,
          question: `${front} = ${isTrue ? back : wrongAnswer}`
        };
      }

      case 'written':
        return baseQuestion;

      default:
        console.error('Invalid test type:', testType.value);
        return baseQuestion;
    }
  }).filter(Boolean); // Remove any null questions

  if (questions.value.length === 0) {
    console.error('No valid questions could be generated');
    router.push({ name: 'flashcardLearn' });
    return;
  }

  console.log('Generated questions:', questions.value);

  // Reset all state
  currentQuestionIndex.value = 0;
  selectedAnswer.value = null;
  writtenAnswer.value = '';
  showResults.value = false;
  showFinalResults.value = false;
  answers.value = [];

  // Initialize timer
  timeRemaining.value = questions.value.length * 30;
  startTimer();
};

const startTimer = () => {
  if (timer.value) {
    clearInterval(timer.value);
  }
  timer.value = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--;
    } else {
      submitTest();
    }
  }, 1000);
};

// Watch for route changes and initialize test type
watch(() => route.query.type, (newType) => {
  console.log('Route type changed to:', newType);
  if (newType) {
    testType.value = newType;
    nextTick(() => {
      initializeTest();
    });
  } else {
    router.push({ name: 'flashcardLearn' });
  }
}, { immediate: true });

// Computed Properties
const testTypeTitle = computed(() => {
  switch (testType.value) {
    case 'multiple-choice':
      return 'Bài kiểm tra trắc nghiệm';
    case 'written':
      return 'Bài kiểm tra tự luận';
    case 'true-false':
      return 'Bài kiểm tra đúng sai';
    default:
      return 'Bài kiểm tra';
  }
});

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value] || {});
const totalQuestions = computed(() => questions.value.length);
const isLastQuestion = computed(() => currentQuestionIndex.value === totalQuestions.value - 1);
const progressPercentage = computed(() => (currentQuestionIndex.value / totalQuestions.value) * 100);
const showTimer = computed(() => true);

const canSubmit = computed(() => {
  if (testType.value === 'written') {
    return writtenAnswer.value.trim().length > 0;
  }
  return selectedAnswer.value !== null;
});

const score = computed(() => {
  return answers.value.filter(answer => answer.isCorrect).length;
});

const accuracy = computed(() => {
  return Math.round((score.value / totalQuestions.value) * 100);
});

// Methods
const selectAnswer = (answer) => {
  selectedAnswer.value = answer;
};

const submitAnswer = () => {
  console.log('Submitting answer for test type:', testType.value);
  const currentQ = questions.value[currentQuestionIndex.value];
  let isCorrect = false;

  switch (testType.value) {
    case 'written':
      // For written answers, use simple string comparison
      isCorrect = writtenAnswer.value.toLowerCase().trim() === currentQ.correctAnswer.toLowerCase().trim();
      answers.value.push({
        question: currentQ.question,
        userAnswer: writtenAnswer.value,
        correctAnswer: currentQ.correctAnswer,
        isCorrect
      });
      break;

    case 'multiple-choice':
      isCorrect = selectedAnswer.value === currentQ.correctAnswer;
      answers.value.push({
        question: currentQ.question,
        userAnswer: currentQ.options[selectedAnswer.value],
        correctAnswer: currentQ.options[currentQ.correctAnswer],
        isCorrect
      });
      break;

    case 'true-false':
      isCorrect = selectedAnswer.value === currentQ.correctAnswer;
      answers.value.push({
        question: currentQ.question,
        userAnswer: selectedAnswer.value ? 'Đúng' : 'Sai',
        correctAnswer: currentQ.correctAnswer ? 'Đúng' : 'Sai',
        isCorrect
      });
      break;

    default:
      console.error('Invalid test type:', testType.value);
      return;
  }

  console.log('Answer submitted:', {
    type: testType.value,
    isCorrect,
    userAnswer: answers.value[answers.value.length - 1].userAnswer,
    correctAnswer: answers.value[answers.value.length - 1].correctAnswer
  });

  showResults.value = true;

  if (isLastQuestion.value) {
    submitTest();
  } else {
    setTimeout(() => {
      nextQuestion();
    }, 1500);
  }
};

const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++;
    if (isReviewing.value) {
      showResults.value = true; // Keep showing results in review mode
      loadUserAnswerForCurrentQuestion();
    } else {
      showResults.value = false; // Hide results for next question in normal mode
      selectedAnswer.value = null;
      writtenAnswer.value = '';
    }
  } else if (!isReviewing.value) { // If it's the last question and not in review mode, submit test
    // This case might be redundant if submitTest() is called by submitAnswer on last question
    // submitTest(); 
  }
};

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--;
    // In both review and normal (if somehow possible to go back after answering), show results and load answer
    showResults.value = true; 
    loadUserAnswerForCurrentQuestion(); // Use the helper here
  }
};

const submitTest = () => {
  clearInterval(timer.value);
  totalTime.value = totalQuestions.value * 30 - timeRemaining.value;
  showFinalResults.value = true;
};

const reviewAnswers = () => {
  isReviewing.value = true; // NEW: Enter review mode
  showFinalResults.value = false;
  currentQuestionIndex.value = 0;
  showResults.value = true; // Crucial for review mode
  loadUserAnswerForCurrentQuestion(); // Load answer for the first question
};

const retakeTest = () => {
  isReviewing.value = false; // NEW: Exit review mode
  initializeTest(); 
};

const returnToLearn = () => {
  isReviewing.value = false; // Ensure review mode is off
  // Clear any test-specific intervals if not already handled in onUnmounted
  if (timer.value) {
    clearInterval(timer.value);
  }
  router.push({ name: 'flashcardLearn' }); // Or use path: '/flashcard/learn'
  // FlashcardLearn component will handle restoring its state from localStorage
};

// NEW Functions for Exit Confirmation
const requestExitToLearn = () => {
  // Pause timer if active and not already paused
  if (timer.value && timeRemaining.value > 0) {
    clearInterval(timer.value); 
    // No need to store pausedTimer.value = true, as exiting will clear it anyway
  }
  showConfirmExitPopup.value = true;
};

const confirmExitToLearn = () => {
  showConfirmExitPopup.value = false;
  isReviewing.value = false; // Ensure review mode is off
  if (timer.value) { // Clear timer if it was somehow still running
    clearInterval(timer.value);
  }
  router.push({ name: 'flashcardLearn' }); 
};

const cancelExitToLearn = () => {
  // Resume timer if it was paused by requestExitToLearn AND the test is still ongoing
  if (!showFinalResults && !isReviewing.value && timeRemaining.value > 0) {
    startTimer(); // restartTimer might be more appropriate if it exists and preserves time
  }
  showConfirmExitPopup.value = false;
};

// Lifecycle hooks
onMounted(() => {
  initializeTest();
});

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value);
  }
});
</script>

<style lang="scss" scoped>
@use '@/views/flashcard-test/FlashcardTest.scss';
</style> 