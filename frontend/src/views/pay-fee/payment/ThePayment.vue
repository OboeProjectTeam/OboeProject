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
            <p class="external-payment-info"> sẽ được chuyển hướng đến trang PayPal để hoàn tất thanh toán một cách an toàn.</p>
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
            Bằng việc thanh toán,  đồng ý với <a href="#">Điều khoản dịch vụ</a>.
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
@use '@/views/pay-fee/payment/ThePayment.scss'; 
</style> 