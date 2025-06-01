<template>
  <div class="create-quiz">
    <h1>Tạo Bài Kiểm Tra Mới</h1>
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
  // TODO: Implement save functionality
  console.log('Saving quiz:', {
    title: title.value,
    description: description.value,
    questions: questions.value
  })
}
</script>

<style lang="scss" scoped>
.create-quiz {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;

  h1 {
    color: #333;
    margin-bottom: 30px;
  }
}

.form-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 20px;

  label {
    display: block;
    margin-bottom: 8px;
    color: #555;
    font-weight: 600;
  }

  input, textarea {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;

    &:focus {
      outline: none;
      border-color: #E94560;
    }
  }

  textarea {
    height: 100px;
    resize: vertical;
  }
}

.questions-container {
  margin-top: 30px;

  h2 {
    color: #444;
    margin-bottom: 20px;
  }
}

.question-item {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  span {
    font-weight: 600;
    color: #666;
  }
}

.question-content {
  > input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;
    margin-bottom: 16px;

    &:focus {
      outline: none;
      border-color: #E94560;
    }
  }
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 12px;

  input[type="radio"] {
    margin: 0;
  }

  input[type="text"] {
    flex: 1;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 14px;

    &:focus {
      outline: none;
      border-color: #E94560;
    }
  }
}

.remove-btn, .remove-option-btn {
  background: none;
  border: none;
  color: #ff4757;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;

  &:hover {
    background: #ffebee;
  }
}

.add-question-btn, .add-option-btn {
  width: 100%;
  padding: 12px;
  background: #f1f1f1;
  border: 2px dashed #ddd;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 12px;

  &:hover {
    background: #e9e9e9;
    border-color: #ccc;
  }
}

.add-option-btn {
  margin-top: 8px;
  padding: 8px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}

.save-btn {
  background: #E94560;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background: #d13651;
  }
}
</style> 