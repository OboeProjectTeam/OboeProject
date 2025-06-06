<template>
  <div class="payment-page-container">
    <div class="payment-layout">

      <!-- Left Column: Payment Details -->
      <div class="payment-main-column">
        <div class="form-header">
          <h2>Thông tin thanh toán</h2>
        </div>
        
        <div class="payment-form">
          <div class="payment-methods">
            <button type="button" class="method-btn" :class="{ active: selectedMethod === 'card' }" @click="selectMethod('card')">
              <i class="fas fa-credit-card"></i> Thẻ
            </button>
            <button type="button" class="method-btn" :class="{ active: selectedMethod === 'paypal' }" @click="selectMethod('paypal')">
              <i class="fab fa-paypal"></i> PayPal
            </button>
            <button type="button" class="method-btn" :class="{ active: selectedMethod === 'momo' }" @click="selectMethod('momo')">
              <i class="fas fa-wallet"></i> Ví MoMo
            </button>
          </div>

          <!-- Card Details Form -->
          <div v-if="selectedMethod === 'card'" class="payment-details">
            <div class="form-group">
              <label for="card-number">Số thẻ</label>
              <div class="input-with-icon">
                <input type="text" id="card-number" placeholder="•••• •••• •••• ••••">
                <i class="far fa-credit-card input-icon"></i>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group half-width">
                <label for="expiry-date">Ngày hết hạn</label>
                <input type="text" id="expiry-date" placeholder="MM / YY">
              </div>
              <div class="form-group half-width">
                <label for="cvc">Mã CVC</label>
                <div class="input-with-icon">
                  <input type="text" id="cvc" placeholder="•••">
                  <i class="fas fa-question-circle input-icon-help"></i>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label for="card-holder">Tên trên thẻ</label>
              <input type="text" id="card-holder" placeholder="NGUYEN VAN A">
            </div>
            <div class="form-group">
              <label for="country">Quốc gia</label>
              <select id="country">
                <option value="vn">Việt Nam</option>
                <option value="us">United States</option>
              </select>
            </div>
          </div>

          <!-- PayPal View -->
          <div v-if="selectedMethod === 'paypal'" class="payment-details paypal-view">
            <p class="external-payment-info">Bạn sẽ được chuyển hướng đến trang PayPal để hoàn tất thanh toán một cách an toàn.</p>
          </div>

          <!-- MoMo View -->
          <div v-if="selectedMethod === 'momo'" class="payment-details momo-view">
            <p class="external-payment-info">Quét mã QR bằng ứng dụng MoMo để hoàn tất thanh toán.</p>
            <div class="qr-code-container">
              <img src="https://upload.wikimedia.org/wikipedia/commons/d/d0/QR_code_for_mobile_English_Wikipedia.svg" alt="QR Code" class="qr-code-img">
            </div>
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
          
          <div v-if="selectedMethod === 'card'">
            <button type="submit" class="btn-submit">
              <i class="fas fa-lock"></i> Thanh toán 99.000đ
            </button>
          </div>
          <div v-if="selectedMethod === 'paypal'">
            <button type="button" class="btn-submit btn-paypal">
              <i class="fab fa-paypal"></i> Tiếp tục với PayPal
            </button>
          </div>
          <div v-if="selectedMethod === 'momo'">
             <p class="momo-instructions">Sử dụng <strong>Ví MoMo</strong> và chọn <strong>"Quét Mã"</strong>.</p>
          </div>
          
          <p class="terms">
            Bằng việc thanh toán, bạn đồng ý với <a href="#">Điều khoản dịch vụ</a>.
          </p>
        </div>
        
        <div class="trust-badges">
          <i class="fas fa-shield-alt"></i>
          <span>Thanh toán an toàn và bảo mật.</span>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const selectedMethod = ref('card'); // 'card', 'paypal', or 'momo'

const selectMethod = (method) => {
  selectedMethod.value = method;
};
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.payment-page-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: calc(100vh - 140px);
  font-family: $font-family-regular;
}

.payment-layout {
  display: flex;
  gap: 30px;
  width: 100%;
  max-width: 1100px;
}

