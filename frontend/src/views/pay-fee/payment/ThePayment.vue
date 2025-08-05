<template>
  <div class="payment-page-container">
    <div class="payment-layout">

      <!-- Left Column: Payment Details -->
      <div class="payment-main-column">
        <div class="form-header">
          <h2 v-if="!isPremium">Thanh toán với PayOS</h2>
          <h2 v-else><i class="fas fa-crown"></i> Tài khoản Premium</h2>
          <p v-if="!isPremium" class="payment-subtitle">Quét mã QR để thanh toán nhanh chóng và an toàn</p>
          <p v-else class="payment-subtitle">Chào mừng bạn đến với trải nghiệm Premium</p>
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
        <div v-else-if="paymentData && !isPremium" class="payment-form">
          <div class="qr-payment-container">
            <div class="qr-header">
              <h3>Quét mã QR để thanh toán</h3>
              <p>Sử dụng ứng dụng ngân hàng hoặc ví điện tử hỗ trợ QR Pay</p>
            </div>
            
            <div class="qr-code-section">
              <div class="qr-code-container">
                <img :src="qrCodeUrl" alt="QR Code thanh toán" class="qr-code-img">
              </div>
              
              <div class="payment-info">
                <div class="info-item">
                  <span class="label">Mã đơn hàng:</span>
                  <span class="value">{{ paymentData.orderCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Số tiền:</span>
                  <span class="value amount">{{ formatAmount(paymentData.amount) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">Trạng thái:</span>
                  <span class="value" :class="getStatusClass(paymentData.status)">{{ getStatusText(paymentData.status) }}</span>
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
        <div v-else-if="!isPremium" class="payment-form">
          <div class="payment-preview">
            <div class="preview-icon">
              <i class="fas fa-qrcode"></i>
            </div>
            <h3>Sẵn sàng thanh toán</h3>
            <p>Nhấn nút "Tạo mã thanh toán" để bắt đầu</p>
          </div>
        </div>

        <!-- Premium User State -->
        <div v-else-if="isPremium" class="payment-form">
          <div class="premium-user-notice">
            <div class="premium-icon">
              <i class="fas fa-crown"></i>
            </div>
            <h3>Bạn đã là thành viên Premium</h3>
            <p>Tài khoản của bạn đã được nâng cấp lên Premium và có thể sử dụng tất cả tính năng cao cấp.</p>
            <div class="premium-features">
              <ul>
                <li><i class="fas fa-check"></i> Truy cập không giới hạn</li>
                <li><i class="fas fa-check"></i> Tính năng AI nâng cao</li>
                <li><i class="fas fa-check"></i> Hỗ trợ ưu tiên</li>
                <li><i class="fas fa-check"></i> Không có quảng cáo</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column: Order Summary -->
      <div class="payment-sidebar-column">
        <div class="order-summary-card">
          <h4 v-if="!isPremium">Tóm tắt đơn hàng</h4>
          <h4 v-else>Thông tin tài khoản</h4>
          
          <div v-if="!isPremium" class="order-summary">
            <div class="summary-item">
              <span>Gói Oboe Pro (Hàng tháng)</span>
              <strong>99.000đ</strong>
            </div>
            <div class="summary-item total">
              <span>Tổng cộng</span>
              <strong class="total-price">99.000đ</strong>
            </div>
          </div>
          
          <div v-else class="premium-summary">
            <div class="premium-status">
              <div class="premium-badge">
                <i class="fas fa-crown"></i>
                <span>PREMIUM</span>
              </div>
              <p class="premium-description">
                Tài khoản của bạn đã được nâng cấp lên Premium với đầy đủ tính năng cao cấp.
              </p>
            </div>
            <div class="premium-info">
              <div class="info-item">
                <span>Trạng thái:</span>
                <strong class="status-active">Đang hoạt động</strong>
              </div>
              <div class="info-item">
                <span>Loại tài khoản:</span>
                <strong>Premium</strong>
              </div>
            </div>
          </div>
          
          <!-- Payment Button -->
          <div v-if="!isPremium" class="payment-actions">
            <button 
              v-if="!paymentData && !loading" 
              @click="createPayment" 
              class="btn-submit"
              :disabled="loading"
            >
              <i class="fas fa-qrcode"></i> Tạo mã thanh toán
            </button>
            <button 
              v-else-if="paymentData && paymentData.status !== 'PENDING'" 
              @click="createNewPayment" 
              class="btn-submit"
              :disabled="loading"
            >
              <i class="fas fa-plus"></i> Tạo thanh toán mới
            </button>
          </div>
          
          <!-- Premium User Actions -->
          <div v-else class="payment-actions">
            <button 
              @click="$router.push('/')" 
              class="btn-submit btn-premium"
            >
              <i class="fas fa-home"></i> Về trang chủ
            </button>
          </div>
          
          <p v-if="!isPremium" class="terms">
            Bằng việc thanh toán, bạn đồng ý với <a href="#">Điều khoản dịch vụ</a>.
          </p>
        </div>
        
        <div class="trust-badges">
          <i v-if="!isPremium" class="fas fa-shield-alt"></i>
          <i v-else class="fas fa-star"></i>
          <span v-if="!isPremium">Thanh toán an toàn với PayOS</span>
          <span v-else>Cảm ơn bạn đã là thành viên Premium</span>
        </div>

        <!-- Payment Status -->
      </div>

    </div>
  </div>

  <!-- Success Popup -->
  <ThePopup
    v-if="showSuccessPopup"
    title="🎉 Thanh toán thành công!"
    :message="'Chúc mừng! Tài khoản của bạn đã được nâng cấp lên Premium.\n\nBạn có thể sử dụng tất cả tính năng cao cấp:\n• Truy cập không giới hạn\n• Tính năng AI nâng cao\n• Hỗ trợ ưu tiên\n• Không có quảng cáo'"
    confirm-text="Về trang chủ"
    :show-cancel="false"
    :use-html="false"
    @confirm="handlePopupConfirm"
    @cancel="handlePopupCancel"
  />
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import paymentApi from '@/api/modules/paymentApi';
import profileApi from '@/api/modules/profileApi';
import ThePopup from '@/components/common/popup/ThePopup.vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Reactive data
const loading = ref(false);
const error = ref(null);
const paymentData = ref(null);
const checking = ref(false);
const paymentStatus = ref(null);
const countdown = ref(0);
const userProfile = ref(null);
const isPremium = ref(false);
const showSuccessPopup = ref(false);

// Auto-check interval
let checkInterval = null;
let countdownInterval = null;

// Computed properties
const qrCodeUrl = computed(() => {
  if (!paymentData.value?.qrUrl) return '';
  // Tạo QR code từ qrUrl bằng API QR generator
  return `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${encodeURIComponent(paymentData.value.qrUrl)}`;
});

// Create payment
const createPayment = async () => {
  // Kiểm tra nếu đã là Premium thì không cho tạo thanh toán
  if (isPremium.value) {
    paymentStatus.value = {
      type: 'success',
      title: 'Tài khoản Premium đã kích hoạt',
      message: 'Bạn đã là thành viên Premium và không cần thanh toán thêm.'
    };
    return;
  }

  try {
    loading.value = true;
    error.value = null;
    paymentStatus.value = null;
    
    const response = await paymentApi.createPayOsPayment();
    paymentData.value = response;
    
    console.log('Payment created:', response);
    
    // Start auto-checking payment status
    startAutoCheck();
    
  } catch (err) {
    console.error('Error creating payment:', err);
    error.value = err.message || 'Không thể tạo mã thanh toán. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
};

// Create new payment (reset current payment)
const createNewPayment = async () => {
  stopAutoCheck();
  paymentData.value = null;
  paymentStatus.value = null;
  await createPayment();
};

// Check payment status
const checkPaymentStatus = async () => {
  if (!paymentData.value?.orderCode) return;
  
  try {
    checking.value = true;
    
    // Gọi API kiểm tra trạng thái thanh toán
    const response = await paymentApi.getPaymentStatus(paymentData.value.orderCode);
    
    // Cập nhật trạng thái payment data
    paymentData.value.status = response.status;
    
    if (response.status === 'PAID') {
      // Thanh toán thành công
      paymentStatus.value = {
        type: 'success',
        title: 'Cảm ơn quý khách đã ủng hộ!',
        message: 'Hiện tại tài khoản quý khách đã là Premium'
      };
      
      // Dừng auto-check khi thanh toán thành công
      stopAutoCheck();
      
      // Xử lý thanh toán thành công
      handlePaymentSuccess();
      
    } else if (response.status === 'CANCELLED') {
      // Thanh toán bị hủy
      paymentStatus.value = {
        type: 'error',
        title: 'Thanh toán đã bị hủy',
        message: 'Giao dịch đã bị hủy. Vui lòng tạo thanh toán mới.'
      };
      stopAutoCheck();
      
    } else if (response.status === 'FAILED') {
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
  
  checkInterval = setInterval(async () => {
    if (paymentData.value?.status === 'PENDING') {
      // Kiểm tra trạng thái thanh toán
      await checkPaymentStatus();
      
      // Đồng thời kiểm tra profile để phát hiện thay đổi Premium
        if (!isPremium.value) {
          try {
            const profile = await profileApi.getProfile();
            if (profile.accountType === 'PREMIUM') {
              // Nếu phát hiện đã là Premium, cập nhật ngay
              userProfile.value = profile;
              isPremium.value = true;
              
              // Hiển thị popup thành công
              showSuccessPopup.value = true;
              stopAutoCheck();
            }
          } catch (err) {
            console.error('Error checking profile during auto-check:', err);
          }
        }
    } else {
      stopAutoCheck();
    }
  }, 3000); // Check every 3 seconds for faster response
};

// Stop auto-checking
const stopAutoCheck = () => {
  if (checkInterval) {
    clearInterval(checkInterval);
    checkInterval = null;
  }
};

// Format amount
const formatAmount = (amount) => {
  if (!amount) return '0đ';
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
};

// Get status text
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': 'Đang chờ thanh toán',
    'PAID': 'Đã thanh toán',
    'CANCELLED': 'Đã hủy',
    'FAILED': 'Thất bại'
  };
  return statusMap[status] || status;
};

// Get status class
const getStatusClass = (status) => {
  const classMap = {
    'PENDING': 'status-pending',
    'PAID': 'status-success',
    'CANCELLED': 'status-cancelled',
    'FAILED': 'status-failed'
  };
  return classMap[status] || '';
};

// Check user profile and Premium status
const checkUserProfile = async () => {
  try {
    loading.value = true;
    const profile = await profileApi.getProfile();
    userProfile.value = profile;
    isPremium.value = profile.accountType === 'PREMIUM';
    
    if (isPremium.value) {
      // Nếu đã là Premium, hiển thị thông báo
      paymentStatus.value = {
        type: 'success',
        title: 'Tài khoản Premium đã kích hoạt',
        message: 'Bạn đã là thành viên Premium và có thể sử dụng tất cả tính năng cao cấp.'
      };
    }
  } catch (err) {
    console.error('Error checking user profile:', err);
    error.value = 'Không thể kiểm tra thông tin tài khoản. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
};

// Handle successful payment
const handlePaymentSuccess = async () => {
  try {
    // Kiểm tra lại profile để cập nhật trạng thái Premium
    const profile = await profileApi.getProfile();
    userProfile.value = profile;
    isPremium.value = profile.accountType === 'PREMIUM';
    
    // Show success popup
    showSuccessPopup.value = true;
    
    // Stop auto-checking
    stopAutoCheck();
    
  } catch (err) {
    console.error('Error updating profile after payment:', err);
    // Still show popup even if profile update fails
    showSuccessPopup.value = true;
    stopAutoCheck();
  }
};

// Handle popup confirm
function handlePopupConfirm() {
  showSuccessPopup.value = false;
  router.push('/');
}

// Handle popup cancel
function handlePopupCancel() {
  showSuccessPopup.value = false;
}

// Lifecycle hooks
onMounted(() => {
  // Kiểm tra trạng thái Premium khi component mount
  checkUserProfile();
});

onUnmounted(() => {
  stopAutoCheck();
  if (countdownInterval) {
    clearInterval(countdownInterval);
  }
});
</script>

<script>
export default {
  components: {
    ThePopup
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/pay-fee/payment/ThePayment.scss'; 
</style>