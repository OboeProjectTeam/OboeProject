<template>
  <div class="payment-page-container">
    <div class="payment-layout">

      <!-- Left Column: Payment Details -->
      <div class="payment-main-column">
        <div class="form-header">
          <h2>Thanh toán với PayOS</h2>
          <p class="payment-subtitle">Quét mã QR để thanh toán nhanh chóng và an toàn</p>
        </div>
        
        <!-- Loading State -->
        <div v-if="loading" class="payment-loading">
          <div class="loading-spinner"></div>
          <p>Đang tạo mã thanh toán...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="payment-error">
          <div class="error-icon">⚠️</div>
          <h3>Có lỗi xảy ra</h3>
          <p>{{ error }}</p>
          <button @click="createPayment" class="btn-retry">
            <i class="fas fa-redo"></i> Thử lại
          </button>
        </div>

        <!-- Payment QR Code -->
        <div v-else-if="paymentData" class="payment-form">
          <div class="qr-payment-container">
            <div class="qr-header">
              <h3>Quét mã QR để thanh toán</h3>
              <p>Sử dụng ứng dụng ngân hàng hoặc ví điện tử hỗ trợ QR Pay</p>
            </div>
            
            <div class="qr-code-section">
              <div class="qr-code-container">
                <img :src="paymentData.qrUrl" alt="QR Code thanh toán" class="qr-code-img">
              </div>
              
              <div class="payment-info">
                <div class="info-item">
                  <span class="label">Mã đơn hàng:</span>
                  <span class="value">{{ paymentData.orderCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Số tiền:</span>
                  <span class="value amount">99.000đ</span>
                </div>
                <div class="info-item" v-if="paymentData.expiredAt">
                  <span class="label">Hết hạn:</span>
                  <span class="value">{{ formatExpiredTime(paymentData.expiredAt) }}</span>
                </div>
              </div>
            </div>

            <div class="payment-instructions">
              <h4>Hướng dẫn thanh toán:</h4>
              <ol>
                <li>Mở ứng dụng ngân hàng hoặc ví điện tử trên điện thoại</li>
                <li>Chọn chức năng "Quét mã QR" hoặc "QR Pay"</li>
                <li>Quét mã QR phía trên</li>
                <li>Xác nhận thông tin và hoàn tất thanh toán</li>
              </ol>
            </div>

            <div class="alternative-payment">
              <p>Hoặc bạn có thể:</p>
              <a :href="paymentData.checkoutUrl" target="_blank" class="btn-checkout">
                <i class="fas fa-external-link-alt"></i>
                Thanh toán trên trang web
              </a>
            </div>
          </div>
        </div>

        <!-- Initial State -->
        <div v-else class="payment-form">
          <div class="payment-preview">
            <div class="preview-icon">
              <i class="fas fa-qrcode"></i>
            </div>
            <h3>Sẵn sàng thanh toán</h3>
            <p>Nhấn nút "Tạo mã thanh toán" để bắt đầu</p>
          </div>
        </div>
      </div>

      <!-- Right Column: Order Summary -->
      <div class="payment-sidebar-column">
        <div class="order-summary-card">
          <h4>Tóm tắt đơn hàng</h4>
          <div class="order-summary">
            <div class="summary-item">
              <span>Gói Oboe Pro (Hàng tháng)</span>
              <strong>99.000đ</strong>
            </div>
            <div class="summary-item total">
              <span>Tổng cộng</span>
              <strong class="total-price">99.000đ</strong>
            </div>
          </div>
          
          <!-- Payment Button -->
          <div class="payment-actions">
            <button 
              v-if="!paymentData && !loading" 
              @click="createPayment" 
              class="btn-submit"
              :disabled="loading"
            >
              <i class="fas fa-qrcode"></i> Tạo mã thanh toán
            </button>
            
            <button 
              v-else-if="paymentData" 
              @click="checkPaymentStatus" 
              class="btn-submit btn-check"
              :disabled="checking"
            >
              <i class="fas fa-sync" :class="{ 'fa-spin': checking }"></i> 
              {{ checking ? 'Đang kiểm tra...' : 'Kiểm tra thanh toán' }}
            </button>
          </div>
          
          <p class="terms">
            Bằng việc thanh toán, bạn đồng ý với <a href="#">Điều khoản dịch vụ</a>.
          </p>
        </div>
        
        <div class="trust-badges">
          <i class="fas fa-shield-alt"></i>
          <span>Thanh toán an toàn với PayOS</span>
        </div>

        <!-- Payment Status -->
        <div v-if="paymentStatus" class="payment-status" :class="paymentStatus.type">
          <div class="status-icon">
            <i v-if="paymentStatus.type === 'success'" class="fas fa-check-circle"></i>
            <i v-else-if="paymentStatus.type === 'error'" class="fas fa-times-circle"></i>
            <i v-else class="fas fa-clock"></i>
          </div>
          <div class="status-content">
            <h4>{{ paymentStatus.title }}</h4>
            <p>{{ paymentStatus.message }}</p>
            <div v-if="paymentStatus.type === 'success' && countdown > 0" class="countdown">
              <p><i class="fas fa-clock"></i> Tự động chuyển hướng sau {{ countdown }} giây...</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import paymentApi from '@/api/modules/paymentApi';

// Reactive data
const loading = ref(false);
const error = ref(null);
const paymentData = ref(null);
const checking = ref(false);
const paymentStatus = ref(null);
const countdown = ref(0);

// Auto-check interval
let checkInterval = null;
let countdownInterval = null;

// Create payment
const createPayment = async () => {
  try {
    loading.value = true;
    error.value = null;
    paymentStatus.value = null;
    
    const response = await paymentApi.createPayOsPayment(99000);
    paymentData.value = response;
    
    // Start auto-checking payment status
    startAutoCheck();
    
  } catch (err) {
    console.error('Error creating payment:', err);
    error.value = err.message || 'Không thể tạo mã thanh toán. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
};

// Check payment status
const checkPaymentStatus = async () => {
  if (!paymentData.value?.orderCode) return;
  
  try {
    checking.value = true;
    
    // Gọi API kiểm tra trạng thái thanh toán
    const response = await paymentApi.checkPayOsPaymentStatus(paymentData.value.orderCode);
    
    if (response.transactionStatus === 'SUCCESS' && response.code === '000') {
      // Thanh toán thành công
      paymentStatus.value = {
        type: 'success',
        title: 'Thanh toán thành công!',
        message: `Đơn hàng ${response.orderCode} đã được thanh toán thành công với số tiền ${response.amount.toLocaleString('vi-VN')} VNĐ.`
      };
      
      // Dừng auto-check khi thanh toán thành công
       stopAutoCheck();
       
       // Xử lý thanh toán thành công
       handlePaymentSuccess();
      
    } else if (response.transactionStatus === 'FAILED') {
      // Thanh toán thất bại
      paymentStatus.value = {
        type: 'error',
        title: 'Thanh toán thất bại',
        message: 'Giao dịch không thành công. Vui lòng thử lại.'
      };
      stopAutoCheck();
      
    } else {
      // Đang chờ thanh toán
      paymentStatus.value = {
        type: 'pending',
        title: 'Đang chờ thanh toán',
        message: 'Vui lòng hoàn tất thanh toán để tiếp tục.'
      };
    }
    
  } catch (err) {
    console.error('Error checking payment status:', err);
    paymentStatus.value = {
      type: 'error',
      title: 'Lỗi kiểm tra thanh toán',
      message: 'Không thể kiểm tra trạng thái thanh toán. Vui lòng thử lại.'
    };
  } finally {
    checking.value = false;
  }
};

// Start auto-checking payment status
const startAutoCheck = () => {
  if (checkInterval) {
    clearInterval(checkInterval);
  }
  
  checkInterval = setInterval(() => {
    checkPaymentStatus();
  }, 10000); // Check every 10 seconds
};

// Stop auto-checking
const stopAutoCheck = () => {
  if (checkInterval) {
    clearInterval(checkInterval);
    checkInterval = null;
  }
};

// Format expired time
const formatExpiredTime = (expiredAt) => {
  if (!expiredAt) return '';
  
  try {
    const date = new Date(expiredAt);
    return date.toLocaleString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (err) {
    return expiredAt;
  }
};

// Handle successful payment
const handlePaymentSuccess = () => {
  // Có thể thêm logic lưu trạng thái thanh toán vào store
  // hoặc gọi API để cập nhật thông tin người dùng
  
  // Bắt đầu countdown
  countdown.value = 3;
  countdownInterval = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(countdownInterval);
      // Redirect về trang dashboard hoặc trang thành công
      // Bạn có thể thay đổi đường dẫn này theo nhu cầu
      window.location.href = '/dashboard';
    }
  }, 1000);
};

// Lifecycle hooks
onMounted(() => {
  // Auto-create payment when component mounts
  // createPayment();
});

onUnmounted(() => {
  stopAutoCheck();
  if (countdownInterval) {
    clearInterval(countdownInterval);
  }
});
</script>

<style lang="scss" scoped>
@use '@/views/pay-fee/payment/ThePayment.scss'; 
</style>