// --- Left Column ---
.payment-main-column {
  border: 1px solid #dfe4e9;
  flex: 2;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px -5px rgba(0,0,0,0.05);
  overflow: hidden;

  .form-header {
    padding: 20px 25px;
    border-bottom: 1px solid #e9ecef;
    h2 {
      margin: 0;
      font-size: 1.5rem;
      font-family: $font-family-bold;
    }
  }

  .payment-form {
    padding: 25px;
  }
}

// --- Right Column ---
.payment-sidebar-column {
  flex: 1;
  position: sticky;
  top: 160px; /* Adjust based on header height */
}

.order-summary-card {
  border: 1px solid #dfe4e9;
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 20px -5px rgba(0,0,0,0.05);

  h4 {
    margin: 0 0 20px 0;
    font-size: 1.2rem;
    font-family: $font-family-bold;
  }
}

.order-summary {
  .summary-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 1rem;
    color: #495057;
    &:not(:last-child) {
      margin-bottom: 12px;
    }
    &.total {
      font-weight: 700;
      font-size: 1.1rem;
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #e9ecef;
    }
    .total-price {
      color: $btn-primary;
      font-size: 1.5rem;
    }
  }
}

// --- Shared & Specific Component Styles ---
.payment-methods {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;

  .method-btn {
    flex: 1;
    padding: 12px;
    border-radius: 8px;
    border: 1px solid #ced4da;
    background-color: #f8f9fa;
    color: #495057;
    cursor: pointer;
    font-weight: 600;
    font-size: 0.9rem;
    transition: all 0.2s ease;
    
    i { margin-right: 8px; }

    &:hover {
      border-color: $btn-primary;
      color: $btn-primary;
    }

    &.active {
      border-color: $btn-primary;
      background-color: color.adjust($btn-primary, $alpha: -0.9);
      color: $btn-primary;
      font-weight: 700;
    }
  }
}

.payment-details {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-group {
  margin-bottom: 20px;
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #343a40;
    font-size: 0.9rem;
  }
  input, select {
    width: 100%;
    padding: 12px 15px;
    border-radius: 6px;
    border: 1px solid #ced4da;
    font-size: 1rem;
    &:focus {
      outline: none;
      border-color: $btn-primary;
      box-shadow: 0 0 0 3px color.adjust($btn-primary, $alpha: -0.7);
    }
  }
}

.form-row {
  display: flex;
  gap: 20px;
  .half-width { width: 50%; }
}

.input-with-icon {
  position: relative;
  .input-icon, .input-icon-help {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #adb5bd;
  }
}

.btn-submit {
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 8px;
  background-color: $btn-primary;
  color: white;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
  i { margin-right: 10px; }
  &:hover {
    background-color: color.adjust($btn-primary, $lightness: -5%);
    transform: translateY(-2px);
  }
}

.btn-paypal {
  background-color: #00457C;
  &:hover { background-color: color.adjust(#00457C, $lightness: -5%); }
}

.external-payment-info {
  text-align: center;
  color: #495057;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.momo-view {
  text-align: center;
}

.qr-code-container {
  padding: 15px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  display: inline-block;
  margin-top: 15px;
  .qr-code-img {
    width: 200px;
    height: 200px;
    display: block;
  }
}

.momo-instructions {
  font-weight: 500;
  color: #343a40;
  text-align: center;
  margin-top: 15px;
}

.terms {
  text-align: center;
  font-size: 0.8rem;
  color: #6c757d;
  margin-top: 20px;
  a {
    color: $btn-primary;
    font-weight: 600;
    text-decoration: none;
    &:hover { text-decoration: underline; }
  }
}

.trust-badges {
  text-align: center;
  margin-top: 20px;
  color: #6c757d;
  font-size: 0.9rem;
  i { margin-right: 8px; color: #28a745; }
}

// Responsive
@media (max-width: 992px) {
  .payment-layout {
    flex-direction: column;
  }
  .payment-sidebar-column {
    position: static;
    top: auto;
  }
}

</style> 