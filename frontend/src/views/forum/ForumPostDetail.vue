<template>
  <div class="post-detail-container">
    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <a href="#" @click.prevent="goBackToForum">Diễn đàn</a>
      <i class="fas fa-chevron-right separator"></i>
      <span>Chi tiết bài viết</span>
    </div>

    <!-- Post Header -->
    <div class="post-header">
      <h1 class="post-title">Thảo luận về cách học Kanji hiệu quả cho người mới bắt đầu</h1>
      <div class="post-meta">
        <img src="https://i.pravatar.cc/150?u=a042581f4e29026704d" alt="Mai An" class="author-avatar">
        <div class="meta-info">
          <span class="author-name">Mai An</span>
          <span class="post-time">Đăng 2 giờ trước</span>
        </div>
      </div>
    </div>

    <!-- Original Post Content -->
    <div class="post-content-card">
      <p>Chào mọi người,</p>
      <p>Mình là người mới bắt đầu học tiếng Nhật và đang gặp khá nhiều khó khăn với Kanji. Dù đã thử qua nhiều phương pháp như viết tay, dùng flashcard, học theo bộ thủ nhưng vẫn cảm thấy rất rối và khó nhớ.</p>
      <p>Mọi người có kinh nghiệm hay phương pháp nào học Kanji hiệu quả, đặc biệt là cho người mới, thì chia sẻ cho mình và mọi người cùng biết với ạ. Ví dụ như:</p>
      <ul>
        <li>Nên bắt đầu từ những bộ Kanji nào?</li>
        <li>Có app hay website nào hỗ trợ học tốt không?</li>
        <li>Cách để ôn tập và tránh quên Kanji đã học?</li>
      </ul>
      <p>Cảm ơn mọi người rất nhiều!</p>
    </div>

    <!-- Replies Section -->
    <div class="replies-section">
      <h2 class="replies-header">15 Trả lời</h2>
      
      <div class="reply-list">
        <!-- Reply Item -->
        <div v-for="reply in replies" :key="reply.id" class="reply-item">
          <img :src="reply.author.avatar" :alt="reply.author.name" class="author-avatar">
          <div class="reply-content-wrapper">
            <div class="reply-meta">
              <span class="author-name">{{ reply.author.name }}</span>
              <span class="post-time">{{ reply.time }}</span>
            </div>
            <div class="reply-content">
              <p>{{ reply.text }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Add a Reply -->
    <div class="add-reply-card">
       <h3 class="add-reply-header">Tham gia thảo luận</h3>
       <div class="reply-form">
        <textarea class="reply-textarea" placeholder="Viết bình luận của bạn..."></textarea>
        <button class="btn btn-primary submit-reply-btn">Gửi trả lời</button>
       </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goBackToForum = () => {
  router.push('/forum');
};

// Mock data for replies
const replies = ref([
  {
    id: 1,
    author: { name: 'Hùng Trần', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026705d' },
    time: '1 giờ 45 phút trước',
    text: 'Mình thấy học theo sách "Look & Learn" khá hiệu quả, hình ảnh minh họa dễ nhớ lắm bạn. Kết hợp với app Anki để ôn tập mỗi ngày nữa là ổn.'
  },
  {
    id: 2,
    author: { name: 'Lan Anh', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026707d' },
    time: '1 giờ 20 phút trước',
    text: 'Đúng rồi, học theo bộ thủ là cách nhớ lâu nhất đó. Bạn thử tìm hiểu về 214 bộ thủ cơ bản trước, sau đó ghép Kanji sẽ thấy logic hơn nhiều.'
  },
   {
    id: 3,
    author: { name: 'Duy Mạnh', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026709d' },
    time: '30 phút trước',
    text: 'Cứ bình tĩnh bạn ơi, ai mới học cũng vậy thôi. Quan trọng là kiên trì, mỗi ngày 5-10 chữ thôi cũng được, mưa dầm thấm lâu. Cố lên!'
  }
]);
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.post-detail-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px 20px 40px 20px;
  font-family: $font-family-regular;
}

.breadcrumb {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  font-size: 0.95rem;

  a {
    color: $btn-primary;
    text-decoration: none;
    font-weight: 600;
    &:hover {
      text-decoration: underline;
    }
  }

  .separator {
    margin: 0 10px;
    font-size: 0.8rem;
    color: #adb5bd;
  }

  span {
    color: #6c757d;
  }
}

.post-header {
  margin-bottom: 25px;
  .post-title {
    font-size: 2.5rem;
    font-family: $font-family-bold;
    color: #212529;
    line-height: 1.3;
    margin: 0 0 15px 0;
  }
  .post-meta {
    display: flex;
    align-items: center;
    .author-avatar {
      width: 45px;
      height: 45px;
      border-radius: 50%;
      margin-right: 12px;
    }
    .meta-info {
      display: flex;
      flex-direction: column;
      .author-name {
        font-weight: 600;
        color: #343a40;
      }
      .post-time {
        font-size: 0.9rem;
        color: #6c757d;
      }
    }
  }
}

.post-content-card, .add-reply-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 25px -10px rgba(0,0,0,0.08);
  border: 1px solid #e9ecef;
  margin-bottom: 30px;
}

.post-content-card {
  font-size: 1.1rem;
  line-height: 1.7;
  color: #343a40;
  p {
    margin: 0 0 1rem 0;
  }
  ul {
    padding-left: 20px;
    margin-bottom: 1rem;
    li {
      margin-bottom: 0.5rem;
    }
  }
}

.replies-section {
  .replies-header {
    font-size: 1.5rem;
    font-family: $font-family-bold;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #dee2e6;
  }
}

.reply-list {
  .reply-item {
    display: flex;
    padding: 20px 0;
    &:not(:last-child) {
      border-bottom: 1px solid #e9ecef;
    }
    .author-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      margin-right: 15px;
      flex-shrink: 0;
    }
    .reply-content-wrapper {
      flex-grow: 1;
      .reply-meta {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        .author-name {
          font-weight: 700;
          color: #343a40;
          margin-right: 10px;
        }
        .post-time {
          font-size: 0.85rem;
          color: #868e96;
        }
      }
      .reply-content {
        color: #495057;
        line-height: 1.6;
        p { margin: 0; }
      }
    }
  }
}

.add-reply-card {
  .add-reply-header {
    font-size: 1.2rem;
    font-weight: 700;
    margin: 0 0 15px 0;
  }
  .reply-textarea {
    width: 100%;
    min-height: 120px;
    padding: 15px;
    border-radius: 8px;
    border: 1px solid #ced4da;
    font-size: 1rem;
    font-family: inherit;
    resize: vertical;
    margin-bottom: 15px;
    &:focus {
      outline: none;
      border-color: $btn-primary;
      box-shadow: 0 0 0 3px color.adjust($btn-primary, $alpha: -0.7);
    }
  }
  .submit-reply-btn {
    padding: 10px 20px;
    font-size: 1rem;
    font-weight: 600;
    border-radius: 8px;
    background-color: $btn-primary;
    color: white;
    border: none;
    cursor: pointer;
    transition: background-color 0.2s ease;
    &:hover {
      background-color: color.adjust($btn-primary, $lightness: -5%);
    }
  }
}

</style> 