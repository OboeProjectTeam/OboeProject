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
import quizApi from '@/api/modules/quizApi'
import questionApi from '@/api/modules/questionApi'

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

const validateQuiz = () => {
  if (!title.value.trim()) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: 'Vui lòng nhập tên bài kiểm tra.'
    });
    return false;
  }

  if (questions.value.length === 0) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: 'Vui lòng thêm ít nhất một câu hỏi.'
    });
    return false;
  }

  for (let i = 0; i < questions.value.length; i++) {
    const question = questions.value[i];
    if (!question.text.trim()) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: `Vui lòng nhập nội dung cho câu hỏi ${i + 1}.`
      });
      return false;
    }

    const emptyOption = question.options.findIndex(opt => !opt.trim());
    if (emptyOption !== -1) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: `Vui lòng nhập nội dung cho phương án ${emptyOption + 1} của câu hỏi ${i + 1}.`
      });
      return false;
    }
  }

  return true;
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
    console.log('Quiz created successfully:', response);
    
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
      text: 'Tạo bài kiểm tra thành công!'
    });
    
    // Chuyển hướng đến trang thư viện
    router.push('/library');
  } catch (error) {
    console.error('Error creating quiz:', error);
    store.dispatch('message/showMessage', {
      type: 'error',
      text: 'Đã có lỗi xảy ra khi lưu bài kiểm tra: ' + error.message
    });
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/create-learn/create-quiz/CreateQuiz.scss';
</style> 