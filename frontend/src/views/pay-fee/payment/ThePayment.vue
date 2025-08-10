<template>
  <div class="payment-page-container">
    <div class="payment-layout">

      <!-- Left Column: Payment Details -->
      <div class="payment-main-column">
        <div class="form-header">
          <h2 v-if="!isPremium">{{ $t('payment.title') }}</h2>
          <h2 v-else><i class="fas fa-crown"></i> {{ $t('payment.premiumAccount') }}</h2>
          <p v-if="!isPremium" class="payment-subtitle">{{ $t('payment.subtitle') }}</p>
          <p v-else class="payment-subtitle">{{ $t('payment.premiumWelcome') }}</p>
        </div>
        
        <!-- Loading State -->
        <div v-if="loading" class="payment-loading">
          <div class="loading-spinner"></div>
          <p>{{ $t('payment.creatingPayment') }}</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="payment-error">
          <div class="error-icon">⚠️</div>
          <h3>{{ $t('payment.errorOccurred') }}</h3>
          <p>{{ error }}</p>
          <button @click="createPayment" class="btn-retry">
            <i class="fas fa-redo"></i> {{ $t('payment.retry') }}
          </button>
        </div>

        <!-- Payment QR Code -->
        <div v-else-if="paymentData && !isPremium" class="payment-form">
          <div class="qr-payment-container">
            <div class="qr-header">
              <h3>{{ $t('payment.scanQR') }}</h3>
              <p>{{ $t('payment.qrInstructions') }}</p>
            </div>
            
            <div class="qr-code-section">
              <div class="qr-code-container">
                <img :src="qrCodeUrl" alt="QR Code thanh toán" class="qr-code-img">
              </div>
              
              <div class="payment-info">
                <div class="info-item">
                  <span class="label">{{ $t('payment.bank') }}:</span>
                  <span class="value">{{ $t('payment.bankName') }}</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ $t('payment.accountHolder') }}:</span>
                  <span class="value">NGUYEN HUU NGHIA</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ $t('payment.accountNumber') }}:</span>
                  <span class="value">VQRQADQDG0146</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ $t('payment.orderCode') }}:</span>
                  <span class="value">{{ paymentData.orderCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ $t('payment.amount') }}:</span>
                  <span class="value amount">{{ formatAmount(paymentData.amount) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ $t('payment.status') }}:</span>
                  <span class="value" :class="getStatusClass(paymentData.status)">{{ getStatusText(paymentData.status) }}</span>
                </div>
                
              </div>
            </div>

            <div class="payment-instructions">
              <h4>{{ $t('payment.instructionsTitle') }}:</h4>
              <ol>
                <li>{{ $t('payment.instruction1') }}</li>
                <li>{{ $t('payment.instruction2') }}</li>
                <li>{{ $t('payment.instruction3') }}</li>
                <li>{{ $t('payment.instruction4') }}</li>
              </ol>
            </div>
            <div class="alternative-payment">
              <p>{{ $t('payment.alternativeText') }}:</p>
              <a :href="paymentData.checkoutUrl" target="_blank" class="btn-checkout">
                <i class="fas fa-external-link-alt"></i>
                {{ $t('payment.payOnWebsite') }}
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
            <h3>{{ $t('payment.readyToPay') }}</h3>
            <p>{{ $t('payment.clickToStart') }}</p>
          </div>
        </div>

        <!-- Premium User State -->
        <div v-else-if="isPremium" class="payment-form">
          <div class="premium-user-notice">
            <div class="premium-icon">
              <i class="fas fa-crown"></i>
            </div>
            <h3>{{ $t('payment.alreadyPremium') }}</h3>
            <p>{{ $t('payment.premiumDescription') }}</p>
            <div class="premium-features">
              <ul>
                <li><i class="fas fa-check"></i> {{ $t('payment.feature1') }}</li>
                <li><i class="fas fa-check"></i> {{ $t('payment.feature2') }}</li>
                <li><i class="fas fa-check"></i> {{ $t('payment.feature3') }}</li>
                <li><i class="fas fa-check"></i> {{ $t('payment.feature4') }}</li>
                <li><i class="fas fa-check"></i> {{ $t('payment.feature5') }}</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column: Order Summary -->
      <div class="payment-sidebar-column">
        <div class="order-summary-card">
          <h4 v-if="!isPremium">{{ $t('payment.orderSummary') }}</h4>
          <h4 v-else>{{ $t('payment.accountInfo') }}</h4>
          
          <div v-if="!isPremium" class="order-summary">
            <div class="summary-item">
              <span>{{ $t('payment.oboePro') }}</span>
              <strong>{{ $t('payment.monthlyPrice') }}</strong>
            </div>
            <div class="summary-item total">
              <span>{{ $t('payment.total') }}</span>
              <strong class="total-price">{{ $t('payment.monthlyPrice') }}</strong>
            </div>
          </div>
          
          <div v-else class="premium-summary">
            <div class="premium-status">
              <div class="premium-badge">
                <i class="fas fa-crown"></i>
                <span>{{ $t('payment.premium') }}</span>
              </div>
              <p class="premium-description">
                {{ $t('payment.premiumAccountDescription') }}
              </p>
            </div>
            <div class="premium-info">
              <div class="info-item">
                <span>{{ $t('payment.statusLabel') }}:</span>
                <strong class="status-active">{{ $t('payment.active') }}</strong>
              </div>
              <div class="info-item">
                <span>{{ $t('payment.accountType') }}:</span>
                <strong>{{ $t('payment.premium') }}</strong>
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
              <i class="fas fa-qrcode"></i> {{ $t('payment.createPayment') }}
            </button>
            <button 
              v-else-if="paymentData && paymentData.status !== 'PENDING'" 
              @click="createNewPayment" 
              class="btn-submit"
              :disabled="loading"
            >
              <i class="fas fa-plus"></i> {{ $t('payment.createNew') }}
            </button>
          </div>
          
          <!-- Premium User Actions -->
          <div v-else class="payment-actions">
            <button 
              @click="$router.push('/')" 
              class="btn-submit btn-premium"
            >
              <i class="fas fa-home"></i> {{ $t('payment.goHome') }}
            </button>
          </div>
          
          <p v-if="!isPremium" class="terms">
            {{ $t('payment.termsText') }} <a href="#">{{ $t('payment.termsOfService') }}</a>.
          </p>
        </div>
        
        <div class="trust-badges">
          <i v-if="!isPremium" class="fas fa-shield-alt"></i>
          <i v-else class="fas fa-star"></i>
          <span v-if="!isPremium">{{ $t('payment.securePayment') }}</span>
          <span v-else>{{ $t('payment.thanksPremium') }}</span>
        </div>

        <!-- Payment Status -->
      </div>

    </div>
  </div>

  <!-- Success Popup -->
  <ThePopup
    v-if="showSuccessPopup"
    :title="$t('payment.successTitle')"
    :message="$t('payment.successMessage')"
    :confirm-text="$t('payment.goHome')"
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
import { useStore } from 'vuex';
import { useGlobalPremiumSync } from '@/composables/useGlobalPremiumSync';
import { useI18n } from 'vue-i18n';

