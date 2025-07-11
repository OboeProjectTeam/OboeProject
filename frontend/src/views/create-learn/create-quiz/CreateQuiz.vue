<template>
  <div class="create-quiz">
    <div class="header-section">
      <div class="flex-jsb">
        <h1>Tạo Bài Kiểm Tra Mới</h1>
        <button class="ai-btn">Tạo bằng AI</button>
      </div>
    </div>
    <div class="form-container">
      <div class="form-group">
        <label>Tên bài kiểm tra</label>
        <input v-model="title" type="text" placeholder="Nhập tên bài kiểm tra..." />
      </div>
      <div class="form-group">
        <label>Mô tả</label>
        <textarea v-model="description" placeholder="Nhập mô tả về bài kiểm tra..."></textarea>
      </div>
      <div class="questions-container">
        <h2>Câu hỏi</h2>
        <div v-for="(question, qIndex) in questions" :key="qIndex" class="question-item">
          <div class="question-header">
            <span>Câu hỏi {{ qIndex + 1 }}</span>
            <button @click="removeQuestion(qIndex)" class="remove-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <div class="question-content">
            <input v-model="question.text" type="text" placeholder="Nhập câu hỏi..." />
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
                  :placeholder="'Phương án ' + (oIndex + 1)"
                />
                <button @click="removeOption(qIndex, oIndex)" class="remove-option-btn" v-if="question.options.length > 2">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <button @click="addOption(qIndex)" class="add-option-btn" v-if="question.options.length < 4">
                <i class="fas fa-plus"></i>
                Thêm phương án
              </button>
            </div>
          </div>
        </div>
        <button @click="addQuestion" class="add-question-btn">
          <i class="fas fa-plus"></i>
          Thêm câu hỏi
        </button>
      </div>
      <div class="form-actions">
        <button @click="saveQuiz" class="save-btn">Lưu bài kiểm tra</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

const title = ref('')
const description = ref('')
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

const saveQuiz = async () => {
  if (!title.value.trim()) {
    alert('Vui lòng nhập tên bài kiểm tra.')
    return
  }

  if (questions.value.length === 0) {
    alert('Vui lòng thêm ít nhất một câu hỏi.')
    return
  }

  const quizData = {
    title: title.value,
    description: description.value,
    questions: questions.value,
  };

  try {
    await store.dispatch('quiz/createQuiz', quizData)
    router.push('/library')
  } catch (error) {
    console.error('Error saving quiz:', error)
    alert('Đã có lỗi xảy ra khi lưu bài kiểm tra. Vui lòng thử lại.')
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/create-learn/create-quiz/CreateQuiz.scss';
</style> 