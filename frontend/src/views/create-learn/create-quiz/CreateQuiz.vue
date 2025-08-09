<template>
  <div class="create-quiz">
    <div class="header-section">
      <div class="flex-jsb">
        <h1>{{ t('createQuiz.title') }}</h1>
        <button @click="createQuizWithAI" class="ai-btn" :disabled="isGeneratingAI">
          <i v-if="isGeneratingAI" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-magic"></i>
          {{ isGeneratingAI ? t('createQuiz.generating') : t('createQuiz.aiButton') }}
        </button>
      </div>
    </div>
    <div class="form-container">
      <div class="form-group">
        <label>{{ t('createQuiz.nameLabel') }}</label>
        <input v-model="title" type="text" :placeholder="t('createQuiz.namePlaceholder')" />
      </div>
      <div class="form-group">
        <label>{{ t('createQuiz.descriptionLabel') }}</label>
        <textarea v-model="description" :placeholder="t('createQuiz.descriptionPlaceholder')"></textarea>
      </div>
      <div class="questions-container">
        <h2>{{ t('createQuiz.questions') }}</h2>
        <div v-for="(question, qIndex) in questions" :key="qIndex" class="question-item">
          <div class="question-header">
            <span>{{ t('createQuiz.question') }} {{ qIndex + 1 }}</span>
            <button @click="removeQuestion(qIndex)" class="remove-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <div class="question-content">
            <input v-model="question.text" type="text" :placeholder="t('createQuiz.questionPlaceholder')" />
            <div class="options-container">
              <div v-for="(option, oIndex) in question.options" :key="oIndex" class="option-item">
                <input 
                  type="radio" 
                  :name="'question-' + qIndex"
                  :id="'q' + qIndex + 'o' + oIndex"
                  :value="oIndex"
                  v-model="question.correctAnswer"
                />
                <input 
                  type="text" 
                  v-model="question.options[oIndex]"
                  :placeholder="t('createQuiz.option') + ' ' + (oIndex + 1)"
                />
                <button @click="removeOption(qIndex, oIndex)" class="remove-option-btn" v-if="question.options.length > 2">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <button @click="addOption(qIndex)" class="add-option-btn" v-if="question.options.length < 4">
                <i class="fas fa-plus"></i>
                {{ t('createQuiz.addOption') }}
              </button>
            </div>
          </div>
        </div>
        <button @click="addQuestion" class="add-question-btn">
          <i class="fas fa-plus"></i>
          {{ t('createQuiz.addQuestion') }}
        </button>
      </div>
      <div class="form-actions">
        <button @click="saveQuiz" class="save-btn">{{ t('createQuiz.saveQuiz') }}</button>
      </div>
    </div>
    
    <!-- Premium Required Popup -->
    <ThePopup
      v-if="showPremiumPopup"
      :title="premiumPopupTitle"
      :message="premiumPopupMessage"
      :confirmText="t('createQuiz.upgradePremium')"
      @confirm="handlePremiumPopupConfirm"
      @cancel="handlePremiumPopupCancel"
      :showCancel="true"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { useI18n } from 'vue-i18n'
import questionApi from '@/api/modules/questionApi'
import aiApi from '@/api/modules/aiApi'
import { usePremiumCheck } from '@/composables/usePremiumCheck'
import ThePopup from '@/components/common/popup/ThePopup.vue'

const router = useRouter()
const store = useStore()
const { t } = useI18n()

// Premium check
const { 
  checkPremiumFeature, 
  showPremiumPopup, 
  premiumPopupMessage, 
  premiumPopupTitle,
  handlePremiumPopupConfirm,
  handlePremiumPopupCancel
} = usePremiumCheck()

const title = ref('')
const description = ref('')
const isGeneratingAI = ref(false)
const questions = ref([
  {
    text: '',
    options: ['', ''],
    correctAnswer: 0
  }
])