const router = useRouter();
const store = useStore();
const { requestPremiumSync } = useGlobalPremiumSync();
const { t } = useI18n();

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
      title: t('payment.premiumActivated'),
      message: t('payment.alreadyPremiumMessage')
    };
    return;
  }

  try {
    loading.value = true;
    error.value = null;
    paymentStatus.value = null;
    
    const response = await paymentApi.createPayOsPayment();
    paymentData.value = response;
    // Start auto-checking payment status
    startAutoCheck();
    
  } catch (err) {
    console.error('Error creating payment:', err);
    error.value = err.message || t('payment.createError');
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
        title: t('payment.thankYou'),
        message: t('payment.accountUpgraded')
      };
      
      // Dừng auto-check khi thanh toán thành công
      stopAutoCheck();
      
      // Xử lý thanh toán thành công
      handlePaymentSuccess();
      
    } else if (response.status === 'CANCELLED') {
      // Thanh toán bị hủy
      paymentStatus.value = {
        type: 'error',
        title: t('payment.paymentCancelled'),
        message: t('payment.transactionCancelled')
      };
      stopAutoCheck();
      
    } else if (response.status === 'FAILED') {
      // Thanh toán thất bại
      paymentStatus.value = {
        type: 'error',
        title: t('payment.paymentFailed'),
        message: t('payment.transactionFailed')
      };
      stopAutoCheck();
      
    } else {
      // Đang chờ thanh toán
      paymentStatus.value = {
        type: 'pending',
        title: t('payment.waitingPayment'),
        message: t('payment.completePayment')
      };
    }
    
  } catch (err) {
    console.error('Error checking payment status:', err);
    paymentStatus.value = {
      type: 'error',
      title: t('payment.checkError'),
      message: t('payment.checkErrorMessage')
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
             const profile = await requestPremiumSync();
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
    'PENDING': t('payment.status.pending'),
    'PAID': t('payment.status.paid'),
    'CANCELLED': t('payment.status.cancelled'),
    'FAILED': t('payment.status.failed')
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
    const profile = await requestPremiumSync();
    userProfile.value = profile;
    isPremium.value = profile.accountType === 'PREMIUM';
    
    if (isPremium.value) {
      // Nếu đã là Premium, hiển thị thông báo
      paymentStatus.value = {
        type: 'success',
        title: t('payment.premiumActivated'),
        message: t('payment.premiumActivatedMessage')
      };
    }
  } catch (err) {
    console.error('Error checking user profile:', err);
    error.value = t('payment.profileCheckError');
  } finally {
    loading.value = false;
  }
};

// Handle successful payment
const handlePaymentSuccess = async () => {
  try {
    // Sử dụng global premium sync để cập nhật toàn bộ app
    const profile = await requestPremiumSync();
    
    // Cập nhật trạng thái Premium local
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
  },
  emits: ['sendMessage']
}
</script>

<style lang="scss" scoped>
@use '@/views/pay-fee/payment/ThePayment.scss'; 
</style>