<template>
  <div class="contact-page">
    <div class="contact-header">
      <h1 class="main-title">Liên Hệ Chúng Tôi</h1>
      <p class="subtitle">Chúng tôi luôn sẵn sàng lắng nghe và hỗ trợ bạn 24/7</p>
    </div>

    <div class="contact-content">
      <!-- Contact Information -->
      <div class="contact-info">
        <div class="info-card">
          <i class="fas fa-phone-alt"></i>
          <h3>Điện thoại</h3>
          <p>0775751954</p>
          <p class="time">8:00 - 24:00 (Thứ 2 - Chủ nhật)</p>
        </div>

        <div class="info-card">
          <i class="fas fa-envelope"></i>
          <h3>Email</h3>
          <p>bophanchamsoc@oboe.com</p>
          <p class="time">Phản hồi trong vòng 24h</p>
        </div>

        <div class="info-card">
          <i class="fas fa-map-marker-alt"></i>
          <h3>Địa chỉ</h3>
          <p>Hoài Đức, Hà Nội, Việt Nam</p>
          <p class="time">Văn phòng chính</p>
        </div>
      </div>

      <!-- Contact Form -->
      <div class="contact-form">
        <h2>Gửi tin nhắn cho chúng tôi</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label for="name">Họ và tên</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name"
              placeholder="Nhập họ và tên của bạn"
              required
            >
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email"
              placeholder="Nhập địa chỉ email của bạn"
              required
            >
          </div>

          <div class="form-group">
            <label for="title">Tiêu đề</label>
            <input 
              type="text" 
              id="title" 
              v-model="formData.title"
              placeholder="Nhập tiêu đề tin nhắn"
              required
            >
          </div>

          <div class="form-group">
            <label for="subject">Chủ đề</label>
            <select id="subject" v-model="formData.subject" required>
              <option value="">Chọn chủ đề</option>
              <option value="support">Hỗ trợ kỹ thuật</option>
              <option value="billing">Vấn đề thanh toán</option>
              <option value="feedback">Góp ý sản phẩm</option>
              <option value="other">Khác</option>
            </select>
          </div>

          <div class="form-group">
            <label for="message">Nội dung tin nhắn</label>
            <textarea 
              id="message" 
              v-model="formData.message"
              rows="5"
              placeholder="Nhập nội dung tin nhắn của bạn"
              required
            ></textarea>
          </div>

          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? 'Đang gửi...' : 'Gửi tin nhắn' }}
          </button>
        </form>
      </div>
    </div>

    <!-- Social Media Links -->
    <div class="social-links">
      <h2>Kết nối với chúng tôi</h2>
      <div class="social-icons">
        <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-facebook"></i>
        </a>
        <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-twitter"></i>
        </a>
        <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-instagram"></i>
        </a>
        <a href="https://youtube.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-youtube"></i>
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const isSubmitting = ref(false);
const formData = ref({
  name: '',
  email: '',
  subject: '',
  title: '',
  message: ''
});

const submitForm = async () => {
  try {
    isSubmitting.value = true;
    // Giả lập gửi form
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // Reset form
    formData.value = {
      name: '',
      email: '',
      subject: '',
      title: '',
      message: ''
    };
    
    alert('Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.');
  } catch (error) {
    console.error('Error submitting form:', error);
    alert('Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau!');
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/contact/TheContact.scss';
</style> 