const addQuestion = () => {
  questions.value.push({
    text: '',
    options: ['', ''],
    correctAnswer: 0
  })
}

const removeQuestion = (qIndex) => {
  questions.value.splice(qIndex, 1)
}

const addOption = (qIndex) => {
  if (questions.value[qIndex].options.length < 4) {
    questions.value[qIndex].options.push('')
  }
}

const removeOption = (qIndex, oIndex) => {
  if (questions.value[qIndex].options.length > 2) {
    questions.value[qIndex].options.splice(oIndex, 1)
    if (questions.value[qIndex].correctAnswer >= oIndex) {
      questions.value[qIndex].correctAnswer = Math.max(0, questions.value[qIndex].correctAnswer - 1)
    }
  }
}

const validateQuiz = () => {
  if (!title.value.trim()) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.nameRequired')
    });
    return false;
  }

  if (questions.value.length === 0) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.questionRequired')
    });
    return false;
  }

  for (let i = 0; i < questions.value.length; i++) {
    const question = questions.value[i];
    if (!question.text.trim()) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: `${t('createQuiz.questionContentRequired')} ${i + 1}.`
      });
      return false;
    }

    const emptyOption = question.options.findIndex(opt => !opt.trim());
    if (emptyOption !== -1) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: `${t('createQuiz.optionContentRequired')} ${emptyOption + 1} ${t('createQuiz.of')} ${i + 1}.`
      });
      return false;
    }
  }

  return true;
}

const createQuizWithAI = async () => {
  // Kiểm tra premium trước khi sử dụng tính năng AI
  if (!(await checkPremiumFeature())) {
    return
  }
  
  try {
    isGeneratingAI.value = true
    
    // Gọi API để tạo câu hỏi ngẫu nhiên bằng AI
    const response = await aiApi.generateQuestionsByUserId()
    
    if (!Array.isArray(response) || response.length === 0) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: t('createQuiz.aiGenerateError')
      })
      return
    }

    // Chuyển đổi dữ liệu từ API thành format phù hợp cho FlashcardTest
    const learningItems = response.map(q => ({
      type: 'question',
      id: q.questionID,
      front: q.questionName,
      back: q.correctAnswer,
      options: q.options,
      content: q.questionName,
      backcontent: q.correctAnswer
    }))

    // Lưu vào store
    await store.dispatch('flashcard/setLearningItems', learningItems)
    
    // Chuyển hướng đến trang test với thông tin AI-generated
    router.push({
      path: '/flashcard/test',
      query: {
        type: 'multiple-choice',
        aiGenerated: 'true',
        source: 'ai-generated',
        title: t('createQuiz.aiGeneratedTitle'),
        description: t('createQuiz.aiGeneratedDescription', { count: response.length })
      }
    })
    
  } catch (error) {
    console.error('Error generating AI quiz:', error)
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.aiError') + error.message
    })
  } finally {
    isGeneratingAI.value = false
  }
}

const saveQuiz = async () => {
  if (!validateQuiz()) return;

  const quizData = {
    title: title.value.trim(),
    description: description.value.trim()
  };

  try {
    // Bước 1: Tạo quiz
    const response = await store.dispatch('quiz/createQuiz', quizData);

    
    // Bước 2: Tạo câu hỏi cho quiz
    const questionsList = questions.value.map(q => ({
      questionName: q.text.trim(),
      correctAnswer: q.options[q.correctAnswer].trim(),
      options: q.options.map(opt => opt.trim()),
      quizId: response.quizzesID
    }));
    
    // Gọi API tạo câu hỏi
    await questionApi.create(questionsList);
    
    store.dispatch('message/showMessage', {
      type: 'success',
      text: t('createQuiz.createSuccess')
    });
    
    // Chuyển hướng đến trang thư viện
    router.push('/library');
  } catch (error) {
    console.error('Error creating quiz:', error);
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('createQuiz.saveError') + error.message
    });
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/create-learn/create-quiz/CreateQuiz.scss';
</style>