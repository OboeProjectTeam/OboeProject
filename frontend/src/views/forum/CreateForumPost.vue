<template>
  <div class="create-post-container">
    <div class="create-post-card">
      <div class="card-header">
        <h1>Tạo bài viết mới</h1>
        <p>Chia sẻ kiến thức và câu hỏi của bạn với cộng đồng</p>
      </div>
      <div class="card-body">
        <form @submit.prevent>
          <div class="form-group">
            <label for="post-title">Tiêu đề</label>
            <input type="text" id="post-title" placeholder="Nhập tiêu đề hấp dẫn cho bài viết của bạn...">
          </div>
          <div class="form-group">
            <label for="post-content">Nội dung</label>
            <textarea id="post-content" rows="12" placeholder="Viết nội dung chi tiết ở đây. Bạn có thể sử dụng markdown để định dạng."></textarea>
          </div>
          <div class="form-group">
            <label for="post-tags">Tags (phân cách bởi dấu phẩy)</label>
            <input type="text" id="post-tags" placeholder="Ví dụ: kanji, jlpt, n3, giao tiếp...">
          </div>
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="goBackToForum">Hủy</button>
            <button type="submit" class="btn btn-primary">Đăng bài</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goBackToForum = () => {
  router.push('/forum');
};
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.create-post-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: $font-family-regular;
}

.create-post-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 25px -10px rgba(0,0,0,0.08);
  border: 1px solid #e9ecef;
  overflow: hidden;
}

.card-header {
  padding: 25px 30px;
  border-bottom: 1px solid #e9ecef;
  background-color: #f8f9fa;
  h1 {
    font-size: 1.8rem;
    font-family: $font-family-bold;
    margin: 0 0 5px 0;
  }
  p {
    margin: 0;
    color: #6c757d;
  }
}

.card-body {
  padding: 30px;
}

.form-group {
  margin-bottom: 25px;
  label {
    display: block;
    font-weight: 600;
    color: #343a40;
    margin-bottom: 8px;
  }
  input[type="text"], textarea {
    width: 100%;
    padding: 12px 15px;
    border-radius: 8px;
    border: 1px solid #ced4da;
    font-size: 1rem;
    font-family: inherit;
    transition: all 0.2s ease;
    &:focus {
      outline: none;
      border-color: $btn-primary;
      box-shadow: 0 0 0 3px color.adjust($btn-primary, $alpha: -0.7);
    }
  }
  textarea {
    resize: vertical;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 10px;
  
  .btn {
    padding: 10px 25px;
    font-size: 1rem;
    font-weight: 600;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .btn-primary {
    background-color: $btn-primary;
    color: white;
    &:hover {
      background-color: color.adjust($btn-primary, $lightness: -5%);
    }
  }

  .btn-secondary {
    background-color: #e9ecef;
    color: #495057;
     &:hover {
      background-color: #dee2e6;
    }
  }
}
</style> 